package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.emi;

import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import appeng.integration.modules.emi.EmiUseCraftingRecipeHandler;
import appeng.menu.me.items.CraftingTermMenu;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.platform.EmiClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EmiUseCraftingRecipeHandler.class, remap = false)
public abstract class EmiUseCraftingRecipeHandlerMixin implements StandardRecipeHandler<CraftingTermMenu> {
    @Inject(method = "canCraft", at = @At("HEAD"), cancellable = true)
    void canCraft(EmiRecipe recipe, EmiCraftContext<?> context, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(true);
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
}
