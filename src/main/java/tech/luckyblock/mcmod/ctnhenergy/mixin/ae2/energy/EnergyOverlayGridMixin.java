package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.energy;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "appeng.me.service.EnergyOverlayGrid", remap = false)
public class EnergyOverlayGridMixin {

    @Redirect(method = "buildCache",
              at = @At(value = "INVOKE", target = "Lappeng/core/AELog;error(Ljava/lang/String;[Ljava/lang/Object;)V"))
    private static void ignoreError(String format, Object[] params) {
        // TODO: 找出为什么会报错
    }
}
