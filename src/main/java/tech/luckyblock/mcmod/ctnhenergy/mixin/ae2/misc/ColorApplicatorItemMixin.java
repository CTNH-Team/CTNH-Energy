package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.StorageCells;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalBlockPos;
import appeng.items.tools.powered.ColorApplicatorItem;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import appeng.me.helpers.PlayerSource;
import appeng.util.Platform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tech.luckyblock.mcmod.ctnhenergy.utils.TempColorSprayBehaviour;

import java.util.function.DoubleSupplier;

@Mixin(value = ColorApplicatorItem.class, remap = false)
public abstract class ColorApplicatorItemMixin extends AEBasePoweredItem {

    public ColorApplicatorItemMixin(DoubleSupplier powerCapacity, Properties props) {
        super(powerCapacity, props);
    }

    @Shadow
    public ItemStack getColor(ItemStack is) {
        return null;
    }

    @Shadow
    private AEColor getColorFromItem(ItemStack paintBall) {
        return null;
    }

    @Shadow
    public boolean consumeItem(ItemStack applicator, AEItemKey paintItem, boolean simulate) {
        return false;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack is = context.getItemInHand();
        Player p = context.getPlayer(); // This can be null
        if (p == null && level instanceof ServerLevel) {
            p = Platform.getFakePlayer((ServerLevel) level, null);
        }

        var paintBall = this.getColor(is);
        var paintBallKey = AEItemKey.of(paintBall);
        var source = new PlayerSource(p);
        var inv = StorageCells.getCellInventory(is, null);
        if (inv != null) {
            var extracted = inv.extract(paintBallKey, 1, Actionable.SIMULATE, source);

            if (extracted > 0) {
                paintBall = paintBall.copy();
                paintBall.setCount(1);
            } else {
                paintBall = ItemStack.EMPTY;
            }

            if (!Platform.hasPermissions(new DimensionalBlockPos(level, pos), p)) {
                return InteractionResult.FAIL;
            }

            if (!paintBall.isEmpty()) {
                final AEColor newColor = this.getColorFromItem(paintBall);
                if (newColor != null && this.getAECurrentPower(is) > 100) {
                    int color = -1;
                    if (newColor.dye != null) {
                        color = newColor.dye.getId();
                    }
                    var behaviour = new TempColorSprayBehaviour(color);
                    behaviour.onItemUseFirst(ItemStack.EMPTY, context);
                    if (behaviour.used) {
                        consumeItem(is, paintBallKey, false);
                        return InteractionResult.sidedSuccess(level.isClientSide());
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }
}
