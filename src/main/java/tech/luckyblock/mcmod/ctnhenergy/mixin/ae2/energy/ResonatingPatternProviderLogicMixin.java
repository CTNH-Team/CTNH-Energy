package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.energy;

import io.github.lounode.ae2cs.common.me.logic.ResonatingPatternProviderLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.common.me.service.IEnergyDistributor;

@Mixin(value = ResonatingPatternProviderLogic.class, remap = false)
public class ResonatingPatternProviderLogicMixin {

    @Inject(method = "onUpgradesChange", at = @At("HEAD"))
    void updateSleep(CallbackInfo ci) {
        if (this instanceof IEnergyDistributor energyDistributor) {
            energyDistributor.updateSleep();
        }
    }
}
