package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.cpu;

import appeng.api.networking.crafting.ICraftingSubmitResult;
import appeng.menu.me.crafting.CraftConfirmMenu;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.common.stats.CEStats;

@Mixin(value = CraftConfirmMenu.class, remap = false)
public class CraftConfirmMenuMixin {

    @Inject(
            method = "startJob",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/api/networking/crafting/ICraftingSubmitResult;successful()Z",
                    remap = false))
    private void ctnhenergy$awardMECraftRequest(CallbackInfo ci, @Local ICraftingSubmitResult submitResult) {
        var menu = (CraftConfirmMenu) (Object) this;
        var player = menu.getPlayer();
        if (!player.level().isClientSide() && submitResult.successful()) {
            CEStats.awardMECraftRequest(player);
        }
    }
}
