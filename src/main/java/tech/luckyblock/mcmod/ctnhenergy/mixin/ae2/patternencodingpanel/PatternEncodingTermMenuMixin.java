package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.patternencodingpanel;

import net.minecraft.world.item.ItemStack;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.menu.AEBaseMenu;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.RestrictedInputSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.common.pattern.PatternAuthorData;
import tech.luckyblock.mcmod.ctnhenergy.common.stats.CEStats;

@Mixin(value = PatternEncodingTermMenu.class, remap = false)
public abstract class PatternEncodingTermMenuMixin {

    @Shadow
    private RestrictedInputSlot encodedPatternSlot;

    @Unique
    private boolean ctnhenergy$wasEmptyBeforeEncode;

    @Inject(method = "encode", at = @At("HEAD"))
    private void ctnhenergy$recordBeforeEncode(CallbackInfo ci) {
        ctnhenergy$wasEmptyBeforeEncode = encodedPatternSlot.getItem().isEmpty();
    }

    @Inject(method = "encode", at = @At("TAIL"))
    private void ctnhenergy$afterEncode(CallbackInfo ci) {
        var player = ((AEBaseMenu) (Object) this).getPlayer();
        if (player.level().isClientSide()) {
            return;
        }
        ItemStack stack = encodedPatternSlot.getItem();
        if (ctnhenergy$wasEmptyBeforeEncode && !stack.isEmpty() && PatternDetailsHelper.isEncodedPattern(stack)) {
            PatternAuthorData.addAuthorLore(stack, player.getScoreboardName());
            // 下单统计数
            CEStats.awardEncodedPattern(player);
        }
    }
}
