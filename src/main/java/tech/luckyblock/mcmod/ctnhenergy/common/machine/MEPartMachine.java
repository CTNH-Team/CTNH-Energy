package tech.luckyblock.mcmod.ctnhenergy.common.machine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.CircuitFancyConfigurator;
import com.gregtechceu.gtceu.api.machine.feature.IDataStickConfigurable;
import com.gregtechceu.gtceu.api.machine.feature.IHasCircuitSlot;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.IWorkLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDistinctPart;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.handler.RecipeHandlerList;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.integration.ae2.GridNodeHost;
import com.gregtechceu.gtceu.integration.ae2.IGridConnectedMachine;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.utils.MEConfigUtil;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class MEPartMachine extends TieredIOPartMachine
                           implements IDistinctPart, IHasCircuitSlot, IGridConnectedMachine, IActionHost,
                           IDataStickConfigurable {

    @Persisted
    protected final GridNodeHost nodeHost;
    @Getter
    protected final IActionSource actionSource = IActionSource.ofMachine(this);
    protected final Supplier<IGridNode> nodeSupplier;

    @Getter
    private final boolean circuitSlotEnabled;
    private final boolean distinctEnabled;

    @Getter
    @Persisted
    protected final NotifiableItemStackHandler circuitInventory;

    @Getter
    @Persisted
    @DescSynced
    private boolean isDistinct = false;

    @Persisted
    private boolean exposeAllSides = false;

    private final int aeOffset = GTValues.RNG.nextInt(ConfigHolder.INSTANCE.compat.ae2.updateIntervals);

    private final Supplier<Set<Direction>> frontFacing = () -> hasFrontFacing() ? EnumSet.of(getFrontFacing()) :
            allFacing;
    private static final Set<Direction> allFacing = EnumSet.allOf(Direction.class);

    public MEPartMachine(IMachineBlockEntity holder, int tier, IO io) {
        this(holder, tier, io, io.support(IO.IN), io.support(IO.IN));
    }

    public MEPartMachine(IMachineBlockEntity holder, int tier, IO io,
                         boolean circuitSlotEnabled, boolean distinctEnabled) {
        super(holder, tier, io);
        nodeHost = createNodeHost();
        nodeSupplier = () -> {
            if (isWorkingEnabled()) {
                return nodeHost.getGridNode();
            }
            return null;
        };
        this.circuitSlotEnabled = circuitSlotEnabled;
        this.distinctEnabled = distinctEnabled;
        this.circuitInventory = createCircuitItemHandler().shouldSearchContent(false);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateExposedSides();
    }

    protected GridNodeHost createNodeHost() {
        GridNodeHost host = new GridNodeHost(this);
        host.getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .setIdlePowerUsage(ConfigHolder.INSTANCE.compat.ae2.meHatchEnergyUsage);
        return host;
    }

    @Override
    public Pair<GTToolType, InteractionResult> onToolClick(Set<@NotNull GTToolType> toolType, ItemStack itemStack,
                                                           UseOnContext context) {
        var result = super.onToolClick(toolType, itemStack, context);
        if (!result.getSecond().consumesAction() && toolType.contains(GTToolType.WIRE_CUTTER)) {
            if (!isRemote()) {
                exposeAllSides = !exposeAllSides;
                updateExposedSides();
            }
            return new Pair<>(GTToolType.WIRE_CUTTER, InteractionResult.sidedSuccess(isRemote()));
        }
        return result;
    }

    private void updateExposedSides() {
        nodeHost.getMainNode().setExposedOnSides(exposeAllSides ? allFacing : frontFacing.get());
    }

    @Override
    public @Nullable IGridNode getActionableNode() {
        return nodeHost.getMainNode().getNode();
    }

    public boolean isNodeActive() {
        return getActionableNode() != null && getActionableNode().isActive();
    }

    protected boolean isMESyncTick() {
        int interval = ConfigHolder.INSTANCE.compat.ae2.updateIntervals;
        // Spread AE machine updates evenly across each AE update cycle.
        return (getOffsetTimer() + aeOffset) % interval == 0;
    }

    @Override
    public void onRotated(Direction oldFacing, Direction newFacing) {
        super.onRotated(oldFacing, newFacing);
        nodeHost.getMainNode().setExposedOnSides(exposeAllSides ? allFacing : Set.of(newFacing));
    }

    protected NotifiableItemStackHandler createCircuitItemHandler() {
        if (circuitSlotEnabled) {
            return new NotifiableItemStackHandler(this, 1, IO.IN, IO.NONE)
                    .setFilter(IntCircuitBehaviour::isIntegratedCircuit);
        } else {
            return new NotifiableItemStackHandler(this, 0, IO.NONE);
        }
    }

    @Override
    public void setDistinct(boolean distinct) {
        isDistinct = (io != IO.OUT && distinct);
        getControllers().forEach(controller -> {
            if (controller instanceof IRecipeLogicMachine rlm) {
                rlm.getRecipeLogic().resetLastGroup();
            }
        });
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        super.setWorkingEnabled(workingEnabled);
        if (workingEnabled) {
            getControllers().forEach(controller -> {
                if (controller instanceof IWorkLogicMachine workLogicMachine) {
                    workLogicMachine.getWorkLogic().updateTickSubscription();
                }
            });
        }
    }

    @Override
    public void attachConfigurators(ConfiguratorPanel left, ConfiguratorPanel right) {
        if (isCircuitSlotEnabled()) {
            left.attachConfigurators(new CircuitFancyConfigurator(circuitInventory.storage));
        }

        if (distinctEnabled) {
            IDistinctPart.super.attachConfigurators(left, right);
        } else {
            super.attachConfigurators(left, right);
        }
    }

    @Override
    public List<RecipeHandlerList> getRecipeHandlers() {
        return super.getRecipeHandlers();
    }

    @Override
    public void writeConfig(CompoundTag tag) {
        if (isCircuitSlotEnabled()) {
            MEConfigUtil.writeGhostCircuit(tag, circuitInventory);
        }
        if (distinctEnabled) {
            MEConfigUtil.writeDistinctBuses(tag, isDistinct());
        }
    }

    @Override
    public void readConfig(CompoundTag tag) {
        if (isCircuitSlotEnabled()) {
            MEConfigUtil.readGhostCircuit(tag, circuitInventory);
        }
        if (distinctEnabled) {
            MEConfigUtil.readDistinctBuses(tag, this::setDistinct);
        }
    }
}
