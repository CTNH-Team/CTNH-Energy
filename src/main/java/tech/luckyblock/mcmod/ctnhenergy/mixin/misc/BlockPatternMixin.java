package tech.luckyblock.mcmod.ctnhenergy.mixin.misc;

import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockState;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.common.block.QuantumComputerCasingBlock;

@Mixin(BlockPattern.class)
public class BlockPatternMixin {
    @Inject(method = "checkPatternAt(Lcom/gregtechceu/gtceu/api/pattern/MultiblockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/core/Direction;ZZ)Z", at = @At(value = "INVOKE", target = "Lcom/gregtechceu/gtceu/api/pattern/TraceabilityPredicate;test(Lcom/gregtechceu/gtceu/api/pattern/MultiblockState;)Z"),remap = false)
    private void injectCode(MultiblockState worldState, BlockPos centerPos, Direction frontFacing, Direction upwardsFacing, boolean isFlipped, boolean savePredicate, CallbackInfoReturnable<Boolean> cir) {
        if (worldState.getBlockState().getBlock() instanceof QuantumComputerCasingBlock) {
            worldState.getMatchContext().getOrCreate("qcCasings", LongOpenHashSet::new)
                    .add(worldState.getPos().asLong());
        }

    }
}
