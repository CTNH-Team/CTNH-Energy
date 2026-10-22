package tech.luckyblock.mcmod.ctnhenergy.event;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import appeng.client.gui.me.crafting.CraftingCPUScreen;
import com.ctnhlang.CN;
import com.ctnhlang.Category;
import com.ctnhlang.EN;
import com.mojang.blaze3d.platform.InputConstants;
import com.wintercogs.ae2omnicells.common.blocks.OmniCraftingUnitBlock;
import com.wintercogs.ae2omnicells.common.items.OmniCraftingBlockItem;
import com.wintercogs.ae2omnicells.common.me.crafting.OmniCraftingFamily;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.luckyblock.mcmod.ctnhenergy.common.pattern.PatternAuthorData;
import tech.luckyblock.mcmod.ctnhenergy.network.packets.LocatePatternProviderPacket;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import static com.lowdragmc.lowdraglib.networking.LDLNetworking.NETWORK;

@Category("tooltip")
@Mod.EventBusSubscriber(modid = CTNHEnergy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEventHandler {

    @CN("并行数：")
    @EN("Number of Parallels: ")
    static Lang omni_thread_num;

    @CN("可自动翻倍发配处理样板")
    @EN("Automatically doubles processing pattern execution.")
    static Lang auto_multiply;

    @CN("Shift 点击定位样板")
    @EN("Shift + click to locate the pattern")
    public static Lang shift_locate_pattern;

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof OmniCraftingBlockItem blockItem &&
                blockItem.getBlock() instanceof OmniCraftingUnitBlock craftingUnitBlock) {
            var threads = craftingUnitBlock.type.getAcceleratorThreads();
            event.getToolTip().add(omni_thread_num.translate().append(Component.literal(String.valueOf(threads))));
            if (craftingUnitBlock.omniCraftingType.family == OmniCraftingFamily.COMPLEX) {
                event.getToolTip().add(auto_multiply.translate().withStyle(ChatFormatting.AQUA));
            }
        }
        if (Screen.hasShiftDown()) {
            PatternAuthorData.addEncodedTimeLine(event.getToolTip(), stack);
        }
    }

    /**
     * Shift + left click on an entry of the AE2 crafting status UI (scheduled or currently crafting)
     * highlights the pattern providers holding a pattern for that item, reusing ExtendedAE's outline
     * renderer and chat message.
     * <p>
     * The press only reaches this event because EmiAeBaseScreenStackProviderMixin reports crafting
     * status stacks as non-clickable while shift is held; otherwise EMI consumes it inside its
     * MouseHandler mixin and starts a stack drag instead.
     */
    @SubscribeEvent
    public static void onCraftingStatusClick(ScreenEvent.MouseButtonPressed.Pre event) {
        if (event.getButton() != InputConstants.MOUSE_BUTTON_LEFT || !Screen.hasShiftDown()) {
            return;
        }
        // Only entries of the crafting status table are handled, never regular inventory slots.
        if (!(event.getScreen() instanceof CraftingCPUScreen<?> screen) || screen.getSlotUnderMouse() != null) {
            return;
        }
        var hovered = screen.getStackUnderMouse(event.getMouseX(), event.getMouseY());
        if (hovered == null) {
            return;
        }
        NETWORK.sendToServer(new LocatePatternProviderPacket(hovered.stack().what()));
        event.setCanceled(true);
    }
}
