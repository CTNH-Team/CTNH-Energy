package tech.luckyblock.mcmod.ctnhenergy.common.me.strategy.context;


import com.gregtechceu.gtceu.api.GTValues;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tech.luckyblock.mcmod.ctnhenergy.api.EUItemContext;

public record PlayerInvContextEU(Player player, int slot) implements EUItemContext {

    @Override
    public ItemStack getStack() {
        return this.player.getInventory().getItem(this.slot);
    }

    @Override
    public void setStack(ItemStack stack) {
        this.player.getInventory().setItem(this.slot, stack);
    }

    public void addOverflow(ItemStack stack) {
        this.player.getInventory().placeItemBackInInventory(stack);
    }

    @Override
    public int getTier() {
        return GTValues.MAX;
    }
}
