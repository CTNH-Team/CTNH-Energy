package tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.port;

import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.gregtechceu.gtceu.api.block.property.GTBlockStateProperties;
import com.gregtechceu.gtceu.api.data.RotationState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import tech.luckyblock.mcmod.ctnhenergy.registry.AEMenus;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEBlocks;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author aaaAlant
 * @date 2025/8/11 23:43
 **/
public class QuantumComputerMENetworkPortBlock extends
        AEBaseEntityBlock<QuantumComputerMENetworkPortBlockEntity>

{
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light_level", 0, 15);
    //RotationState rotationState = RotationState.ALL;

    public QuantumComputerMENetworkPortBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(defaultBlockState()
                .setValue(FORMED, false)
                .setValue(POWERED, false)
                .setValue(LIGHT_LEVEL, 0)
//                .setValue(GTBlockStateProperties.UPWARDS_FACING, Direction.NORTH)
//                .setValue(rotationState.property, rotationState.defaultDirection)
        );

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
        builder.add(FORMED);
        builder.add(LIGHT_LEVEL);
//        builder.add(RotationState.ALL.property);
//        builder.add(GTBlockStateProperties.UPWARDS_FACING);
    }

    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    @Override
    public @NotNull BlockState updateShape(
            BlockState stateIn,
            Direction facing,
            BlockState facingState,
            LevelAccessor level,
            BlockPos currentPos,
            BlockPos facingPos) {
        BlockEntity te = level.getBlockEntity(currentPos);
        if (te != null) {
            te.requestModelDataUpdate();
        }
        return super.updateShape(stateIn, facing, facingState, level, currentPos, facingPos);
    }


    @Override
    public InteractionResult use(
            BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof QuantumComputerMENetworkPortBlockEntity be && be.isFormed() && be.isActive()) {
            if (!level.isClientSide()) {
                MenuOpener.open(AEMenus.QUANTUM_COMPUTER.get(), player, MenuLocators.forBlockEntity(be));
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return super.use(state, level, pos, player, hand, hit);
    }
}
