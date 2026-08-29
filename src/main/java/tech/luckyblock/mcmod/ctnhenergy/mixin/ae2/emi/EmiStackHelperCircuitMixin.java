package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.emi;

import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.gregtechceu.gtceu.integration.emi.recipe.GTEmiRecipe;

import appeng.api.stacks.GenericStack;
import appeng.integration.modules.emi.EmiStackHelper;
import dev.emi.emi.api.recipe.EmiRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.CEConfig;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = EmiStackHelper.class, remap = false)
public abstract class EmiStackHelperCircuitMixin {

    @Inject(method = "ofInputs", at = @At("RETURN"), cancellable = true)
    private static void addCircuit(EmiRecipe recipe, CallbackInfoReturnable<List<List<GenericStack>>> cir) {
        if (CEConfig.INSTANCE == null || !CEConfig.INSTANCE.client.enableCircuitInPatternEncoding) return;

        if (recipe instanceof GTEmiRecipe) {
            var circuit = recipe.getCatalysts().stream()
                    .filter(entry -> !entry.getEmiStacks().isEmpty())
                    .map(entry -> entry.getEmiStacks().get(0).getItemStack())
                    .filter(stack -> stack.is(GTItems.PROGRAMMED_CIRCUIT.asItem()))
                    .findFirst().orElse(IntCircuitBehaviour.stack(0));

            var generic = GenericStack.fromItemStack(circuit);

            if (generic != null) {
                var list = new ArrayList<>(cir.getReturnValue());
                list.add(List.of(generic));
                cir.setReturnValue(list);
            }
        }
    }
}
