package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.misc;

import appeng.api.upgrades.IUpgradeableObject;
import appeng.core.definitions.AEItems;
import appeng.parts.automation.IOBusPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = IOBusPart.class, remap = false)
public class IOBusPartMixin implements IUpgradeableObject {
    @Inject(method = "getOperationsPerTick", at = @At("RETURN"), cancellable = true)
    protected void getOperationsPerTick(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(
                switch (getInstalledUpgrades(AEItems.SPEED_CARD)) {
                    case 1 -> 1024;
                    case 2 -> 114514;
                    case 3 -> 1919810;
                    case 4 -> Integer.MAX_VALUE;
                    default -> 64;
                }
        );
    }
}
