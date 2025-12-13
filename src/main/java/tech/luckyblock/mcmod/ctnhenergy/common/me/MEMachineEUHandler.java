package tech.luckyblock.mcmod.ctnhenergy.common.me;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.MEStorage;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.util.inv.AppEngInternalInventory;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.utils.GTUtil;
import lombok.Getter;
import net.minecraft.core.Direction;
import tech.luckyblock.mcmod.ctnhenergy.common.item.DynamoCardItem;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKey;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.VoltageKey;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEItems;
import tech.luckyblock.mcmod.ctnhenergy.utils.CEUtil;

import static com.gregtechceu.gtceu.api.GTValues.*;

public class MEMachineEUHandler implements IEnergyContainer {
    @Getter
    IGridNode node;
    MEStorage inv;
    IActionSource source;
    long outputVoltage = 0;

    public MEMachineEUHandler(IGridNode gridNode, IUpgradeableObject upgradeable){
        node = gridNode;
        inv =  node.getGrid().getStorageService().getInventory();
        source = IActionSource.ofMachine(() -> node);
        if(upgradeable.isUpgradedWith(CEItems.DYNAMO_CARD)
                && upgradeable.getUpgrades() instanceof AppEngInternalInventory inventory){
            for (var itemStack : inventory) {
                var tag = itemStack.getOrCreateTag();
                if(itemStack.is(CEItems.DYNAMO_CARD.asItem()) && tag.contains(DynamoCardItem.VOLTAGE)){
                    outputVoltage = V[tag.getInt(DynamoCardItem.VOLTAGE)];
                }
            }
        }

    }

    @Override
    public long acceptEnergyFromNetwork(Direction side, long voltage, long amperage) {
        if (voltage <= 0L || amperage < 1) return 0;

        if(voltage <= getInputVoltage()){

            long energyToAdd = voltage * amperage;

            long actuallyAdded = changeEnergy(energyToAdd);

            return actuallyAdded > 0 ? actuallyAdded / voltage : 0;
        }

        return 0;
    }

    @Override
    public boolean inputsEnergy(Direction side) {
        return true;
    }

    @Override
    public long changeEnergy(long differenceAmount) {
        if (differenceAmount == 0) {
            return 0;
        }

        if (differenceAmount > 0) {
            return inv.insert(EUKey.EU, differenceAmount, Actionable.MODULATE, source);
        }
        else {
            return -inv.extract(EUKey.EU, -differenceAmount, Actionable.MODULATE, source);
        }
    }

    @Override
    public long getEnergyStored() {
        return inv.extract(EUKey.EU, Long.MAX_VALUE, Actionable.SIMULATE, source);
    }

    @Override
    public long getEnergyCapacity() {
        return inv.insert(EUKey.EU, Long.MAX_VALUE, Actionable.SIMULATE, source) + getEnergyStored();
    }

    @Override
    public long getInputAmperage() {
        return 1;
    }

    @Override
    public long getInputVoltage() {
        var tier = CEUtil.getGridTier(node);
        if(tier >= 0)
            return V[tier];
        return 0;
    }

    @Override
    public long getOutputAmperage() {
        return 1;
    }

    @Override
    public long getOutputVoltage() {
        return outputVoltage;
    }

    @Override
    public boolean outputsEnergy(Direction side) {
        return outputVoltage > 0 && outputVoltage <= getInputVoltage();
    }
}
