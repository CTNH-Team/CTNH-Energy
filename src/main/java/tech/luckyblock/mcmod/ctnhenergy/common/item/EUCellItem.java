package tech.luckyblock.mcmod.ctnhenergy.common.item;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.localization.Tooltips;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.common.me.cell.EuCellHandler;

import java.util.List;

public class EUCellItem extends ComponentItem implements IEUCell{
    public EUCellItem(Properties properties) {
        super(properties);
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack stack) {
        return  UpgradeInventories.forItem(stack, 3);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        var handler = EuCellHandler.HANDLER.getCellInventory(stack, null);
        if(handler != null){
            tooltipComponents.add(2, Tooltips.bytesUsed(handler.getUsedBytes(), handler.getTotalBytes()));
        }
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
}
