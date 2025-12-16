package tech.luckyblock.mcmod.ctnhenergy.mixin.gtm;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WorkableElectricMultiblockMachine.class, remap = false)
public class WorkableElectricMultiblockMachineMixin {
    @Redirect(
            method = "getMaxVoltage",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/gregtechceu/gtceu/api/GTValues;V:[J",
                    opcode = Opcodes.GETSTATIC)
    )
    private long[] redirectGTValuesV() {
        return GTValues.VEX;
    }
}
