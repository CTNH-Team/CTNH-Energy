package tech.luckyblock.mcmod.ctnhenergy.event;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.ctnhlang.CN;
import com.ctnhlang.Category;
import com.ctnhlang.EN;
import com.wintercogs.ae2omnicells.common.blocks.OmniCraftingUnitBlock;
import com.wintercogs.ae2omnicells.common.items.OmniCraftingBlockItem;
import com.wintercogs.ae2omnicells.common.me.crafting.OmniCraftingFamily;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.luckyblock.mcmod.ctnhenergy.common.pattern.PatternAuthorData;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

@Category("tooltip")
@Mod.EventBusSubscriber(modid = CTNHEnergy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEventHandler {

    @CN("并行数：")
    @EN("Number of Parallels: ")
    static Lang omni_thread_num;

    @CN("可自动翻倍发配处理样板")
    @EN("Automatically doubles processing pattern execution.")
    static Lang auto_multiply;

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
}
