package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.misc;

import appeng.api.config.PowerUnits;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = PowerUnits.class, remap = false)
public class PowerUnitsMixin {

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private static String noMoreAE1(String arg3) {
        return arg3.equals("gui.ae2.units.appliedenergistics") ? "EU" : arg3;
    }

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, ordinal = 2)
    private static String noMoreAE2(String arg3) {
        return arg3.equals("AE") ? "EU" : arg3;
    }
}
