package tech.luckyblock.mcmod.ctnhenergy.mixin.betterP2P;

import com.jerry.eup2p.common.registry.EUP2PItem;
import com.llamalad7.mixinextras.sugar.Local;
import dev.lasm.betterp2p.ClientProxy;
import dev.lasm.betterp2p.CommonProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(
        value = {CommonProxy.class},
        remap = false
)
public abstract class CommonProxyMixin {

    @Shadow protected abstract void registerModTunnel(Supplier<Item> def, int type, String classType);

    @Inject(
            method = {"initTunnels"},
            at = {@At("RETURN")}
    )
    private void onInit(CallbackInfo ci, @Local(name = "typeId") int typeID) {
        registerModTunnel(EUP2PItem.EU_P2P_TUNNEL::asItem, typeID, "com.jerry.eup2p.common.parts.p2p.EUP2PTunnelPart");
    }
}

@Mixin(value = ClientProxy.class, remap = false)
abstract class ClientProxyMixin{
    @Shadow protected abstract void registerModTunnel(Supplier<Item> def, int type, String classType, ResourceLocation icon);

    @Inject(
            method = {"initTunnels"},
            at = {@At("RETURN")}
    )
    private void onInit(CallbackInfo ci, @Local(name = "typeId") int typeID) {
        registerModTunnel(
                EUP2PItem.EU_P2P_TUNNEL::asItem,
                typeID,
                "com.jerry.eup2p.common.parts.p2p.EUP2PTunnelPart",
                ResourceLocation.fromNamespaceAndPath("eup2p","textures/item/eu.png")
                );
    }
}