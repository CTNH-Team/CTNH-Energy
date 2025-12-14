package tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IInteractedMachine;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.api.transfer.fluid.CustomFluidTank;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.integration.ae2.gui.widget.list.AEListGridWidget;
import com.gregtechceu.gtceu.integration.ae2.machine.MEBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.utils.KeyStorage;

import com.gregtechceu.gtceu.utils.GTMath;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.ISubscription;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import lombok.NoArgsConstructor;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.utils.AEGenericDisplayWidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * The Output Bus that can directly send its contents to ME storage network.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MEDualOutputHatchPartMachine extends MEBusPartMachine implements IMachineLife, IInteractedMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MEDualOutputHatchPartMachine.class, MEBusPartMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    private KeyStorage internalBuffer;

    @Persisted
    public final NotifiableFluidTank tank;

    @Nullable
    protected ISubscription tankSubs;

    private List<Runnable> changeListeners;

    public MEDualOutputHatchPartMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, IO.OUT, args);
        tank = new InaccessibleInfiniteTank(this, getChangeListeners(), internalBuffer);
        internalBuffer.setOnContentsChanged(()-> changeListeners.forEach(Runnable::run));
    }

    /////////////////////////////////
    // ***** Machine LifeCycle ****//
    /////////////////////////////////

    @Override
    protected NotifiableItemStackHandler createInventory(Object... args) {
        this.internalBuffer = new KeyStorage();
        return new InaccessibleInfiniteHandler(this, getChangeListeners(), internalBuffer);
    }

    List<Runnable> getChangeListeners(){
        if(changeListeners == null)
            changeListeners = new ArrayList<>();
        return changeListeners;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        tankSubs = tank.addChangedListener(this::updateInventorySubscription);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (tankSubs != null) {
            tankSubs.unsubscribe();
            tankSubs = null;
        }
    }

    @Override
    public void onMachineRemoved() {
        var grid = getMainNode().getGrid();
        if (grid != null && !internalBuffer.isEmpty()) {
            for (var entry : internalBuffer) {
                grid.getStorageService().getInventory().insert(entry.getKey(), entry.getLongValue(),
                        Actionable.MODULATE, actionSource);
            }
        }
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    /////////////////////////////////
    // ********** Sync ME *********//
    /////////////////////////////////

    @Override
    protected boolean shouldSubscribe() {
        return super.shouldSubscribe() && !internalBuffer.storage.isEmpty();
    }

    @Override
    public void autoIO() {
        if (!this.shouldSyncME()) return;
        if (this.updateMEStatus()) {
            var grid = getMainNode().getGrid();
            if (grid != null && !internalBuffer.isEmpty()) {
                internalBuffer.insertInventory(grid.getStorageService().getInventory(), actionSource);
            }
            this.updateInventorySubscription();
        }
    }

    ///////////////////////////////
    // ********** GUI ***********//
    ///////////////////////////////

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 170, 65);
        // ME Network status
        group.addWidget(new LabelWidget(5, 0, () -> this.isOnline ?
                "gtceu.gui.me_network.online" :
                "gtceu.gui.me_network.offline"));
        group.addWidget(new LabelWidget(5, 10, "gtceu.gui.waiting_list"));
        // display list
        group.addWidget(new Generic(5, 20, 3, this.internalBuffer));

        return group;
    }

    public static class Generic extends AEListGridWidget {

        public Generic(int x, int y, int slotsY, KeyStorage internalList) {
            super(x, y, slotsY, internalList);
        }

        @Override
        protected void toPacket(FriendlyByteBuf buffer, AEKey key) {
            AEKey.writeKey(buffer, key);
        }

        @Override
        protected AEKey fromPacket(FriendlyByteBuf buffer) {
            return AEKey.readKey(buffer);
        }

        @Override
        protected void writeListChange(FriendlyByteBuf buffer) {
            this.changeMap.clear();

            // Remove
            var cachedIt = cached.storage.object2LongEntrySet().iterator();
            while (cachedIt.hasNext()) {
                var entry = cachedIt.next();
                var cachedKey = entry.getKey();
                if (!list.storage.containsKey(cachedKey)) {
                    this.changeMap.put(cachedKey, -entry.getLongValue());
                    cachedIt.remove();
                }
            }

            // Change/Add
            for (var entry : list.storage.object2LongEntrySet()) {
                var key = entry.getKey();
                long value = entry.getLongValue();
                long cacheValue = cached.storage.getOrDefault(key, 0);
                if (cacheValue == 0) {
                    // Add
                    this.changeMap.put(key, value);
                    this.cached.storage.put(key, value);
                } else {
                    // Change
                    if (cacheValue != value) {
                        this.changeMap.put(key, value - cacheValue);
                        this.cached.storage.put(key, value);
                    }
                }
            }

            buffer.writeVarInt(this.changeMap.size());
            for (var entry : this.changeMap.object2LongEntrySet()) {
                toPacket(buffer, entry.getKey());// rewrite this method to fix this line
                buffer.writeVarLong(entry.getLongValue());
            }
        }

        @Override
        protected Widget createDisplayWidget(int x, int y, int index) {
            return new AEGenericDisplayWidget(x, y, this, index);
        }
    }

    public static class InaccessibleInfiniteHandler extends NotifiableItemStackHandler {

        public InaccessibleInfiniteHandler(MetaMachine holder, List<Runnable> changeListeners, KeyStorage buffer) {
            super(holder, 1, IO.OUT, IO.NONE, i -> new ItemStackHandlerDelegate(i, buffer));
            //internalBuffer.setOnContentsChanged(this::onContentsChanged);
            changeListeners.add(this::onContentsChanged);
        }

        @Override
        public @NotNull List<Object> getContents() {
            return Collections.emptyList();
        }

        @Override
        public double getTotalContentAmount() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }

    @NoArgsConstructor
    public static class ItemStackHandlerDelegate extends CustomItemStackHandler {

        KeyStorage buffer;
        // Necessary for InaccessibleInfiniteHandler
        public ItemStackHandlerDelegate(Integer integer, KeyStorage buffer) {
            super();
            this.buffer = buffer;
        }

        @Override
        public int getSlots() {
            return Short.MAX_VALUE;
        }

        @Override
        public int getSlotLimit(int slot) {
            return Integer.MAX_VALUE;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            // NO-OP
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            var key = AEItemKey.of(stack);
            int count = stack.getCount();
            long oldValue = buffer.storage.getOrDefault(key, 0);
            long changeValue = Math.min(Long.MAX_VALUE - oldValue, count);
            if (changeValue > 0) {
                if (!simulate) {
                    buffer.storage.put(key, oldValue + changeValue);
                    buffer.onChanged();
                }
                return stack.copyWithCount((int) (count - changeValue));
            } else {
                return ItemStack.EMPTY;
            }
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    }

    public static class InaccessibleInfiniteTank extends NotifiableFluidTank {

        FluidStorageDelegate storage;

        public InaccessibleInfiniteTank(MetaMachine holder, List<Runnable> changeListeners, KeyStorage buffer) {
            super(holder, List.of(new FluidStorageDelegate(buffer)), IO.OUT, IO.NONE);
            //internalBuffer.setOnContentsChanged(this::onContentsChanged);
            changeListeners.add(this::onContentsChanged);
            storage = (FluidStorageDelegate) getStorages()[0];
            allowSameFluids = true;
        }

        @Override
        public int getTanks() {
            return 128;
        }

        @Override
        public @NotNull List<Object> getContents() {
            return Collections.emptyList();
        }

        @Override
        public double getTotalContentAmount() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return FluidStack.EMPTY;
        }

        @Override
        public void setFluidInTank(int tank, @NotNull FluidStack fluidStack) {}

        @Override
        public int getTankCapacity(int tank) {
            return storage.getCapacity();
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return true;
        }

        @Override
        @Nullable
        public List<FluidIngredient> handleRecipeInner(IO io, GTRecipe recipe, List<FluidIngredient> left,
                                                       boolean simulate) {
            if (io != IO.OUT) return left;
            FluidAction action = simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE;
            for (var it = left.iterator(); it.hasNext();) {
                var ingredient = it.next();
                if (ingredient.isEmpty()) {
                    it.remove();
                    continue;
                }

                var fluids = ingredient.getStacks();
                if (fluids.length == 0 || fluids[0].isEmpty()) {
                    it.remove();
                    continue;
                }

                FluidStack output = fluids[0];
                ingredient.shrink(storage.fill(output, action));
                if (ingredient.getAmount() <= 0) it.remove();
            }
            return left.isEmpty() ? null : left;
        }
    }

    public static class FluidStorageDelegate extends CustomFluidTank {

        KeyStorage buffer;
        public FluidStorageDelegate(KeyStorage buffer) {
            super(0);
            this.buffer = buffer;
        }

        @Override
        public int getCapacity() {
            return Integer.MAX_VALUE;
        }

        @Override
        public void setFluid(FluidStack fluid) {
            // NO-OP
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            var key = AEFluidKey.of(resource.getFluid(), resource.getTag());
            int amount = resource.getAmount();
            int oldValue = GTMath.saturatedCast(buffer.storage.getOrDefault(key, 0));
            int changeValue = Math.min(Integer.MAX_VALUE - oldValue, amount);
            if (changeValue > 0 && action.execute()) {
                buffer.storage.put(key, oldValue + changeValue);
                buffer.onChanged();
            }
            return changeValue;
        }

        @Override
        public boolean supportsFill(int tank) {
            return false;
        }

        @Override
        public boolean supportsDrain(int tank) {
            return false;
        }
    }
}
