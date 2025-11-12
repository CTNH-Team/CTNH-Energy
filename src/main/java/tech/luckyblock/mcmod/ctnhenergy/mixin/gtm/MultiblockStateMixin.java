package tech.luckyblock.mcmod.ctnhenergy.mixin.gtm;

import com.gregtechceu.gtceu.api.pattern.MultiblockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.api.IBlockStateChangeIgnored;

@Mixin(value = MultiblockState.class, remap = false)
public class MultiblockStateMixin {
    @Inject(
            method = "onBlockStateChanged",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onBlockStateChanged(BlockPos pos, BlockState state, CallbackInfo ci) {
        if(state.getBlock() instanceof IBlockStateChangeIgnored)
            ci.cancel();
    }
}
