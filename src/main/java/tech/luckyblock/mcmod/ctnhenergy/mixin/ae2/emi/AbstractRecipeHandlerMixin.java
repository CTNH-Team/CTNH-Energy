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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "appeng.integration.modules.emi.AbstractRecipeHandler", remap = false)
public abstract class AbstractRecipeHandlerMixin<T extends CraftingTermMenu> implements StandardRecipeHandler<T> {

    @Redirect(
            method = "canCraft",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/emi/emi/api/recipe/handler/EmiCraftContext;getType()Ldev/emi/emi/api/recipe/handler/EmiCraftContext$Type;")
    )
    public EmiCraftContext.Type canCraft(EmiCraftContext instance) {
        if((Object)this instanceof EmiUseCraftingRecipeHandler)
            return EmiCraftContext.Type.FILL_BUTTON;
        else
            return instance.getType();
    }

    @Inject(
            method = "craft",
            at = @At("RETURN")
    )
    public void craftClick(EmiRecipe recipe, EmiCraftContext<T> context, CallbackInfoReturnable<Boolean> cir) {
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
