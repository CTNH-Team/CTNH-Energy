package tech.luckyblock.mcmod.ctnhenergy.mixin.omni;

import appeng.blockentity.crafting.CraftingBlockEntity;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import com.wintercogs.ae2omnicells.common.blocks.entities.OmniCraftingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tech.luckyblock.mcmod.ctnhenergy.api.IAutoMultiplyCPU;

@Mixin(value = OmniCraftingBlockEntity.class, remap = false)
public class OmniCraftingBlockEntityMixin extends CraftingBlockEntity {
    public OmniCraftingBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Override
    public CraftingCPUCluster getCluster() {
        var cluster = super.getCluster();
        if(cluster!=null && cluster.craftingLogic instanceof IAutoMultiplyCPU autoMultiplyCPU)
            autoMultiplyCPU.setEnableMultiply(true);
        return super.getCluster();
    }
}
