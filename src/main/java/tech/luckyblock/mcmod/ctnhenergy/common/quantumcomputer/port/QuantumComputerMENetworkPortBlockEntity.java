package tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.port;

import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridMultiblock;
import appeng.api.networking.IGridNodeListener;
import appeng.api.orientation.BlockOrientation;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.crafting.CraftingCubeModelData;
import appeng.blockentity.grid.AENetworkBlockEntity;
import appeng.me.cluster.IAEMultiBlock;
import appeng.me.helpers.MachineSource;
import appeng.util.NullConfigManager;
import appeng.util.Platform;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.cpu.QuantumComputerCluster;
import tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.machine.QuantumComputerMultiblockMachine;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEBlocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author aaaAlant
 * @date 2025/8/11 22:58
 **/
public class QuantumComputerMENetworkPortBlockEntity extends AENetworkBlockEntity
        implements IAEMultiBlock<QuantumComputerCluster>, IPowerChannelState, IConfigurableObject{

//    private QuantumComputerCalculator calculator;
    @Setter
    @Getter
    private CompoundTag previousState = null;
    private QuantumComputerCluster cluster;

    @Setter
    @Getter
    private QuantumComputerMultiblockMachine machine;

    private QuantumComputerMultiblockMachine.WorkStatus workStatus;

    public QuantumComputerMENetworkPortBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
        this.getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .addService(IGridMultiblock.class, () -> Collections.singleton(this.getGridNode()).iterator())
                .setVisualRepresentation(this.getItemFromBlockEntity());
//        calculator = new QuantumComputerCalculator(this, (s, min, max) -> false);
    }

    public void active(){
        if(this.workStatus== QuantumComputerMultiblockMachine.WorkStatus.WORKING){
            return;
        }
        
        this.workStatus = QuantumComputerMultiblockMachine.WorkStatus.WORKING;
        if(cluster==null){
            cluster = new QuantumComputerCluster(getBlockPos(), getBlockPos());
            cluster.setMeNetworkPortBlockEntity(this);
            cluster.setMachineSrc(new MachineSource(this));
        }
        if (previousState != null) {
            cluster.readFromNBT(previousState);
            previousState = null;
        }
        updateStatus(this.cluster);
//        this.updateSubType(true);
    }

    public void suspend(){
        if(this.workStatus== QuantumComputerMultiblockMachine.WorkStatus.SUSPEND){
            return;
        }
        
        this.workStatus = QuantumComputerMultiblockMachine.WorkStatus.SUSPEND;
        if (cluster != null) {
            // 保存当前任务到 previousState
            CompoundTag state = new CompoundTag();
            cluster.writeToNBT(state);
            this.previousState = state;

            this.breakCluster();
            this.updateSubType(true);
        }
    }

    public void multiBlockBreak(){
        
        this.workStatus = null;
        this.breakCluster();
        this.machine = null;
    }

    public long getTotalStorage(){
        if (machine==null){
            return 0;
        }
        return 1024L * machine.getStorageKilobyte();
    }

    public long getRemainingStorage(){
        if(cluster==null){
            return machine.getStorageKilobyte();
        }
        return cluster.getRemainingStorage() / 1024L;
    }

    public int getCoprocessing(){
        if (machine==null){
            return 0;
        }
        return machine.getCoprocessing();
    }

    public int getMaxMultiplier(){
        if (machine==null){
            return 1;
        }
        return machine.getMaxMultiplier();
    }

    @Override
    protected Item getItemFromBlockEntity() {
//        if (this.level == null) {
//            return Items.AIR;
//        }
        return CEBlocks.QUANTUM_COMPUTER_ME_NETWORK_PORT.asItem();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        if (this.cluster != null) {
            this.cluster.updateName();
        }
    }


    public void updateStatus(QuantumComputerCluster newCluster) {
        if (this.cluster != null && this.cluster != newCluster) {
            this.breakCluster();
        }
        this.cluster = newCluster;
        this.updateSubType(true);
    }

    public void updateSubType(boolean updateFormed) {
        if (this.level == null || this.notLoaded() || this.isRemoved()) {
            return;
        }

        final BlockState currentState = this.level.getBlockState(this.worldPosition);

        final BlockState newState = currentState.setValue(QuantumComputerMENetworkPortBlock.POWERED, this.getMainNode().isOnline())
                .setValue(QuantumComputerMENetworkPortBlock.FORMED, this.isFormed());

        if (currentState != newState) {
            this.level.setBlock(this.worldPosition, newState, Block.UPDATE_CLIENTS);
        }

        if (updateFormed) {
            onGridConnectableSidesChanged();
        }
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return EnumSet.of(Direction.UP);
    }

    public boolean isFormed() {
        if (isClientSide()) {
//            return getBlockState().getValue(QuantumComputerMENetworkPortBlock.FORMED);
            return this.workStatus== QuantumComputerMultiblockMachine.WorkStatus.WORKING;
        }
        return this.cluster != null;
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        if (this.cluster != null) {
            this.cluster.writeToNBT(data);
        }
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        if (this.cluster != null) {
            this.cluster.readFromNBT(data);
        } else {
            this.setPreviousState(data.copy());
        }
    }

    @Override
    public void disconnect(boolean update) {
        if (this.cluster != null) {
            this.cluster.destroy();
            if (update) {
                this.updateSubType(true);
            }
        }
    }

    @Override
    public QuantumComputerCluster getCluster() {
        return this.cluster;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        if (reason != IGridNodeListener.State.GRID_BOOT) {
            this.updateSubType(false);
        }
    }

    @Override
    public boolean isPowered() {
        if (isClientSide()) {
            return this.level.getBlockState(this.worldPosition).getValue(QuantumComputerMENetworkPortBlock.POWERED);
        }
        return this.getMainNode().isActive();
    }

    @Override
    public boolean isActive() {
        if (!isClientSide()) {
            return this.getMainNode().isActive();
        }
        return this.isPowered() && this.isFormed();
    }

    @Override
    public @NotNull ModelData getModelData() {
        return CraftingCubeModelData.create(EnumSet.noneOf(Direction.class));
    }

