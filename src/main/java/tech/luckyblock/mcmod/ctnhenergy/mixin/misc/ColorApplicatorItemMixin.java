package tech.luckyblock.mcmod.ctnhenergy.mixin.misc;

import appeng.api.config.Actionable;
import appeng.api.implementations.blockentities.IColorableBlockEntity;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.StorageCells;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalBlockPos;
import appeng.block.paint.PaintSplotchesBlock;
import appeng.blockentity.misc.PaintSplotchesBlockEntity;
import appeng.items.tools.powered.ColorApplicatorItem;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import appeng.me.helpers.PlayerSource;
import appeng.util.Platform;
import com.gregtechceu.gtceu.common.item.ColorSprayBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.DoubleSupplier;

import static com.gregtechceu.gtceu.common.data.GTItems.SPRAY_EMPTY;

@Mixin(value = ColorApplicatorItem.class, remap = false)
public abstract class ColorApplicatorItemMixin extends AEBasePoweredItem {


    public ColorApplicatorItemMixin(DoubleSupplier powerCapacity, Properties props) {
        super(powerCapacity, props);
    }

    @Shadow
    public ItemStack getColor(ItemStack is) {return null;}

    @Shadow
    private AEColor getColorFromItem(ItemStack paintBall) {return null;}

    @Shadow
    public boolean consumeItem(ItemStack applicator, AEItemKey paintItem, boolean simulate) {
        return false;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack is = context.getItemInHand();
        Direction side = context.getClickedFace();
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
                    if(newColor.dye != null){
                        color = newColor.dye.getId();
                    }
                    var behaviour = new ColorSprayBehaviour(() -> SPRAY_EMPTY.asStack(), 1024, color);
                    var result = behaviour.onItemUseFirst(ItemStack.EMPTY, context);
                    if(result.shouldAwardStats()){
                        consumeItem(is, paintBallKey, false);
                        return result;
                    }
                    else
                        return InteractionResult.PASS;
                }
            }
        }

        return InteractionResult.PASS;
    }
}
