package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.emi;

import appeng.api.stacks.GenericStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "appeng.integration.modules.emi.EmiAeBaseScreenStackProvider", remap = false)
public class EmiAeBaseScreenStackProviderMixin {
    @ModifyArg(method = "getStackAt", at = @At(value = "INVOKE", target = "Lappeng/integration/modules/emi/EmiStackHelper;toEmiStack(Lappeng/api/stacks/GenericStack;)Ldev/emi/emi/api/stack/EmiStack;"))
    GenericStack setNumber(GenericStack stack){
        return new GenericStack(stack.what(), Math.max(stack.amount(), 1));
    }

    @ModifyArg(method = "getStackAt", at = @At(value = "INVOKE", target = "Ldev/emi/emi/api/stack/EmiStackInteraction;<init>(Ldev/emi/emi/api/stack/EmiIngredient;Ldev/emi/emi/api/recipe/EmiRecipe;Z)V"), index = 2)
    boolean allowClick(boolean clickable){
        return true;
    }
}
