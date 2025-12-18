package tech.luckyblock.mcmod.ctnhenergy.common.machine.energyhatch;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.storage.MEStorage;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.gui.widget.LongInputWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.ingredient.EnergyStack;
import com.gregtechceu.gtceu.integration.ae2.machine.feature.IGridConnectedMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.trait.GridNodeHolder;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SelectorWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKey;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.VoltageKey;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.gregtechceu.gtceu.api.GTValues.*;

public class MEEnergyPartMachine extends TieredIOPartMachine implements IGridConnectedMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MEEnergyPartMachine.class, TieredIOPartMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    protected final GridNodeHolder nodeHolder;

    @Persisted
    public final MEEnergyContainer energyContainer;

    @DescSynced
    @Getter
    @Setter
    protected boolean isOnline;

    protected final IActionSource actionSource;

    public MEEnergyPartMachine(IMachineBlockEntity holder, IO io) {
        super(holder, UV, io);
        this.nodeHolder = new GridNodeHolder(this);
        this.energyContainer = new MEEnergyContainer(this, io);
        this.actionSource = IActionSource.ofMachine(nodeHolder.getMainNode()::getNode);

    }

    @Override
    public IManagedGridNode getMainNode() {
        return nodeHolder.getMainNode();
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup configGroup = new WidgetGroup(0, 0, 100, 80);
        configGroup.addWidgets(
                new LabelWidget(0, 2, () -> this.isOnline ? "gtceu.gui.me_network.online" : "gtceu.gui.me_network.offline"),
                new LabelWidget(35, 16, "gtceu.creative.energy.voltage"),
                new SelectorWidget(25, 28, 50, 20, Arrays.stream(GTValues.VNF).limit(15).toList(), -1)
                        .setOnChanged(tier -> {
                            energyContainer.setTier(ArrayUtils.indexOf(GTValues.VNF, tier));
                        })
                        .setSupplier(() -> GTValues.VNF[energyContainer.getTier()])
                        .setButtonBackground(ResourceBorderTexture.BUTTON_COMMON)
                        .setBackground(ColorPattern.BLACK.rectTexture())
                        .setValue(GTValues.VNF[energyContainer.getTier()])
                        .setIsUp(true),
                new LabelWidget(35, 52, "gtceu.creative.energy.amperage"),
                new LongInputWidget(0, 64, 100, 20, energyContainer::getInputAmperage, energyContainer::setInputAmperage)
                        .setMax(64L)
        );
        return configGroup;
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return io == IO.IN;
    }

    public static class MEEnergyContainer extends NotifiableRecipeHandlerTrait<EnergyStack> implements IEnergyContainer {

        public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
                MEEnergyContainer.class, NotifiableRecipeHandlerTrait.MANAGED_FIELD_HOLDER);

        @Persisted
        @Getter
        int tier = 0;

        @Persisted
        @Getter
        long inputAmperage = 0;

        @Getter
        final IO handlerIO;

        IGridNode gridNode;
        final IActionSource actionSource = IActionSource.ofMachine(() -> gridNode);

        public MEEnergyContainer(MetaMachine machine, IO io) {
            super(machine);
            handlerIO = io;
        }

        public void setTier(int tier) {
            if(this.tier != tier)
            {
                this.tier = tier;
                if(getMachine() instanceof IMultiPart part){
                    part.getControllers().forEach(IMultiController::onStructureFormed);
                }
            }
        }

        public void setInputAmperage(long inputAmperage) {
            if(this.inputAmperage != inputAmperage){
                this.inputAmperage = inputAmperage;
                if(getMachine() instanceof IMultiPart part){
                    part.getControllers().forEach(IMultiController::onStructureFormed);
                }
            }
        }

        @Override
        public List<EnergyStack> handleRecipeInner(IO io, GTRecipe recipe, List<EnergyStack> left, boolean simulate) {
            for (var it = left.listIterator(); it.hasNext();) {
                EnergyStack stack = it.next();
                if (stack.isEmpty()) {
                    it.remove();
                    continue;
                }
                var storage = getStorage();
                if(storage != null){
                    long totalEU = stack.getTotalEU();
                    totalEU -= Math.abs(changeEnergy(io == IO.IN ? -totalEU:totalEU, simulate));
                    if (totalEU <= 0) {
                        it.remove();
                    } else {
                        it.set(new EnergyStack(totalEU));
                    }
                }
            }

            return left.isEmpty() ? null : left;
        }

        @Override
        public @NotNull List<Object> getContents() {
            long amperage = Math.max(getInputAmperage(), getOutputAmperage());
            return Collections.singletonList(EnergyContainerList.calculateVoltageAmperage(getEnergyStored(), amperage));
        }

        @Override
        public double getTotalContentAmount() {
            return getEnergyStored();
        }

        @Override
        public RecipeCapability<EnergyStack> getCapability() {
            return EURecipeCapability.CAP;
        }

        @Override
        public ManagedFieldHolder getFieldHolder() {
            return MANAGED_FIELD_HOLDER;
        }

        @Override
        public long acceptEnergyFromNetwork(Direction side, long voltage, long amperage) {
            return 0;
        }

        @Override
        public boolean inputsEnergy(Direction side) {
            return false;
        }

        @Override
        public long changeEnergy(long differenceAmount) {
            return changeEnergy(differenceAmount, false);
        }

        public long changeEnergy(long differenceAmount, boolean simulate) {
            var storage = getStorage();
            if(storage != null){
                if(differenceAmount > 0){
                        return storage.insert(EUKey.EU, differenceAmount, Actionable.ofSimulate(simulate), actionSource);
                }
                else {
                    if(checkGridTier())
                        return -storage.extract(EUKey.EU, -differenceAmount, Actionable.ofSimulate(simulate), actionSource);
                    return 0;
                }
            }
            return 0;
        }

        @Override
        public long getEnergyStored() {
            return handlerIO == IO.IN ? getEnergyCapacity() : 0;
        }

        @Override
        public long getEnergyCapacity() {
            if(getStorage() == null) return 0;
            if(handlerIO == IO.IN){
                if(checkGridTier())
                    return getStorage().extract(EUKey.EU, Long.MAX_VALUE, Actionable.SIMULATE, actionSource);
            }
            else{
                return getStorage().insert(EUKey.EU, Long.MAX_VALUE, Actionable.SIMULATE, actionSource);
            }
            return 0;
        }

        @Override
        public long getInputVoltage() {
            return handlerIO == IO.IN ? V[tier] : 0;
        }

        boolean checkGridTier(){
           var storage = getStorage();
           if(storage == null) return false;
           else return storage.extract(VoltageKey.of(tier), 1, Actionable.SIMULATE, actionSource) > 0;
        }

        MEStorage getStorage() {
            if (gridNode == null && machine instanceof IGridConnectedMachine gridConnectedMachine) {
                gridNode = gridConnectedMachine.getGridNode();
            }

            return Optional.ofNullable(gridNode)
                    .map(IGridNode::getGrid)
                    .map(IGrid::getStorageService)
                    .map(IStorageService::getInventory)
                    .orElse(null);
        }

        @Override
        public long getOutputVoltage() {
            return handlerIO == IO.IN ? 0 : V[MAX];
        }

        @Override
        public long getOutputAmperage() {
            return handlerIO == IO.IN ? 0 :1024;
        }
    }
}
