package tech.luckyblock.mcmod.ctnhenergy.mixin.aecs;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import io.github.lounode.ae2cs.common.item.CrystalSeedItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CrystalSeedItem.class, remap = false)
public class CrystalSeedItemMixin {

    @Inject(method = "onEntityItemUpdate", at = @At("HEAD"), cancellable = true)
    void cancelGrow(ItemStack stack, ItemEntity itemEntity, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
