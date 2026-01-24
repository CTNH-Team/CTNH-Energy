package tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.AutoStockingFancyConfigurator;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.integration.ae2.machine.MEInputBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.feature.multiblock.IMEStockingPart;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemList;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemSlot;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAESlot;
import com.gregtechceu.gtceu.integration.ae2.slot.IConfigurableSlotList;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.DropSaved;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import tech.vixhentx.mcmod.ctnhlib.client.gui.IRCFancyUIProvider;
import tech.vixhentx.mcmod.ctnhlib.client.gui.RCUIWidget;
import tech.vixhentx.mcmod.ctnhlib.client.gui.RightConfiguratorPanel;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEStockingBusPartMachine extends MEInputBusPartMachine implements IMEStockingPart, IRCFancyUIProvider {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MEStockingBusPartMachine.class, MEInputBusPartMachine.MANAGED_FIELD_HOLDER);

    @DescSynced
    @Persisted
    @Getter
    private boolean autoPull;

    @Getter
    @Setter
    @Persisted
    @DropSaved
    private int minStackSize = 1;
    @Getter
    @Setter
    @Persisted
    @DropSaved
    private int ticksPerCycle = 40;

    @Setter
    private Predicate<GenericStack> autoPullTest;

    private enum RefreshState {
        CLEAN,
        NEED_REBUILD
    }

    private RefreshState refreshState = RefreshState.CLEAN;

    private static final class CachedStock {
        final AEKey key;
        long amount;

        CachedStock(AEKey key, long amount) {
            this.key = key;
            this.amount = amount;
        }
    }

    private CachedStock[] cachedStocks = new CachedStock[0];

    public MEStockingBusPartMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.autoPullTest = $ -> false;
    }

    /////////////////////////////////
    // ***** Machine LifeCycle ****//
    /////////////////////////////////

    @Override
    public void addedToController(IMultiController controller) {
        super.addedToController(controller);
        IMEStockingPart.super.addedToController(controller);
    }

    @Override
    public void removedFromController(IMultiController controller) {
        IMEStockingPart.super.removedFromController(controller);
        super.removedFromController(controller);
    }

    @Override
    protected NotifiableItemStackHandler createInventory(Object... args) {
        this.aeItemHandler = new ExportOnlyAEStockingItemList(this, CONFIG_SIZE);
        return this.aeItemHandler;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    /////////////////////////////////
    // ********** Sync ME *********//
    /////////////////////////////////

    private int rebuildCooldown = 0;

    @Override
    public void autoIO() {
        super.autoIO();

        if (!autoPull) return;

        if (ticksPerCycle == 0) {
            ticksPerCycle = ConfigHolder.INSTANCE.compat.ae2.updateIntervals;
        }

        if (getOffsetTimer() % ticksPerCycle != 0) {
            return;
        }

        if (--rebuildCooldown <= 0) {
            refreshState = RefreshState.NEED_REBUILD;
            rebuildCooldown = 2;
        }

        if (refreshState == RefreshState.NEED_REBUILD) {
            rebuildCache();
            applyCacheToSlots();
            refreshState = RefreshState.CLEAN;
        }

        syncME();
    }

    @Override
    protected void syncME() {
        if (cachedStocks.length == 0) return;

        MEStorage storage = getMainNode().getGrid()
                .getStorageService().getInventory();

        var inv = aeItemHandler.getInventory();

        for (int i = 0; i < cachedStocks.length; i++) {
            CachedStock c = cachedStocks[i];

            long available = storage.extract(
                    c.key, Long.MAX_VALUE, Actionable.SIMULATE, actionSource
            );

            if (available >= minStackSize) {
                c.amount = available;
                inv[i].setStock(new GenericStack(c.key, available));
            } else {
                inv[i].setStock(null);
            }
        }
    }

    @Override
    public void attachSideTabs(TabsWidget sideTabs) {
        sideTabs.setMainTab(this); // removes the cover configurator, it's pointless and clashes with layout.
    }

    @Override
    protected void flushInventory() {
        // no-op, nothing to send back to the network
    }

    @Override
    public void setDistinct(boolean isDistinct) {
        super.setDistinct(isDistinct);
        if (!isRemote() && !isDistinct) {
            // Ensure that our configured items won't match any other buses in the multiblock.
            // Needed since we allow duplicates in distinct mode on, but not off
            validateConfig();
        }
    }

    @Override
    public IConfigurableSlotList getSlotList() {
        return aeItemHandler;
    }

    @Override
    public boolean testConfiguredInOtherPart(@Nullable GenericStack config) {
        if (config == null) return false;
        // In distinct mode, we don't need to check other buses since only one bus can run a recipe at a time.
        if (!isFormed() || isDistinct()) return false;

        // Otherwise, we need to test for if the item is configured
        // in any stocking bus in the multi (besides ourselves).
        for (IMultiController controller : getControllers()) {
            for (IMultiPart part : controller.getParts()) {
                if (part instanceof MEStockingBusPartMachine bus) {
                    // We don't need to check for ourselves, as this case is handled elsewhere.
                    if (bus == this || bus.isDistinct()) continue;
                    if (bus.aeItemHandler.hasStackInConfig(config, false)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void setAutoPull(boolean autoPull) {
        this.autoPull = autoPull;

        if (isRemote()) return;

        aeItemHandler.clearInventory(0);
        cachedStocks = new CachedStock[0];

        if (autoPull && updateMEStatus()) {
            refreshState = RefreshState.NEED_REBUILD;
            rebuildCooldown = 0;
            updateInventorySubscription();
        } else {
            refreshState = RefreshState.CLEAN;
        }
    }

    private void rebuildCache() {
        IGrid grid = getMainNode().getGrid();
        if (grid == null) {
            cachedStocks = new CachedStock[0];
            return;
        }

        MEStorage storage = grid.getStorageService().getInventory();
        var available = grid.getStorageService().getCachedInventory();

        PriorityQueue<CachedStock> pq = new PriorityQueue<>(
                Comparator.comparingLong(c -> c.amount)
        );

        for (Object2LongMap.Entry<AEKey> entry : available) {
            if (!(entry.getKey() instanceof AEItemKey itemKey)) continue;

            long reported = entry.getLongValue();
            if (reported < minStackSize) continue;

            long extractable = storage.extract(
                    itemKey, reported, Actionable.SIMULATE, actionSource
            );
            if (extractable < minStackSize) continue;

            if (autoPullTest != null &&
                    !autoPullTest.test(new GenericStack(itemKey, extractable))) {
                continue;
            }

            CachedStock cs = new CachedStock(itemKey, extractable);

            if (pq.size() < CONFIG_SIZE) {
                pq.offer(cs);
            } else if (extractable > pq.peek().amount) {
                pq.poll();
                pq.offer(cs);
            }
        }

        cachedStocks = pq.stream()
                .sorted(Comparator.comparingLong((CachedStock c) -> c.amount).reversed())
                .toArray(CachedStock[]::new);
    }

    private void applyCacheToSlots() {
        var inv = aeItemHandler.getInventory();
        int i = 0;

        for (; i < cachedStocks.length && i < CONFIG_SIZE; i++) {
            CachedStock c = cachedStocks[i];
            inv[i].setConfig(new GenericStack(c.key, 1));
            inv[i].setStock(new GenericStack(c.key, c.amount));
        }

        aeItemHandler.clearInventory(i);
    }

    ///////////////////////////////
    // ********** GUI ***********//
    ///////////////////////////////

    @Override
    public void attachConfigurators(ConfiguratorPanel configuratorPanel) {
        IMEStockingPart.super.attachConfigurators(configuratorPanel);
        super.attachConfigurators(configuratorPanel);
    }

    @Override
    public void attachRightConfigurators(RightConfiguratorPanel configuratorPanel) {
        IRCFancyUIProvider.super.attachRightConfigurators(configuratorPanel);
        configuratorPanel.attachConfigurators(new AutoStockingFancyConfigurator(this));
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        return new ModularUI(176, 166, this, entityPlayer).widget(new RCUIWidget(this, 176, 166));
    }

    @Override
    protected InteractionResult onScrewdriverClick(Player playerIn, InteractionHand hand, Direction gridSide,
                                                   BlockHitResult hitResult) {
        if (!isRemote()) {
            setAutoPull(!autoPull);
            if (autoPull) {
                playerIn.sendSystemMessage(
                        Component.translatable("gtceu.machine.me.stocking_auto_pull_enabled"));
            } else {
                playerIn.sendSystemMessage(
                        Component.translatable("gtceu.machine.me.stocking_auto_pull_disabled"));
            }
        }
        return InteractionResult.sidedSuccess(isRemote());
    }

    ////////////////////////////////
    // ****** Configuration ******//
    ////////////////////////////////

    @Override
    protected CompoundTag writeConfigToTag() {
        if (!autoPull) {
            CompoundTag tag = super.writeConfigToTag();
            tag.putBoolean("AutoPull", false);
            return tag;
        }
        // if in auto-pull, no need to write actual configured slots, but still need to write the ghost circuit
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("AutoPull", true);
        tag.putByte("GhostCircuit",
                (byte) IntCircuitBehaviour.getCircuitConfiguration(circuitInventory.getStackInSlot(0)));
        return tag;
    }

    @Override
    protected void readConfigFromTag(CompoundTag tag) {
        if (tag.getBoolean("AutoPull")) {
            // if being set to auto-pull, no need to read the configured slots
            this.setAutoPull(true);
            circuitInventory.setStackInSlot(0, IntCircuitBehaviour.stack(tag.getByte("GhostCircuit")));
            return;
        }
        // set auto pull first to avoid issues with clearing the config after reading from the data stick
        this.setAutoPull(false);
        super.readConfigFromTag(tag);
    }

    public class ExportOnlyAEStockingItemList extends ExportOnlyAEItemList {

        public ExportOnlyAEStockingItemList(MetaMachine holder, int slots) {
            super(holder, slots, ExportOnlyAEStockingItemSlot::new);
        }

        @Override
        public boolean isAutoPull() {
            return autoPull;
        }

        @Override
        public boolean isStocking() {
            return true;
        }

        @Override
        public boolean hasStackInConfig(GenericStack stack, boolean checkExternal) {
            boolean inThisBus = super.hasStackInConfig(stack, false);
            if (inThisBus) return true;
            if (checkExternal) {
                return testConfiguredInOtherPart(stack);
            }
            return false;
        }
    }

    public class ExportOnlyAEStockingItemSlot extends ExportOnlyAEItemSlot {

        public ExportOnlyAEStockingItemSlot() {
            super();
        }

        public ExportOnlyAEStockingItemSlot(@Nullable GenericStack config, @Nullable GenericStack stock) {
            super(config, stock);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 0 && this.stock != null) {
                if (this.config != null) {
                    // Extract the items from the real net to either validate (simulate)
                    // or extract (modulate) when this is called
                    if (!isOnline()) return ItemStack.EMPTY;
                    MEStorage aeNetwork = getMainNode().getGrid().getStorageService().getInventory();

                    Actionable action = simulate ? Actionable.SIMULATE : Actionable.MODULATE;
                    var key = config.what();
                    long extracted = aeNetwork.extract(key, amount, action, actionSource);

                    if (extracted > 0) {
                        ItemStack resultStack = key instanceof AEItemKey itemKey ?
                                itemKey.toStack((int) extracted) : ItemStack.EMPTY;
                        if (!simulate) {
                            // may as well update the display here
                            this.stock = ExportOnlyAESlot.copy(stock, stock.amount() - extracted);
                            if (this.stock.amount() == 0) {
                                this.stock = null;
                            }
                            if (this.onContentsChanged != null) {
                                this.onContentsChanged.run();
                            }
                        }
                        return resultStack;
                    }
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ExportOnlyAEStockingItemSlot copy() {
            return new ExportOnlyAEStockingItemSlot(
                    this.config == null ? null : copy(this.config),
                    this.stock == null ? null : copy(this.stock));
        }
    }
}
