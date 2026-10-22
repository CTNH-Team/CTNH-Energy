package tech.luckyblock.mcmod.ctnhenergy.network.packets;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import com.lowdragmc.lowdraglib.networking.IHandlerContext;
import com.lowdragmc.lowdraglib.networking.IPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.security.IActionHost;
import appeng.api.stacks.AEKey;
import appeng.menu.me.crafting.CraftingCPUMenu;
import appeng.parts.AEBasePart;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.lowdragmc.lowdraglib.networking.LDLNetworking.NETWORK;

/**
 * Sent when the player shift clicks a scheduled/crafting entry inside the AE2 crafting status UI.
 * The server resolves the crafting providers holding a pattern for that key inside the grid of the
 * terminal the player has open and answers with {@link HighlightBlocksPacket}.
 */
@NoArgsConstructor
public class LocatePatternProviderPacket implements IPacket {

    private static final int MAX_HIGHLIGHTS = 32;

    private AEKey what;

    public LocatePatternProviderPacket(AEKey what) {
        this.what = what;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        AEKey.writeKey(buf, this.what);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.what = AEKey.readKey(buf);
    }

    @Override
    public void execute(IHandlerContext handler) {
        var player = handler.getPlayer();
        if (player == null || this.what == null) {
            return;
        }
        // Only the grid of the terminal the player currently has open may be inspected.
        if (!(player.containerMenu instanceof CraftingCPUMenu menu) ||
                !(menu.getTarget() instanceof IActionHost host)) {
            return;
        }
        var node = host.getActionableNode();
        if (node == null) {
            return;
        }
        var positions = findProviders(node.getGrid(), player.level());
        if (!positions.isEmpty()) {
            NETWORK.sendToPlayer(new HighlightBlocksPacket(positions), player);
        }
    }

    /**
     * Providers producing the key as their primary output win over providers producing it as a
     * by-product. Nodes of another level are skipped because the highlight is only rendered in the
     * level the player is in.
     */
    private List<BlockPos> findProviders(IGrid grid, Level level) {
        Set<BlockPos> primary = new LinkedHashSet<>();
        Set<BlockPos> byproduct = new LinkedHashSet<>();

        for (var node : grid.getNodes()) {
            if (node.getLevel() != level) {
                continue;
            }
            var provider = node.getService(ICraftingProvider.class);
            if (provider == null) {
                continue;
            }
            var pos = resolvePos(node);
            if (pos == null) {
                continue;
            }
            for (var pattern : provider.getAvailablePatterns()) {
                if (pattern == null || pattern.getOutputs().length == 0) {
                    continue;
                }
                if (this.what.equals(pattern.getPrimaryOutput().what())) {
                    primary.add(pos);
                    break;
                }
                for (var output : pattern.getOutputs()) {
                    if (output != null && this.what.equals(output.what())) {
                        byproduct.add(pos);
                        break;
                    }
                }
            }
            if (primary.size() >= MAX_HIGHLIGHTS) {
                break;
            }
        }

        List<BlockPos> found = new ArrayList<>(primary.isEmpty() ? byproduct : primary);
        return found.size() > MAX_HIGHLIGHTS ? found.subList(0, MAX_HIGHLIGHTS) : found;
    }

    @Nullable
    private static BlockPos resolvePos(IGridNode node) {
        var owner = node.getOwner();
        if (owner instanceof BlockEntity blockEntity) {
            return blockEntity.getBlockPos();
        }
        if (owner instanceof AEBasePart part) {
            var blockEntity = part.getBlockEntity();
            return blockEntity == null ? null : blockEntity.getBlockPos();
        }
        // GTCEu machines own their grid node directly instead of going through the block entity.
        if (owner instanceof MetaMachine machine) {
            return machine.getPos();
        }
        return null;
    }
}
