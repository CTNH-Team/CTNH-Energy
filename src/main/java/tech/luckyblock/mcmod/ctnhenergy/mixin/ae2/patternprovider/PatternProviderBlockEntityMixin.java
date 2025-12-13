package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.patternprovider;

import appeng.blockentity.crafting.PatternProviderBlockEntity;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PatternProviderBlockEntity.class, remap = false)
public class PatternProviderBlockEntityMixin extends BlockEntity {
    public PatternProviderBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "onReady", at = @At("TAIL"))
    public void adjustSimpleTieredMachine(CallbackInfo ci){
        if(getLevel().isClientSide) return;
        BlockPos placedPos = getBlockPos();
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = placedPos.relative(dir);
            BlockEntity be = getLevel().getBlockEntity(neighborPos);
            if(be instanceof MetaMachineBlockEntity metaMachineBlockEntity
                    && metaMachineBlockEntity.getMetaMachine() instanceof SimpleTieredMachine tieredMachine)
            {
                tieredMachine.setAutoOutputFluids(true);
                tieredMachine.setAutoOutputItems(true);
                tieredMachine.setOutputFacingFluids(dir.getOpposite());
                tieredMachine.setOutputFacingItems(dir.getOpposite());
                tieredMachine.setAllowInputFromOutputSideItems(true);
                tieredMachine.setAllowInputFromOutputSideFluids(true);
            }
        }

    }


}
