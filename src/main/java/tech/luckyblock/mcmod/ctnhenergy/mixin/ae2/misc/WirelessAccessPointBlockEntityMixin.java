package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.misc;

import appeng.blockentity.grid.AENetworkInvBlockEntity;
import appeng.blockentity.networking.WirelessAccessPointBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WirelessAccessPointBlockEntity.class, remap = false)
public abstract class WirelessAccessPointBlockEntityMixin extends AENetworkInvBlockEntity {
    public WirelessAccessPointBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Inject(method = "getRange", at = @At("HEAD"), cancellable = true)
    void noLimitInVoid(CallbackInfoReturnable<Double> cir){
        if(getLevel() != null && getLevel().dimensionTypeId().location().equals(ResourceLocation.tryBuild("javd", "void"))){
            cir.setReturnValue((double) Integer.MAX_VALUE);
        }
    }
}
