package tech.luckyblock.mcmod.ctnhenergy.common.item;

import appeng.api.config.FuzzyMode;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.cells.ICellWorkbenchItem;
import net.minecraft.world.item.ItemStack;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKeyType;

public interface IEUCell extends ICellWorkbenchItem {
    default AEKeyType getKeyType(){
        return EUKeyType.INSTANCE;
    };

    @Override
    default boolean isEditable(ItemStack is) {
        return true;
    }

    @Override
    default FuzzyMode getFuzzyMode(ItemStack is) {
        return null;
    }

    @Override
    default void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {
    }

}
