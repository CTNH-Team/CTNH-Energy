package tech.luckyblock.mcmod.ctnhenergy.common.me;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;

import net.minecraft.core.Direction;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.MEStorage;
import appeng.api.upgrades.IUpgradeableObject;
import lombok.Getter;
import tech.luckyblock.mcmod.ctnhenergy.common.item.DynamoCardItem;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKey;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEItems;
import tech.luckyblock.mcmod.ctnhenergy.utils.CEUtil;

import java.util.function.Supplier;

import static com.gregtechceu.gtceu.api.GTValues.*;

public class MEMachineEUHandler implements IEnergyContainer {

    @Getter
    Supplier<IGridNode> nodeSupplier;
    MEStorage inv;
    IActionSource source;
    IUpgradeableObject upgradeable;
    long outputVoltage = 0;

    public MEMachineEUHandler(Supplier<IGridNode> nodeSupplier, IUpgradeableObject upgradeable) {
        this.nodeSupplier = nodeSupplier;
        source = IActionSource.ofMachine(nodeSupplier::get);
        this.upgradeable = upgradeable;
    }

    @Override
    public long acceptEnergyFromNetwork(Direction side, long voltage, long amperage) {
        if (voltage <= 0L) return 0;

        if (voltage <= getInputVoltage()) {

            long energyToAdd = voltage * amperage;

            long actuallyAdded = changeEnergy(energyToAdd);

            return actuallyAdded > 0 ? actuallyAdded / voltage : 0;
        }

        return 0;
    }

    @Override
    public boolean inputsEnergy(Direction side) {
        return getInputVoltage() > 0;
    }

    @Override
    public long changeEnergy(long differenceAmount) {
        if (differenceAmount == 0) {
            return 0;
        }

        if (differenceAmount > 0) {
            return getInventory().insert(EUKey.EU, differenceAmount, Actionable.MODULATE, source);
        } else {
            return -getInventory().extract(EUKey.EU, -differenceAmount, Actionable.MODULATE, source);
        }
    }

    @Override
    public long getEnergyStored() {
        return getInventory().extract(EUKey.EU, Long.MAX_VALUE, Actionable.SIMULATE, source);
    }

    @Override
    public long getEnergyCapacity() {
        // 可能会有性能问题
        return getInventory().insert(EUKey.EU, Long.MAX_VALUE, Actionable.SIMULATE, source) + getEnergyStored();
    }

    @Override
    public long getInputAmperage() {
        return 1;
    }

    @Override
    public long getInputVoltage() {
        if (nodeSupplier.get() == null) {
            return 0;
        }
        var tier = CEUtil.getGridTier(nodeSupplier.get());
        if (tier >= 0)
            return V[tier];
        return 0;
    }

    @Override
    public long getOutputAmperage() {
        return 4;
    }

    @Override
    public long getOutputVoltage() {
        return outputVoltage;
    }

    @Override
    public boolean outputsEnergy(Direction side) {
        return outputVoltage > 0 && outputVoltage <= getInputVoltage();
    }

    public void updateVoltage() {
        if (upgradeable.getUpgrades() != null) {
            for (var itemStack : upgradeable.getUpgrades()) {
                if (itemStack.is(CEItems.DYNAMO_CARD.asItem()) && itemStack.hasTag()) {
                    var tag = itemStack.getTag();
                    if (tag.contains(DynamoCardItem.VOLTAGE))
                        outputVoltage = V[tag.getInt(DynamoCardItem.VOLTAGE)];
                }
            }
        }
    }

    MEStorage getInventory() {
        if (inv == null) {
            var node = nodeSupplier.get();
            if (node != null) {
                inv = node.getGrid().getStorageService().getInventory();
            }
        }
        return inv;
    }

    public IGrid getGrid() {
        var node = nodeSupplier.get();
        return node == null ? null : node.getGrid();
    }
}
