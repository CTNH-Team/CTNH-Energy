package tech.luckyblock.mcmod.ctnhenergy.api;

import net.minecraft.world.item.ItemStack;

public interface EUItemContext {

    ItemStack getStack();

    void setStack(ItemStack stack);

    void addOverflow(ItemStack stack);

    int getTier();
}
