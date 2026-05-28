package tech.luckyblock.mcmod.ctnhenergy.mixin.betterP2P;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import com.llamalad7.mixinextras.sugar.Local;
import dev.lasm.betterp2p.ClientProxy;
import dev.lasm.betterp2p.CommonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEItems;

import java.util.function.Supplier;

@Mixin(
       value = { CommonProxy.class },
       remap = false)
public abstract class CommonProxyMixin {

    @Shadow
    protected abstract void registerModTunnel(Supplier<Item> def, int type, String classType);

    @Inject(
            method = { "initTunnels" },
            at = { @At("RETURN") })
    private void onInit(CallbackInfo ci, @Local(name = "typeId") int typeID) {
        registerModTunnel(CEItems.EU_P2P::asItem, typeID,
                "tech.luckyblock.mcmod.ctnhenergy.common.me.parts.p2p.EUP2PTunnelPart");
    }
}

@Mixin(value = ClientProxy.class, remap = false)
abstract class ClientProxyMixin {

    @Shadow
    protected abstract void registerModTunnel(Supplier<Item> def, int type, String classType, ResourceLocation icon);

    @Inject(
            method = { "initTunnels" },
            at = { @At("RETURN") })
    private void onInit(CallbackInfo ci, @Local(name = "typeId") int typeID) {
        registerModTunnel(
                CEItems.EU_P2P::asItem,
                typeID,
                "tech.luckyblock.mcmod.ctnhenergy.common.me.parts.p2p.EUP2PTunnelPart",
                CTNHEnergy.id("textures/item/eu.png"));
    }
}
