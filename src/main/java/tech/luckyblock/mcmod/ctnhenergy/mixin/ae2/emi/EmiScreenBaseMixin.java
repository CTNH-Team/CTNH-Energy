package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.emi;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;

import appeng.menu.AEBaseMenu;
import com.llamalad7.mixinextras.sugar.Local;
import dev.emi.emi.screen.EmiScreenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EmiScreenBase.class, remap = false)
public abstract class EmiScreenBaseMixin {

    @Redirect(method = "of", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;isEmpty()Z"))
    private static boolean allowAEBaseScreen(NonNullList<?> instance, @Local(name = "sh") AbstractContainerMenu sh) {
        return instance.isEmpty() && !(sh instanceof AEBaseMenu);
    }
}
