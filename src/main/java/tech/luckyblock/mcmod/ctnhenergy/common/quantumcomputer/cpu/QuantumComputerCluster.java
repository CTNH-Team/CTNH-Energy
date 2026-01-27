package tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.cpu;

import appeng.api.config.CpuSelectionMode;
import appeng.api.config.Settings;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.CraftingJobStatus;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.crafting.ICraftingSubmitResult;
import appeng.api.networking.events.GridCraftingCpuChange;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.GenericStack;
import appeng.api.util.IConfigManager;
import appeng.crafting.CraftingPlan;
import appeng.crafting.execution.CraftingSubmitResult;
import appeng.crafting.inv.ListCraftingInventory;
import appeng.me.cluster.IAECluster;
import appeng.me.cluster.MBCalculator;
import appeng.me.helpers.MachineSource;
import appeng.util.ConfigManager;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.port.QuantumComputerMENetworkPortBlockEntity;

import java.util.*;

public class QuantumComputerCluster implements IAECluster{
    private final BlockPos boundsMin;
    private final BlockPos boundsMax;

    private final HashMap<ICraftingPlan, VirtualCraftingCPU> activeCpus = new HashMap<>();
    private VirtualCraftingCPU remainingStorageCpu;
    @Setter
    private QuantumComputerMENetworkPortBlockEntity meNetworkPortBlockEntity;

//    private final List<CraftingMonitorBlockEntity> status = new ArrayList<>();

    private final ConfigManager configManager = new ConfigManager(this::markDirty);

    private Component myName = null;
    private boolean isDestroyed = false;
    @Setter
    private MachineSource machineSrc = null;

    public QuantumComputerCluster(BlockPos boundsMin, BlockPos boundsMax) {
        this.boundsMin = boundsMin.immutable();
        this.boundsMax = boundsMax.immutable();

        this.configManager.registerSetting(Settings.CPU_SELECTION_MODE, CpuSelectionMode.ANY);
    }

    @Override
    public boolean isDestroyed() {
        return this.isDestroyed;
    }

    @Override
    public BlockPos getBoundsMin() {
        return this.boundsMin;
    }

    @Override
    public BlockPos getBoundsMax() {
        return this.boundsMax;
    }

    @Override
    public void updateStatus(boolean b) {
//        for (QuantumComputerMENetworkPortBlockEntity r : this.blockEntities) {
//            r.updateSubType(true);
//        }
        meNetworkPortBlockEntity.updateSubType(true);
    }

    @Override
    public void destroy() {
        if (this.isDestroyed) {
            return;
        }
        this.isDestroyed = true;

        boolean ownsModification = !MBCalculator.isModificationInProgress();
        if (ownsModification) {
            MBCalculator.setModificationInProgress(this);
        }
        try {
            updateGridForChangedCpu(null);
        } finally {
            if (ownsModification) {
                MBCalculator.setModificationInProgress(null);
            }
        }
    }

    private void updateGridForChangedCpu(QuantumComputerCluster cluster) {
//        var posted = false;
//        for (QuantumComputerMENetworkPortBlockEntity r : this.blockEntities) {
//            final IGridNode n = r.getActionableNode();
//            if (n != null && !posted) {
//                n.getGrid().postEvent(new GridCraftingCpuChange(n));
//                posted = true;
//            }
//
//            r.updateStatus(cluster);
//        }
        final IGridNode gridNode = this.meNetworkPortBlockEntity.getActionableNode();
        if (gridNode != null) {
            gridNode.getGrid().postEvent(new GridCraftingCpuChange(gridNode));
        }

        this.meNetworkPortBlockEntity.updateStatus(cluster);
    }

    @Override
    public Iterator<QuantumComputerMENetworkPortBlockEntity> getBlockEntities() {
        ArrayList<QuantumComputerMENetworkPortBlockEntity> arr = new ArrayList<>();
        arr.add(meNetworkPortBlockEntity);
        return arr.iterator();
    }

    public List<ListCraftingInventory> getInventories() {
        List<ListCraftingInventory> list = new ArrayList<>();
        for (var cpu : this.activeCpus.values()) {
            list.add(cpu.getInventory());
        }
        return list;
    }

