package tech.luckyblock.mcmod.ctnhenergy.api;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.lowdragmc.lowdraglib.gui.ingredient.IGhostIngredientTarget;
import com.lowdragmc.lowdraglib.gui.ingredient.Target;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public interface IGhostKeyTarget extends IGhostIngredientTarget {

    @OnlyIn(Dist.CLIENT)
    Rect2i getRectangleBox();

    @OnlyIn(Dist.CLIENT)
    void acceptKey(@Nullable AEKey aeKey);

    @Override
    default List<Target> getPhantomTargets(Object o) {
        if(convertIngredient(o) == null) return Collections.emptyList();
        else {
            final Rect2i rectangle = getRectangleBox();
            return GTUtil.list(new Target() {
                @Override
                public @NotNull Rect2i getArea() {
                    return rectangle;
                }

                @Override
                public void accept(Object o) {
                    acceptKey(convertIngredient(o));
                }
            });
        }
    }

    default @Nullable AEKey convertIngredient(Object ingredient) {
        if(ingredient instanceof EmiStack emiStack) {
            Item item = emiStack.getKeyOfType(Item.class);
            if (item != null) {
                return AEItemKey.of(item, emiStack.getNbt());
            }
            else {
                Fluid fluid = emiStack.getKeyOfType(Fluid.class);
                return fluid == null ? null : AEFluidKey.of(fluid, emiStack.getNbt());
            }
        }
        return null;
    }
}
