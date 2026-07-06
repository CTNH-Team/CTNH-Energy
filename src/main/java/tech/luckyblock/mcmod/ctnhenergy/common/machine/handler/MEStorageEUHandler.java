package tech.luckyblock.mcmod.ctnhenergy.common.machine.handler;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.MEStorage;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import lombok.Getter;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.utils.GenericStackHandler;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKey;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.VoltageKey;

import java.util.List;
import java.util.function.Supplier;

import static com.gregtechceu.gtceu.api.GTValues.MAX;
import static com.gregtechceu.gtceu.api.GTValues.V;

public class MEStorageEUHandler extends NotifiableRecipeHandlerTrait<Long> implements IEnergyContainer {
    @Getter
    public final IO handlerIO;

    private final Supplier<IGridNode> nodeSupplier;

    @Persisted
    @Getter
    private int tier = 0;

    @Persisted
    @Getter
    private long inputAmperage = 0;

    @Getter
    private long energyCapacity;

    private final IActionSource actionSource;

    public MEStorageEUHandler(MetaMachine machine, IO io, Supplier<IGridNode> nodeSupplier) {
        super(machine);
        this.handlerIO = io;
        this.nodeSupplier = nodeSupplier;
        actionSource= IActionSource.ofMachine(nodeSupplier::get);
        capabilityValidator = direction -> false;
        updateEnergyCapacity();
    }

    public void setTier(int tier) {
        if (this.tier != tier) {
            this.tier = tier;
            updateEnergyCapacity();
            if (getMachine() instanceof IMultiPart part) {
                part.getControllers().forEach(IMultiController::onStructureFormed);
            }
        }
    }

    public void setInputAmperage(long inputAmperage) {
        if (this.inputAmperage != inputAmperage) {
            this.inputAmperage = inputAmperage;
            updateEnergyCapacity();
            if (getMachine() instanceof IMultiPart part) {
                part.getControllers().forEach(IMultiController::onStructureFormed);
            }
        }
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

    private long changeEnergy(long differenceAmount, boolean simulate) {
        var storage = getStorage();
        if (storage != null) {
            if (differenceAmount > 0) {
                return storage.insert(EUKey.EU, differenceAmount, Actionable.ofSimulate(simulate), actionSource);
            } else {
                return -storage.extract(EUKey.EU, -differenceAmount, Actionable.ofSimulate(simulate), actionSource);
            }
        }
        return 0;
    }

    @Override
    public long getEnergyStored() {
        return handlerIO == IO.IN ? getEnergyCapacity() : 0;
    }

    @Override
    public long getInputVoltage() {
        return handlerIO == IO.IN && checkGridTier() ? V[tier] : 0;
    }

    @Override
    public boolean handleRecipe(IO io, GTRecipe recipe, List<Long> left, boolean simulate) {
        if(getStorage() == null) return false;
        for (var it = left.listIterator(); it.hasNext();) {
            long totalEU = it.next();
            if (totalEU == 0) {
                it.remove();
                continue;
            }
            totalEU -= Math.abs(changeEnergy(io == IO.IN ? -totalEU : totalEU, simulate));
            if (totalEU <= 0) {
                it.remove();
            } else {
                it.set(totalEU);
            }
        }

        return left.isEmpty();
    }

    @Override
    public @NotNull List<Object> getContents() {
        return List.of(getEnergyStored());
    }

    @Override
    public double getTotalContentAmount() {
        return getEnergyStored();
    }

    @Override
    public RecipeCapability<Long> getCapability() {
        return EURecipeCapability.CAP;
    }

    private @Nullable MEStorage getStorage() {
        var mainNode = nodeSupplier.get();
        if (mainNode == null || !mainNode.isActive()) return null;
        return mainNode.getGrid().getStorageService().getInventory();
    }

    public boolean checkGridTier() {
        var storage = getStorage();
        if (storage == null) return false;
        else return storage.extract(VoltageKey.of(tier), 1, Actionable.SIMULATE, actionSource) > 0;
    }

    public void updateEnergyCapacity() {
        if (getStorage() == null) {
            energyCapacity = 0;
            return;
        }
        if (handlerIO == IO.IN) {
            energyCapacity = getStorage().extract(EUKey.EU, Long.MAX_VALUE, Actionable.SIMULATE, actionSource);
        } else {
            energyCapacity = getStorage().insert(EUKey.EU, Long.MAX_VALUE, Actionable.SIMULATE, actionSource);
        }
    }

    @Override
    public long getOutputVoltage() {
        return handlerIO == IO.IN ? 0 : V[MAX];
    }

    @Override
    public long getOutputAmperage() {
        return handlerIO == IO.IN ? 0 : 1024;
    }
}
