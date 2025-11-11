package tech.luckyblock.mcmod.ctnhenergy.utils;

import com.gregtechceu.gtceu.common.item.ColorSprayBehaviour;
import lombok.Getter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

import static com.gregtechceu.gtceu.common.data.GTItems.SPRAY_EMPTY;

@Getter
public class TempColorSprayBehaviour extends ColorSprayBehaviour {
    public boolean used = false;

    public TempColorSprayBehaviour(int color) {
        super(() -> SPRAY_EMPTY.asStack(), 1024, color);
    }

    @Override
    public boolean useItemDurability(Player player, InteractionHand hand, ItemStack stack, ItemStack replacementStack) {
        used = true;
        return true;
    }

}
