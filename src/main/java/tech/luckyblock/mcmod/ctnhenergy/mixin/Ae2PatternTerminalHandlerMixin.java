package tech.luckyblock.mcmod.ctnhenergy.mixin;

import appeng.api.stacks.GenericStack;
import com.gregtechceu.gtceu.integration.emi.recipe.Ae2PatternTerminalHandler;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = Ae2PatternTerminalHandler.class, remap = false)
public class Ae2PatternTerminalHandlerMixin {

    @Shadow
    private static List<GenericStack> intoGenericStack(EmiIngredient ingredient) {
        return null;
    }

    @Inject(method = "ofInputs", at = @At("HEAD"), cancellable = true)
    private static void ofInputs(EmiRecipe emiRecipe, CallbackInfoReturnable<List<List<GenericStack>>> cir) {
        cir.setReturnValue(
                emiRecipe.getInputs()
                        .stream()
                        .map(Ae2PatternTerminalHandlerMixin::intoGenericStack)
                        .toList()
        );
    }
}
