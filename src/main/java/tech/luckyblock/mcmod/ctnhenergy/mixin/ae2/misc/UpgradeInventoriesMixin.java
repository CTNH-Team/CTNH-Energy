package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.misc;

import appeng.api.upgrades.UpgradeInventories;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = UpgradeInventories.class, remap = false)
public class UpgradeInventoriesMixin {
    @ModifyVariable(method = "forMachine", at = @At("HEAD"), argsOnly = true, name = "arg1")
    private static int modifyMaxUpgrades(int maxUpgrades){
        if (maxUpgrades == 1)
            return 2;
        return maxUpgrades;
    }

}
