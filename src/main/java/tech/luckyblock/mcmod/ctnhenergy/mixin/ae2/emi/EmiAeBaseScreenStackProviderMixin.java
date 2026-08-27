package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.emi;

import net.minecraft.client.gui.screens.Screen;

import appeng.api.stacks.GenericStack;
import appeng.client.gui.me.crafting.CraftConfirmScreen;
import appeng.client.gui.me.crafting.CraftingCPUScreen;
import com.llamalad7.mixinextras.sugar.Local;
import com.neuvillette.ae2ct.gui.CraftingTreeScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "appeng.integration.modules.emi.EmiAeBaseScreenStackProvider", remap = false)
public class EmiAeBaseScreenStackProviderMixin {

    @ModifyArg(method = "getStackAt",
               at = @At(value = "INVOKE",
                        target = "Lappeng/integration/modules/emi/EmiStackHelper;toEmiStack(Lappeng/api/stacks/GenericStack;)Ldev/emi/emi/api/stack/EmiStack;"))
    GenericStack setNumber(GenericStack stack) {
        return new GenericStack(stack.what(), Math.max(stack.amount(), 1));
    }

    @ModifyArg(method = "getStackAt",
               at = @At(value = "INVOKE",
                        target = "Ldev/emi/emi/api/stack/EmiStackInteraction;<init>(Ldev/emi/emi/api/stack/EmiIngredient;Ldev/emi/emi/api/recipe/EmiRecipe;Z)V"),
               index = 2)
    boolean allowClick(boolean clickable, @Local(argsOnly = true) Screen screen) {
        // Shift + left click on the crafting status table is CTNH-Energy's own interaction (blink the
        // pattern providers holding that pattern). While shift is held the stack must not be clickable
        // for EMI, otherwise EMI consumes the press inside its MouseHandler mixin and starts a stack
        // drag instead of letting the screen see the click. Hover tooltips are unaffected because EMI
        // queries them with notClick = true.
        if (Screen.hasShiftDown() && screen instanceof CraftingCPUScreen) {
            return false;
        }
        return screen instanceof CraftConfirmScreen || screen instanceof CraftingCPUScreen ||
                screen instanceof CraftingTreeScreen;
    }
}
