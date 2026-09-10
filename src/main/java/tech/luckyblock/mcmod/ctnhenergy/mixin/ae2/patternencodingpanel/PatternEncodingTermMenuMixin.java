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

    /** encode() 之前编码槽是否为空；为空说明本次消耗了空白样板，是一次全新编码。 */
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
        if (stack.isEmpty() || !PatternDetailsHelper.isEncodedPattern(stack)) {
            return;
        }
        // 槽位里已有编码样板时，AE2 会直接用新编码的样板覆盖它（即“修改样板”），
        // 这条路径同样要把署名改写成当前玩家，否则署名会停留在原作者或直接丢失。
        PatternAuthorData.addAuthorLore(stack, player.getScoreboardName());
        if (ctnhenergy$wasEmptyBeforeEncode) {
            // 只有消耗了空白样板的全新编码才计入统计
            CEStats.awardEncodedPattern(player);
        }
    }
}