    public long getRemainingStorage(){
        long usedStorage = 0;
        for (var plan : activeCpus.keySet()) {
            usedStorage += plan.bytes();
        }

        return this.meNetworkPortBlockEntity.getTotalStorage() - usedStorage;
    }

    public void markDirty() {
        this.getCore().saveChanges();
    }

//    public void updateOutput(GenericStack finalOutput) {
//        var send = finalOutput;
//
//        if (finalOutput != null && finalOutput.amount() <= 0) {
//            send = null;
//        }
//
//        for (var t : this.status) {
//            t.setJob(send);
//        }
//    }

    public IActionSource getSrc() {
        return Objects.requireNonNull(this.machineSrc);
    }

    private QuantumComputerMENetworkPortBlockEntity getCore() {
        if (this.machineSrc == null) {
            return null;
        }
        return (QuantumComputerMENetworkPortBlockEntity) this.machineSrc.machine().get();
    }

    @Nullable
    public IGrid getGrid() {
        IGridNode node = getNode();
        return node != null ? node.getGrid() : null;
    }

    public void cancelJobs() {
        for (var plan : activeCpus.keySet()) {
            killCpu(plan, false);
        }
    }

    public void cancelJob(ICraftingPlan plan) {
        var cpu = activeCpus.get(plan);
        if (cpu != null) {
            killCpu(plan);
        }
    }

    public ICraftingSubmitResult submitJob(
            IGrid grid, ICraftingPlan plan, IActionSource src, ICraftingRequester requestingMachine) {
        // Check that the node is active.
        if (!isActive()) return CraftingSubmitResult.CPU_OFFLINE;
        // Check bytes.
        if (getAvailableStorage() < plan.bytes()) return CraftingSubmitResult.CPU_TOO_SMALL;

        var newCpu = new VirtualCraftingCPU(this, plan);

        var submitResult = newCpu.craftingLogic.trySubmitJob(grid, plan, src, requestingMachine);
        if (submitResult.successful()) {
            this.activeCpus.put(plan, newCpu);
            updateGridForChangedCpu(this);
        }
        return submitResult;
    }

    private void killCpu(ICraftingPlan plan, boolean updateGrid) {
        var cpu = this.activeCpus.get(plan);
        cpu.craftingLogic.cancel();
        cpu.craftingLogic.markForDeletion();
        if (updateGrid) {
            updateGridForChangedCpu(this);
        }
    }

    private void killCpu(ICraftingPlan plan) {
        killCpu(plan, true);
    }

    protected void deactivate(ICraftingPlan plan) {
        this.activeCpus.remove(plan);
        updateGridForChangedCpu(this);
    }

    public List<VirtualCraftingCPU> getActiveCPUs() {
        var list = new ArrayList<VirtualCraftingCPU>();
        var killList = new ArrayList<ICraftingPlan>();
        for (var cpuEntry : activeCpus.entrySet()) {
            var cpu = cpuEntry.getValue();
            if (cpu.craftingLogic.hasJob() || cpu.craftingLogic.isMarkedForDeletion()) {
                list.add(cpu);
            } else {
                killList.add(cpuEntry.getKey());
            }
        }
        for (var cpu : killList) {
            killCpu(cpu);
        }

        return list;
    }

    public VirtualCraftingCPU getRemainingCapacityCPU() {
        if (this.remainingStorageCpu == null
                || this.remainingStorageCpu.getAvailableStorage() != getRemainingStorage()) {
            this.remainingStorageCpu = new VirtualCraftingCPU(this, getRemainingStorage());
        }
        return this.remainingStorageCpu;
    }

    @Nullable
    public CraftingJobStatus getJobStatus(ICraftingPlan plan) {
        var cpu = activeCpus.get(plan);
        if (cpu != null) {
            return cpu.getJobStatus();
        }
        return null;
    }

    public long getAvailableStorage() {
        return getRemainingStorage();
    }

    public int getCoProcessors() {
        return this.meNetworkPortBlockEntity.getCoprocessing();
    }

    public int getMaxMultiplier(){
        return meNetworkPortBlockEntity.getMaxMultiplier();
    }

