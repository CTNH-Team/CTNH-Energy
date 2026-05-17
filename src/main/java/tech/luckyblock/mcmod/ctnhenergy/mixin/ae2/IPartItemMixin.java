package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2;

import appeng.api.parts.IPartItem;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEItems;

@Mixin(value = IPartItem.class, remap = false)
public interface IPartItemMixin {
    @Inject(method = "byId", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private static void fixEUP2P(ResourceLocation id, CallbackInfoReturnable<IPartItem<?>> cir) {
        if(id.getNamespace().equals("eup2p") && id.getPath().equals("eu_p2p_tunnel")) {
            cir.setReturnValue(CEItems.EU_P2P.get());
        }
    }
}
