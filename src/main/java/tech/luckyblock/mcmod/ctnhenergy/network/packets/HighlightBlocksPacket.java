package tech.luckyblock.mcmod.ctnhenergy.network.packets;

import com.lowdragmc.lowdraglib.networking.IHandlerContext;
import com.lowdragmc.lowdraglib.networking.IPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import com.glodblock.github.extendedae.client.render.EAEHighlightHandler;
import com.glodblock.github.extendedae.util.MessageUtil;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * Highlights the given block positions on the client by reusing ExtendedAE's pattern access terminal
 * locate feature: {@link EAEHighlightHandler} draws the blinking outline and
 * {@link MessageUtil#createEnhancedHighlightMessage} builds the same clickable coordinate chat line.
 */
@NoArgsConstructor
public class HighlightBlocksPacket implements IPacket {

    /** Same "farther away stays visible longer" scaling ExtendedAE's highlight button uses. */
    private static final long DURATION_PER_BLOCK_MS = 600L;
    private static final long MIN_DURATION_MS = 3_000L;
    private static final long MAX_DURATION_MS = 18_000L;
    /** Every provider is highlighted, but the chat is not flooded with coordinates. */
    private static final int MAX_CHAT_LINES = 4;
    private static final String CHAT_KEY = "chat.ex_pattern_access_terminal.pos";

    private long[] positions = new long[0];

    public HighlightBlocksPacket(Collection<BlockPos> positions) {
        this.positions = positions.stream().mapToLong(BlockPos::asLong).toArray();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeLongArray(this.positions);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.positions = buf.readLongArray();
    }

    @Override
    public void execute(IHandlerContext handler) {
        var player = Minecraft.getInstance().player;
        var level = handler.getLevel();
        if (player == null || level == null) {
            return;
        }
        int lines = 0;
        for (long packed : this.positions) {
            var pos = BlockPos.of(packed);
            long distance = (long) Math.sqrt(player.blockPosition().distSqr(pos));
            long duration = Math.min(MAX_DURATION_MS,
                    Math.max(MIN_DURATION_MS, distance * DURATION_PER_BLOCK_MS));
            EAEHighlightHandler.highlight(pos, level.dimension(), System.currentTimeMillis() + duration);
            if (lines++ < MAX_CHAT_LINES) {
                player.displayClientMessage(
                        MessageUtil.createEnhancedHighlightMessage(player, pos, level.dimension(), CHAT_KEY), false);
            }
        }
    }
}
