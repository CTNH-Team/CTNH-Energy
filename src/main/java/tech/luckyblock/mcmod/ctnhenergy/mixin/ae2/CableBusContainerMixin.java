package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2;

import net.minecraft.resources.ResourceLocation;

import appeng.api.parts.IPartItem;
import appeng.parts.CableBusContainer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEItems;

@Mixin(value = CableBusContainer.class, remap = false)
public class CableBusContainerMixin {

    @WrapOperation(method = "loadPart",
                   at = @At(value = "INVOKE",
                            target = "Lappeng/api/parts/IPartItem;byId(Lnet/minecraft/resources/ResourceLocation;)Lappeng/api/parts/IPartItem;"))
    @Nullable
    IPartItem<?> fixEUP2P(ResourceLocation id, Operation<IPartItem<?>> original) {
        var part = original.call(id);
        if (part == null && id.getNamespace().equals("eup2p") && id.getPath().equals("eu_p2p_tunnel")) {
            return CEItems.EU_P2P.get();
        }
        return part;
    }
}