    public Component getName() {
        return this.myName;
    }

    @Nullable
    public IGridNode getNode() {
        QuantumComputerMENetworkPortBlockEntity core = getCore();
        return core != null ? core.getActionableNode() : null;
    }

    public boolean isActive() {
        IGridNode node = getNode();
        return node != null && node.isActive();
    }

    public void writeToNBT(CompoundTag data) {
        ListTag listCpus = new ListTag();
        for (var cpu : activeCpus.entrySet()) {
            if (cpu != null) {
                CompoundTag keyTag = new CompoundTag();
                writeCraftingPlanToNBT(cpu.getKey(), keyTag);
                CompoundTag cpuTag = new CompoundTag();
                cpu.getValue().writeToNBT(cpuTag);
                CompoundTag pair = new CompoundTag();
                pair.put("key", keyTag);
                pair.put("cpu", cpuTag);
                listCpus.add(pair);
            }
        }
        data.put("cpuList", listCpus);
        this.configManager.writeToNBT(data);
    }

    private void writeCraftingPlanToNBT(ICraftingPlan plan, CompoundTag tag) {
        CompoundTag outputTag = GenericStack.writeTag(plan.finalOutput());
        tag.put("output", outputTag);
        tag.putLong("bytes", plan.bytes());
        tag.putBoolean("simulation", plan.simulation());
        tag.putBoolean("multiplePaths", plan.multiplePaths());
    }

    void done() {
        final QuantumComputerMENetworkPortBlockEntity core = this.getCore();

        if (core.getPreviousState() != null) {
            this.readFromNBT(core.getPreviousState());
            core.setPreviousState(null);
        }

        this.updateName();
    }

    public void readFromNBT(CompoundTag data) {
        ListTag cpuList = (ListTag) data.get("cpuList");
        if (cpuList != null) {
            for (var x = 0; x < cpuList.size(); x++) {
                CompoundTag pair = cpuList.getCompound(x);
                var plan = readCraftingPlanFromNBT(pair.getCompound("key"));
                var cpu = new VirtualCraftingCPU(this, plan);
                this.activeCpus.put(plan, cpu);
                cpu.readFromNBT(pair.getCompound("cpu"));
            }
        }
        this.configManager.readFromNBT(data);
    }

    private CraftingPlan readCraftingPlanFromNBT(CompoundTag tag) {
        GenericStack output = GenericStack.readTag(tag.getCompound("output"));
        long bytes = tag.getLong("bytes");
        boolean simulation = tag.getBoolean("simulation");
        boolean multiplePaths = tag.getBoolean("multiplePaths");
        return new CraftingPlan(output, bytes, simulation, multiplePaths, null, null, null, null);
    }

    public void updateName() {
        this.myName = null;
        if (meNetworkPortBlockEntity.hasCustomName()) {
            if (this.myName != null) {
                this.myName.copy().append(" ").append(meNetworkPortBlockEntity.getCustomName());
            } else {
                this.myName = meNetworkPortBlockEntity.getCustomName().copy();
            }
        }
    }

    public Level getLevel() {
        return this.getCore().getLevel();
    }

    public CpuSelectionMode getSelectionMode() {
        return this.configManager.getSetting(Settings.CPU_SELECTION_MODE);
    }

    public IConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Checks if this CPU cluster can be automatically selected for a crafting request by the given action source.
     */
    public boolean canBeAutoSelectedFor(IActionSource source) {
        return switch (getSelectionMode()) {
            case ANY -> true;
            case PLAYER_ONLY -> source.player().isPresent();
            case MACHINE_ONLY -> source.player().isEmpty();
        };
    }

    /**
     * Checks if this CPU cluster is preferred for crafting requests by the given action source.
     */
    public boolean isPreferredFor(IActionSource source) {
        return switch (getSelectionMode()) {
            case ANY -> false;
            case PLAYER_ONLY -> source.player().isPresent();
            case MACHINE_ONLY -> source.player().isEmpty();
        };
    }


//    public void breakCluster() {
//        if (meNetworkPortBlockEntity != null) {
//            meNetworkPortBlockEntity.breakCluster();
//        }
//    }
}
