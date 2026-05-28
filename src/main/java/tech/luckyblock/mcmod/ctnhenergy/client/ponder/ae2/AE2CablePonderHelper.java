package tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2;

import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.core.definitions.AEBlocks;
import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderSceneBuilder;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public final class AE2CablePonderHelper {

    private static final Direction[] CONNECTION_DIRECTIONS = Direction.values();
    private static final Set<ResourceLocation> KNOWN_CONNECTABLE_AE2_BLOCKS = Set.of(
            ae2("controller"),
            ae2("drive"),
            ae2("energy_acceptor"),
            ae2("energy_cell"),
            ae2("dense_energy_cell"),
            ae2("creative_energy_cell"),
            ae2("interface"),
            ae2("io_port"),
            ae2("pattern_provider"),
            ae2("molecular_assembler"),
            ae2("terminal"),
            ae2("crafting_terminal"),
            ae2("pattern_access_terminal"),
            ae2("pattern_encoding_terminal"),
            ae2("quantum_link"),
            ae2("quantum_ring"),
            ae2("quartz_fiber"),
            ae2("crafting_unit"),
            ae2("crafting_accelerator"),
            ae2("crafting_monitor"),
            ae2("1k_crafting_storage"),
            ae2("4k_crafting_storage"),
            ae2("16k_crafting_storage"),
            ae2("64k_crafting_storage"),
            ae2("256k_crafting_storage"),
            ae2("crystal_resonance_generator"));

    private final CTNHEnergyPonderSceneBuilder scene;
    private final SceneBuildingUtil util;
    private final Set<BlockPos> visiblePositions = new HashSet<>();

    public AE2CablePonderHelper(CTNHEnergyPonderSceneBuilder scene, SceneBuildingUtil util) {
        this.scene = scene;
        this.util = util;
    }

    public void showSectionAndConnect(int x, int y, int z, Direction direction) {
        showSectionAndConnect(util.grid().at(x, y, z), direction);
    }

    public void showSectionAndConnect(int x1, int y1, int z1, int x2, int y2, int z2, Direction direction) {
        showSectionAndConnect(util.grid().at(x1, y1, z1), util.grid().at(x2, y2, z2), direction);
    }

    public void hideSectionAndDisconnect(int x, int y, int z, Direction direction) {
        hideSectionAndDisconnect(util.grid().at(x, y, z), direction);
    }

    public void hideSectionAndDisconnect(int x1, int y1, int z1, int x2, int y2, int z2, Direction direction) {
        hideSectionAndDisconnect(util.grid().at(x1, y1, z1), util.grid().at(x2, y2, z2), direction);
    }

    public static void connectAllVisibleCableBuses(CTNHEnergyPonderSceneBuilder scene, SceneBuildingUtil util) {
        new AE2CablePonderHelper(scene, util).markVisibleAndConnect(util.grid().at(0, 0, 0), util.grid().at(8, 17, 8));
    }

    public static void connectVisibleCableBuses(CTNHEnergyPonderSceneBuilder scene, SceneBuildingUtil util,
                                                BlockPos from, BlockPos to) {
        new AE2CablePonderHelper(scene, util).markVisibleAndConnect(from, to);
    }

    public void showSectionAndConnect(BlockPos pos, Direction direction) {
        scene.world().showSection(util.select().position(pos), direction);
        markVisibleAndConnect(pos, pos);
    }

    public void showSectionAndConnect(BlockPos from, BlockPos to, Direction direction) {
        scene.world().showSection(util.select().fromTo(from, to), direction);
        markVisibleAndConnect(from, to);
    }

    public void hideSectionAndDisconnect(BlockPos pos, Direction direction) {
        hideSectionAndDisconnect(pos, pos, direction);
    }

    public void hideSectionAndDisconnect(BlockPos from, BlockPos to, Direction direction) {
        scene.world().hideSection(util.select().fromTo(from, to), direction);
        markHiddenAndConnect(from, to);
    }

    public void markVisibleAndConnect(BlockPos from, BlockPos to) {
        Set<BlockPos> newlyVisible = new HashSet<>();
        BlockPos.betweenClosed(from, to).forEach(pos -> {
            BlockPos immutablePos = pos.immutable();
            visiblePositions.add(immutablePos);
            newlyVisible.add(immutablePos);
        });
        updateConnectionsAround(newlyVisible);
    }

    public void markHiddenAndConnect(BlockPos from, BlockPos to) {
        Set<BlockPos> newlyHidden = new HashSet<>();
        BlockPos.betweenClosed(from, to).forEach(pos -> {
            BlockPos immutablePos = pos.immutable();
            visiblePositions.remove(immutablePos);
            newlyHidden.add(immutablePos);
        });
        updateConnectionsAround(newlyHidden);
    }

    private void updateConnectionsAround(Set<BlockPos> changedPositions) {
        Set<BlockPos> candidates = new HashSet<>();
        for (BlockPos pos : changedPositions) {
            candidates.add(pos);
            for (Direction direction : CONNECTION_DIRECTIONS) {
                candidates.add(pos.relative(direction));
            }
        }

        Set<BlockPos> visibleSnapshot = Set.copyOf(visiblePositions);
        for (BlockPos candidate : candidates) {
            if (visibleSnapshot.contains(candidate)) {
                connectCableBusAt(candidate, visibleSnapshot);
            }
        }
    }

    private void connectCableBusAt(BlockPos pos, Set<BlockPos> visibleSnapshot) {
        scene.world().modifyBlockEntity(pos, CableBusBlockEntity.class, cableBus -> {
            Level level = cableBus.getLevel();
            if (level == null || !isCableBus(level, pos)) return;

            EnumSet<Direction> connections = EnumSet.noneOf(Direction.class);
            for (Direction direction : CONNECTION_DIRECTIONS) {
                BlockPos neighbor = pos.relative(direction);
                if (visibleSnapshot.contains(neighbor) && isConnectableNeighbor(level, neighbor)) {
                    connections.add(direction);
                }
            }

            CompoundTag tag = cableBus.saveWithoutMetadata();
            writeCableVisualConnections(tag, connections);
            cableBus.load(tag);
            cableBus.setChanged();
            cableBus.requestModelDataUpdate();
        });
        scene.world().modifyBlockEntityNBT(util.select().position(pos), CableBusBlockEntity.class, tag -> {}, true);
    }

    static void writeCableVisualConnections(CompoundTag tag, EnumSet<Direction> connections) {
        if (!tag.contains("cable")) return;

        CompoundTag cable = tag.getCompound("cable");
        if (!cable.contains("id")) return;

        CompoundTag visual = cable.getCompound("visual");
        ListTag connectionTags = new ListTag();
        for (Direction direction : CONNECTION_DIRECTIONS) {
            if (connections.contains(direction)) {
                connectionTags.add(StringTag.valueOf(direction.getSerializedName()));
            }
        }
        visual.put("connections", connectionTags);
        cable.put("visual", visual);
        tag.put("cable", cable);
    }

    private static boolean isConnectableNeighbor(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(AEBlocks.CABLE_BUS.block())) return true;

        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (KNOWN_CONNECTABLE_AE2_BLOCKS.contains(blockId)) return true;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && "ae2".equals(blockId.getNamespace()) &&
                blockEntity.saveWithoutMetadata().contains("proxy");
    }

    private static boolean isCableBus(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(AEBlocks.CABLE_BUS.block());
    }

    private static ResourceLocation ae2(String path) {
        return ResourceLocation.tryBuild("ae2", path);
    }
}
