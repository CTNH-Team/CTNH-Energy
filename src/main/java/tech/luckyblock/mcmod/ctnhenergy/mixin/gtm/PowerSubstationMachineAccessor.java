package tech.luckyblock.mcmod.ctnhenergy.mixin.gtm;

import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.PowerSubstationMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = PowerSubstationMachine.class, remap = false)
public interface PowerSubstationMachineAccessor {
    @Accessor("energyBank")
    PowerSubstationMachine.PowerStationEnergyBank getEnergyBank();

    @Accessor("tickSubscription")
    ConditionalSubscriptionHandler getSubscription();
}
