package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.cpu;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import appeng.client.gui.me.crafting.CraftingStatusTableRenderer;
import appeng.menu.me.crafting.CraftingStatusEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.event.ForgeClientEventHandler;

import java.util.List;

/**
 * Advertises the shift click lookup (see {@code ForgeClientEventHandler#onCraftingStatusClick}) in the
 * tooltip of crafting status entries that are scheduled or currently being crafted.
 */
@Mixin(value = CraftingStatusTableRenderer.class, remap = false)
public class CraftingStatusTableRendererMixin {

    @Inject(method = "getEntryTooltip", at = @At("RETURN"))
    private void CE$addLocateHint(CraftingStatusEntry entry, CallbackInfoReturnable<List<Component>> cir) {
        if (entry.getActiveAmount() <= 0L && entry.getPendingAmount() <= 0L) {
            return;
        }
        var lines = cir.getReturnValue();
        if (lines != null) {
            lines.add(ForgeClientEventHandler.shift_locate_pattern.translate().withStyle(ChatFormatting.GRAY));
        }
    }
}
