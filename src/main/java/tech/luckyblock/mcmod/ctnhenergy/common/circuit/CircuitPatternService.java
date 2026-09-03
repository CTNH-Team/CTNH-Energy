package tech.luckyblock.mcmod.ctnhenergy.common.circuit;

import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.trait.ProgrammableCircuitSlotTrait;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionHost;
import appeng.api.parts.IPartHost;
import appeng.parts.storagebus.StorageBusPart;
import tech.luckyblock.mcmod.ctnhenergy.api.ICircuitPattern;

import java.util.*;

/** PCC's circuit behavior, kept independent from the PCC mod API. */
public final class CircuitPatternService {

    private CircuitPatternService() {}

    public static ItemStack updatePattern(ItemStack source, boolean enabled) {
        if (!enabled) return source;
        var copy = source.copy();
        var root = copy.getOrCreateTag();
        var inputs = root.getList("in", net.minecraft.nbt.Tag.TAG_COMPOUND);
        var circuit = CircuitPatternData.findCircuit(inputs);
        CircuitPatternData.write(root, circuit.orElse(ICircuitPattern.NO_CIRCUIT));
        if (circuit.isPresent()) CircuitPatternData.removeCircuit(inputs);
        return copy;
    }

    public static void apply(IPatternDetails details, Level level, List<BlockPos> targets) {
        if (!(details instanceof ICircuitPattern pattern)) return;
        int number = pattern.CE$getCircuitNumber();
        if (number == ICircuitPattern.NO_CIRCUIT || level == null) return;
        for (BlockPos pos : targets) {
            var machine = SimpleTieredMachine.getMachine(level, pos);
            if(machine == null) return;
            var circuitSlot = machine.getTrait(ProgrammableCircuitSlotTrait.class);
            if (circuitSlot != null) {
                setCircuit(circuitSlot.getStorage(), number);
            }
        }
    }

    public static void setCircuit(IItemHandlerModifiable inventory, int number) {
        inventory.setStackInSlot(0, CircuitPatternData.stack(number));
    }

    public static List<BlockPos> resolveTargets(Level level, BlockPos root, Direction direction, int maxDepth) {
        var result = new ArrayList<BlockPos>();
        var queue = new ArrayDeque<Node>();
        var visited = new HashSet<Node>();
        queue.add(new Node(root, direction, 0));
        while (!queue.isEmpty()) {
            var node = queue.remove();
            if (!visited.add(node) || node.depth > maxDepth) continue;
            var children = resolveStorageBusTargets(level, node.pos, node.side.getOpposite());
            if (children.isEmpty()) result.add(node.pos);
            else for (var child : children) queue.add(new Node(child.getA(), child.getB(), node.depth + 1));
        }
        return result.isEmpty() ? List.of(root.relative(direction)) : result;
    }

    private static List<Tuple<BlockPos, Direction>> resolveStorageBusTargets(Level level, BlockPos pos,
                                                                             Direction side) {
        var host = level.getBlockEntity(pos);
        IActionHost actionHost = host instanceof IActionHost h ? h : null;
        if (actionHost == null && host instanceof IPartHost parts && parts.getPart(side) instanceof IActionHost h)
            actionHost = h;
        if (actionHost == null || actionHost.getActionableNode() == null ||
                actionHost.getActionableNode().getGrid() == null) {
            return List.of();
        }
        IGrid grid = actionHost.getActionableNode().getGrid();
        var result = new ArrayList<Tuple<BlockPos, Direction>>();
        for (StorageBusPart bus : grid.getMachines(StorageBusPart.class)) {
            var busPos = bus.getBlockEntity().getBlockPos();
            var busSide = bus.getSide();
            result.add(new Tuple<>(busPos.relative(busSide), busSide.getOpposite()));
        }
        return result;
    }

    private record Node(BlockPos pos, Direction side, int depth) {}
}
