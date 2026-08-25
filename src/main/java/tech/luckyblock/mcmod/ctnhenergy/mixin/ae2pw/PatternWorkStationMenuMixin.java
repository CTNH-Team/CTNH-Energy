package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2pw;

import net.minecraft.world.item.ItemStack;

import com.ctnh.ae2pw.common.PatternWorkStationMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.common.pattern.PatternAuthorData;

@Mixin(value = PatternWorkStationMenu.class, remap = false)
public class PatternWorkStationMenuMixin {

    @Inject(method = "encodePattern", at = @At("RETURN"), cancellable = true)
    private void ctnhenergy$addAuthorLore(CallbackInfoReturnable<ItemStack> cir) {
        var pattern = cir.getReturnValue();
        if (pattern == null || pattern.isEmpty()) {
            return;
        }
        var player = ((appeng.menu.AEBaseMenu) (Object) this).getPlayer();
        if (player.level().isClientSide()) {
            return;
        }
        PatternAuthorData.addAuthorLore(pattern, player.getScoreboardName());
        cir.setReturnValue(pattern);
    }
}
