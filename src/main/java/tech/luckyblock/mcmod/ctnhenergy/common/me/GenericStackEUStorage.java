package tech.luckyblock.mcmod.ctnhenergy.common.me;

import appeng.api.behaviors.GenericInternalInventory;

import appeng.api.config.Actionable;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import net.minecraft.core.Direction;

import static com.gregtechceu.gtceu.api.GTValues.*;

@SuppressWarnings("UnstableApiUsage")
public class GenericStackEUStorage implements IEnergyContainer {

    private final GenericInternalInventory inv;

    public GenericStackEUStorage(GenericInternalInventory inv){
        this.inv = inv;
    }

    @Override
    public long acceptEnergyFromNetwork(Direction side, long voltage, long amperage) {
        if (voltage <= 0L || (side != null && !inputsEnergy(side))) {
            return 0;
        }

        long canAccept = getEnergyCapacity() - getEnergyStored();
        if (canAccept <= 0) {
            return 0;
        }

        long maxEnergy = Math.min(canAccept, voltage * amperage);
        if (maxEnergy < voltage) {
            return 0;
        }

        long amperesAccepted = Math.min(maxEnergy / voltage, amperage);
        if (amperesAccepted <= 0) {
            return 0;
        }

        long energyToAdd = voltage * amperesAccepted;

        // 调用 changeEnergy 来添加能量
        long actuallyAdded = changeEnergy(energyToAdd);

        // 返回实际接受的安培数
        return actuallyAdded > 0 ? actuallyAdded / voltage : 0;
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

        long energyChanged = 0;

        if (differenceAmount > 0) {
            long remaining = differenceAmount;

            for (int slot = 0; slot < this.inv.size(); slot++) {
                if (remaining <= 0) break;

                var inserted = this.inv.insert(slot, EUKey.EU, remaining, Actionable.MODULATE);
                energyChanged += inserted;
                remaining -= inserted;
            }
        } else {
            long toExtract = -differenceAmount;

            for (int slot = 0; slot < this.inv.size(); slot++) {
                if (toExtract <= 0) break;

                var stack = this.inv.getStack(slot);
                if (stack != null && EUKey.EU == stack.what()) {
                    long available = stack.amount();
                    long extractAmount = Math.min(available, toExtract);

                    if (extractAmount > 0) {
                        var extracted = this.inv.extract(slot, EUKey.EU, extractAmount, Actionable.MODULATE);
                        energyChanged -= extracted;
                        toExtract -= extracted;
                    }
                }
            }
        }

        return energyChanged;
    }

    @Override
    public long getEnergyStored() {
        long cnt = 0;
        for (int slot = 0; slot < this.inv.size(); slot ++) {
            var stack = this.inv.getStack(slot);
            if(stack != null && EUKey.EU == stack.what()){
                cnt += stack.amount();
            }
        }
        return cnt;
    }

    @Override
    public long getEnergyCapacity() {
        long cnt = 0;
        for (int slot = 0; slot < this.inv.size(); slot ++) {
            var stack = this.inv.getStack(slot);
            if (stack != null) {
                if (EUKey.EU == stack.what()) {
                    cnt ++;
                }
            } else {
                cnt ++;
            }
        }
        return cnt * inv.getMaxAmount(EUKey.EU);
    }

    @Override
    public long getInputAmperage() {
        return 1;
    }

    @Override
    public long getInputVoltage() {
        return V[MAX];
    }
}
