package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.misc;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;

import appeng.api.integrations.igtooltip.BaseClassRegistration;
import appeng.integration.modules.igtooltip.TooltipProviders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TooltipProviders.class, remap = false)
public class TooltipProvidersMixin {

    @Inject(method = "registerBlockEntityBaseClasses", at = @At("TAIL"))
    void injectGTBE(BaseClassRegistration registration, CallbackInfo ci) {
        registration.addBaseBlockEntity(MetaMachineBlockEntity.class, MetaMachineBlock.class);
    }
}
