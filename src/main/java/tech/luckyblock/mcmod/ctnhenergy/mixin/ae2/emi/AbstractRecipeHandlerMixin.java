package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.emi;

import appeng.core.localization.ItemModText;
import appeng.menu.me.items.CraftingTermMenu;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.utils.CEUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(targets = "appeng.integration.modules.emi.AbstractRecipeHandler", remap = false)
public class AbstractRecipeHandlerMixin {
    @Inject(method = "getRecipeInputSlots", at = @At("HEAD"), cancellable = true)
    private static void handleNonCrafting(EmiRecipe recipe, List<Widget> widgets, CallbackInfoReturnable<Map<Integer, SlotWidget>> cir){
        if(!CEUtil.isCrafting(recipe)){
            HashMap<Integer, SlotWidget> inputSlots = new HashMap<>(recipe.getInputs().size());
            for(int i = 0; i < recipe.getInputs().size(); ++i) {
                for(Widget widget : widgets) {
                    if (widget instanceof SlotWidget slot) {
                        if (slot.getRecipe() == null && slot.getStack().equals(recipe.getInputs().get(i))) {
                            inputSlots.put(i, slot);
                        }
                    }
                }
            }
            cir.setReturnValue(inputSlots);
        }
    }

    @Mixin(targets = "appeng.integration.modules.emi.AbstractRecipeHandler$Result$PartiallyCraftable", remap = false)
    static class PartiallyCraftableMixin{
        @Shadow
        @Final
        private CraftingTermMenu.MissingIngredientSlots missingSlots;

        @Inject(method = "getTooltip", at = @At("HEAD"), cancellable = true)
        void fixTooltip(EmiRecipe recipe, EmiCraftContext<?> context, CallbackInfoReturnable<List<Component>> cir){
            if (!CEUtil.isCrafting(recipe)){
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.translatable("gui.ctnhenergy.moveitem"));
                if (missingSlots.anyCraftable()) {
                    tooltip.add(ItemModText.CTRL_CLICK_TO_CRAFT.text().withStyle(ChatFormatting.BLUE));
                }
                if (missingSlots.anyMissing()) {
                    tooltip.add(ItemModText.MISSING_ITEMS.text().withStyle(ChatFormatting.RED));
                }
                cir.setReturnValue(tooltip);
            }
        }
    }
}
