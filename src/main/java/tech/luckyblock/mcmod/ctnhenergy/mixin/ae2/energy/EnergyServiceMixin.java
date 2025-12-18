package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.energy;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.PowerUnits;
import appeng.api.networking.security.IActionSource;
import appeng.me.Grid;
import appeng.me.service.EnergyService;
import com.gregtechceu.gtceu.api.capability.compat.FeCompat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKey;

@Mixin(value = EnergyService.class, remap = false)
public class EnergyServiceMixin {
    @Shadow
    @Final
    Grid grid;

    @Inject(method = "extractProviderPower", at = @At("RETURN"), cancellable = true)
    public void extractAEPower(double amt, Actionable mode, CallbackInfoReturnable<Double> cir){
        double extracted = cir.getReturnValue();
        if(extracted < amt){
            extracted += grid.getStorageService().getInventory()
                    .extract(EUKey.EU,
                            FeCompat.toEu((long)PowerUnits.AE.convertTo(PowerUnits.FE, amt - extracted), FeCompat.ratio(false)),
                            mode,
                            IActionSource.empty());
        }
        cir.setReturnValue(extracted);
    }
}
