package tech.luckyblock.mcmod.ctnhenergy.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public class QuantumComputerCasingBlock extends Block {
    public static final EnumProperty<State> STATE =
            EnumProperty.create("state", State.class);

    public enum State implements StringRepresentable {
        RED("red"),
        GREY("grey"),
        PURPLE("purple"),
        BLUE("blue");

        private final String name;

        State(String name) {
            this.name = name;
        }

        public @NotNull String getSerializedName() {
            return name;
        }

        // 获取下一个状态（循环）
        public State next() {
            return values()[(this.ordinal() + 1) % values().length];
        }
    }

    public QuantumComputerCasingBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(STATE, State.GREY));
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(STATE).equals(State.GREY) ? 0 : 15;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE);
    }
}
