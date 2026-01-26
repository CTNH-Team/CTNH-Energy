package tech.luckyblock.mcmod.ctnhenergy.mixin.gtm;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.PowerSubstationMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEMultiblock;

@Mixin(value = PowerSubstationMachine.class, remap = false)
public class PowerSubstationMachineMixin extends WorkableMultiblockMachine {

    @Shadow
    private PowerSubstationMachine.PowerStationEnergyBank energyBank;

    public PowerSubstationMachineMixin(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        var stored =  energyBank.getStored();
        BlockPos blockPos = getHolder().pos();
        BlockState newBlockState = CEMultiblock.POWER_SUBSTATION.defaultBlockState();
        if(getLevel() == null) return;
        getLevel().setBlockAndUpdate(blockPos, newBlockState);
        if (getLevel().getBlockEntity(blockPos) instanceof IMachineBlockEntity newHolder) {
            if (newHolder.getMetaMachine() instanceof tech.luckyblock.mcmod.ctnhenergy.common.machine.PowerSubstationMachine newMachine){
                newMachine.setFrontFacing(this.getFrontFacing());
                newMachine.setUpwardsFacing(this.getUpwardsFacing());
                newMachine.setPaintingColor(this.getPaintingColor());
                newMachine.setLegacyEnergy(stored);
            }
        }
    }
}