//    private boolean isConnected(BlockGetter level, BlockPos pos, Direction side) {
//        BlockPos adjacentPos = pos.relative(side);
//        return level.getBlockState(adjacentPos).getBlock() instanceof AbstractCraftingUnitBlock;
//    }

    /**
     * When the block state changes (i.e. becoming formed or unformed), we need to update the model data since it
     * contains connections to neighboring block entities.
     */
    @Override
    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        requestModelDataUpdate();
    }


    @Override
    public IConfigManager getConfigManager() {
        var cluster = this.getCluster();

        if (cluster != null) {
            return this.getCluster().getConfigManager();
        } else {
            return NullConfigManager.INSTANCE;
        }
    }

    public void breakCluster() {
        if (this.cluster != null) {
            this.cluster.cancelJobs();
            var inventories = this.cluster.getInventories();

            // Drop stacks
            var places = new ArrayList<BlockPos>();
            places.add(worldPosition);

//            for (var blockEntity : (Iterable<QuantumComputerMENetworkPortBlockEntity>) this.cluster::getBlockEntities) {
//                if (this == blockEntity) {
//                    places.add(worldPosition);
//                } else {
//                    for (var d : Direction.values()) {
//                        var p = blockEntity.worldPosition.relative(d);
//
//                        if (this.level.isEmptyBlock(p)) {
//                            places.add(p);
//                        }
//                    }
//                }
//            }

//            if (places.isEmpty()) {
//                throw new IllegalStateException(
//                        this.cluster + " does not contain any kind of blocks, which were destroyed.");
//            }

            for (var inv : inventories) {
                for (var entry : inv.list) {
                    var position = Util.getRandom(places, level.getRandom());
                    var stacks = new ArrayList<ItemStack>();
                    entry.getKey().addDrops(entry.getLongValue(), stacks, this.level, position);
                    Platform.spawnDrops(this.level, position, stacks);
                }

                inv.clear(); // Ensure items only ever get dropped once
            }

            this.cluster.destroy();
        }
    }

}
