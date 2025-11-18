package tech.luckyblock.mcmod.ctnhenergy.mixin.EUP2P;

import com.jerry.eup2p.EUP2P;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.capabilities.Capability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EUP2P.class, remap = false)
public class EUP2PMixin {
    @Redirect(method = "initializeAttunement",
            at = @At(value = "INVOKE",
                    target = "Lappeng/api/features/P2PTunnelAttunement;registerAttunementApi(Lnet/minecraft/world/level/ItemLike;Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/network/chat/Component;)V")
    )
    private void redirectInitializeAttunement(ItemLike tunnelPart, Capability<?> cap, Component description) {}
}
