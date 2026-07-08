package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.Identifiers;
import snownee.jade.api.config.IPluginConfig;

@Mixin(targets = "appeng.integration.modules.jade.BodyProviderAdapter", remap = false)
public class BodyProviderAdapterMixin {

    @Inject(method = "appendTooltip",
            at = @At(value = "INVOKE",
                     target = "Lsnownee/jade/api/ITooltip;remove(Lnet/minecraft/resources/ResourceLocation;)V",
                     ordinal = 0))
    void removeFE(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config, CallbackInfo ci) {
        // remove FE bar
        tooltip.remove(Identifiers.UNIVERSAL_ENERGY_STORAGE);
    }
}
