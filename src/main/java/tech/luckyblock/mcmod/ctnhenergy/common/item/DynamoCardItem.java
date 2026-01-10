package tech.luckyblock.mcmod.ctnhenergy.common.item;

import appeng.items.materials.UpgradeCardItem;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DynamoCardItem extends UpgradeCardItem implements IComponentItem {

    public static final String VOLTAGE = "voltage";

    public DynamoCardItem(Properties properties) {
        super(properties);
    }


    @Override
    public List<IItemComponent> getComponents() {
        return List.of();
    }

    @Override
    public void attachComponents(IItemComponent... components) {

    }

    @Override
    public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
        IComponentItem.super.fillItemCategory(category, items);
        for (var tier: GTValues.ALL_TIERS) {
            ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag().putInt(VOLTAGE, tier);
            items.add(stack);
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        if(tag.contains(VOLTAGE)){
            return Component.literal(GTValues.VNF[tag.getInt(VOLTAGE)])
                    .append(" ")
                    .append(super.getName(stack));
        }
        return super.getName(stack);
    }
}
