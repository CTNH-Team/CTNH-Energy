package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.emi;

import appeng.core.localization.ItemModText;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.FillCraftingGridFromRecipePacket;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import appeng.integration.modules.emi.EmiStackHelper;
import appeng.integration.modules.emi.EmiUseCraftingRecipeHandler;
import appeng.menu.me.items.CraftingTermMenu;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.platform.EmiClient;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.utils.CEUtil;
import tech.luckyblock.mcmod.ctnhenergy.utils.ResultReflection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Mixin(value = EmiUseCraftingRecipeHandler.class, remap = false)
public abstract class EmiUseCraftingRecipeHandlerMixin implements StandardRecipeHandler<CraftingTermMenu> {
    @Inject(method = "supportsRecipe", at = @At("HEAD"), cancellable = true)
    void qiuckCollect(EmiRecipe recipe, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(true);
    }


    @Inject(method = "canCraft", at = @At("HEAD"), cancellable = true)
    void canCraft(EmiRecipe recipe, EmiCraftContext<?> context, CallbackInfoReturnable<Boolean> cir){
        if(CEUtil.isCrafting(recipe)) {
            cir.setReturnValue(true);
        }
        else {
            cir.setReturnValue(context.getType() == EmiCraftContext.Type.FILL_BUTTON);
        }
    }

    @Inject(
            method = "craft",
            at = @At("RETURN")
    )
    public void craftClick(EmiRecipe recipe, EmiCraftContext<CraftingTermMenu> context, CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValue() && EmiClient.onServer){
            var destination = context.getDestination();
            var outputSlot = this.getOutputSlot(context.getScreenHandler());
            if(destination!= EmiCraftContext.Destination.NONE && outputSlot != null){
                InventoryAction action = null;
                if(destination == EmiCraftContext.Destination.CURSOR)
                {
                    action = InventoryAction.CRAFT_ITEM;
                }
                else if(destination == EmiCraftContext.Destination.INVENTORY)
                {
                    action = context.getAmount() == 1 ? InventoryAction.CRAFT_SHIFT : InventoryAction.CRAFT_ALL;
                }

                NetworkHandler.instance().sendToServer(
                        new InventoryActionPacket(action, outputSlot.index, 0)
                );
            }


        }
    }

    @Inject(method = "transferRecipe(Lappeng/menu/me/items/CraftingTermMenu;Lnet/minecraft/world/item/crafting/Recipe;Ldev/emi/emi/api/recipe/EmiRecipe;Z)Lappeng/integration/modules/emi/AbstractRecipeHandler$Result;",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    void handleNonCraftingRecipe(CraftingTermMenu menu, Recipe<?> recipeBase, EmiRecipe emiRecipe, boolean doTransfer, CallbackInfoReturnable<Object> cir){
        if(!CEUtil.isCrafting(emiRecipe)){

            Map<Integer, Ingredient> slotToIngredientMap = new HashMap<>();
            int i = 0;
            for(var list: EmiStackHelper.ofInputs(emiRecipe)){
                slotToIngredientMap.put(i++, CEUtil.ingredientFromGenericStacks(list));
            }
            var missingSlots = menu.findMissingIngredients(slotToIngredientMap);
            if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
                cir.setReturnValue(ResultReflection.createFailed(ItemModText.NO_ITEMS.text(), missingSlots.missingSlots()));
            }

            if(!doTransfer){
                if (missingSlots.anyMissingOrCraftable()) {
                    cir.setReturnValue(ResultReflection.createPartiallyCraftable(missingSlots));
                }
            }
            else {
                var templateItems = NonNullList.of(ItemStack.EMPTY);
                var recipeId = emiRecipe.getId();
                NetworkHandler.instance()
                        .sendToServer(new FillCraftingGridFromRecipePacket(recipeId, templateItems, AbstractContainerScreen.hasControlDown()));
                cir.setReturnValue(ResultReflection.createSuccessful());

            }

        }

    }
}
