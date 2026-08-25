package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.emi;

import net.minecraft.world.item.crafting.Recipe;

import appeng.integration.modules.emi.AbstractRecipeHandler.Result;
import appeng.integration.modules.emi.EmiEncodePatternHandler;
import appeng.menu.me.items.PatternEncodingTermMenu;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EmiEncodePatternHandler.class, remap = false)
public class EmiEncodePatternHandlerMixin {

    @Inject(method = "canCraft", at = @At("HEAD"), cancellable = true)
    void canCraft(EmiRecipe recipe, EmiCraftContext<?> context, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "transferRecipe", at = @At("RETURN"))
    private void ctnhenergy$encodeAfterTransfer(PatternEncodingTermMenu menu, Recipe<?> recipeBase,
                                                EmiRecipe emiRecipe, boolean doTransfer,
                                                CallbackInfoReturnable<Result> cir) {
        if (doTransfer) {
            menu.encode();
        }
    }
}
