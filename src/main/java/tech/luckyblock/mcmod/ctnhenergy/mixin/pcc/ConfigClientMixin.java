package tech.luckyblock.mcmod.ctnhenergy.mixin.pcc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.utils.CircuitConfigManager;
import yuuki1293.pccard.ConfigClient;

@Mixin(value = ConfigClient.class, remap = false)
public class ConfigClientMixin {
    @Inject(method = "getJeiIntegration", at = @At("HEAD"), cancellable = true)
    private static void getJeiIntegration(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(CircuitConfigManager.enabled);
    }
}
