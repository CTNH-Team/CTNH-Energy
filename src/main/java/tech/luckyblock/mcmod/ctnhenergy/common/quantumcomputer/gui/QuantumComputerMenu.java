package tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.gui;

import appeng.api.config.CpuSelectionMode;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.stacks.GenericStack;
import appeng.menu.guisync.GuiSync;
import appeng.menu.guisync.PacketWritable;
import appeng.menu.me.crafting.CraftingCPUMenu;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.cpu.VirtualCraftingCPU;
import tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.port.QuantumComputerMENetworkPortBlockEntity;
import tech.luckyblock.mcmod.ctnhenergy.registry.AEMenus;

import java.util.*;

public class QuantumComputerMenu extends CraftingCPUMenu {

    private static final CraftingCpuList EMPTY_CPU_LIST = new CraftingCpuList(Collections.emptyList());

    private static final Comparator<CraftingCpuListEntry> CPU_COMPARATOR = Comparator.comparing(
                    (CraftingCpuListEntry e) -> e.name() == null)
            .thenComparing(e -> e.name() != null ? e.name().getString() : "")
            .thenComparingInt(CraftingCpuListEntry::serial);

    private static final String ACTION_SELECT_CPU = "selectCpu";

    private WeakHashMap<ICraftingCPU, Integer> cpuSerialMap = new WeakHashMap<>();

    private int nextCpuSerial = 1;

    private List<VirtualCraftingCPU> lastCpuSet = List.of();

    private int lastUpdate = 0;

    @GuiSync(8)
    public CraftingCpuList cpuList = EMPTY_CPU_LIST;

    // This is server-side
    @Nullable
    private ICraftingCPU selectedCpu = null;

    @Getter
    @GuiSync(9)
    private int selectedCpuSerial = -1;

    @Getter
    @GuiSync(10)
    public CpuSelectionMode selectionMode = CpuSelectionMode.ANY;

    private final QuantumComputerMENetworkPortBlockEntity host;

    public QuantumComputerMenu(int id, Inventory ip, QuantumComputerMENetworkPortBlockEntity te) {
        super(AEMenus.QUANTUM_COMPUTER.get(), id, ip, te);
        this.host = te;

        if (te != null && te.getCluster() != null) {
            selectionMode = te.getCluster().getSelectionMode();
        }

        this.registerClientAction(ACTION_SELECT_CPU, Integer.class, this::selectCpu);
    }

    @Override
    protected void setCPU(ICraftingCPU c) {
        super.setCPU(c);
        this.selectedCpuSerial = getOrAssignCpuSerial(c);
    }

    @Override
    public void broadcastChanges() {
        if (this.host == null) {
            super.broadcastChanges();
            return;
        }

        if (isServerSide() && this.host.getCluster() != null) {
            List<VirtualCraftingCPU> newCpuSet = this.host.getCluster().getActiveCPUs();
            newCpuSet.add(this.host.getCluster().getRemainingCapacityCPU());
            if (!lastCpuSet.equals(newCpuSet)
                    // Always try to update once every second to show job progress
                    || ++lastUpdate >= 20) {
                lastCpuSet = newCpuSet;
                cpuList = createCpuList();
            }
        } else {
            lastUpdate = 20;
            if (!lastCpuSet.isEmpty()) {
                cpuList = EMPTY_CPU_LIST;
                lastCpuSet = List.of();
            }
        }

        // Clear selection if CPU is no longer in list
        if (selectedCpuSerial != -1) {
            if (cpuList.cpus().stream().noneMatch(c -> c.serial() == selectedCpuSerial)) {
                selectCpu(-1);
            }
        }

        // Select a suitable CPU if none is selected
        if (selectedCpuSerial == -1) {
            // Try busy CPUs first
            for (var cpu : cpuList.cpus()) {
                if (cpu.currentJob() != null) {
                    selectCpu(cpu.serial());
                    break;
                }
            }
            // If we couldn't find a busy one, just select the first
            if (selectedCpuSerial == -1 && !cpuList.cpus().isEmpty()) {
                selectCpu(cpuList.cpus().get(0).serial());
            }
        }

        if (this.host.getCluster() != null) {
            selectionMode = this.host.getCluster().getSelectionMode();
        }

        super.broadcastChanges();
    }

    private CraftingCpuList createCpuList() {
        var entries = new ArrayList<CraftingCpuListEntry>(lastCpuSet.size());
        for (var cpu : lastCpuSet) {
            var serial = getOrAssignCpuSerial(cpu);
            var status = cpu.getJobStatus();
            var progress = 0f;
            if (status != null && status.totalItems() > 0) {
                progress = (float) (status.progress() / (double) status.totalItems());
            }
            entries.add(new CraftingCpuListEntry(
                    serial,
                    cpu.getAvailableStorage(),
                    cpu.getCoProcessors(),
                    cpu.getName(),
                    cpu.getSelectionMode(),
                    status != null ? status.crafting() : null,
                    progress,
                    status != null ? status.elapsedTimeNanos() : 0));
        }
        entries.sort(CPU_COMPARATOR);
        return new CraftingCpuList(entries);
    }

    private int getOrAssignCpuSerial(ICraftingCPU cpu) {
        if (this.cpuSerialMap == null) {
            this.cpuSerialMap = new WeakHashMap<>();
        }
        return cpuSerialMap.computeIfAbsent(cpu, ignored -> nextCpuSerial++);
    }

    @Override
    public boolean allowConfiguration() {
        return false;
    }

    public void selectCpu(int serial) {
        if (isClientSide()) {
            selectedCpuSerial = serial;
            sendClientAction(ACTION_SELECT_CPU, serial);
        } else {
            ICraftingCPU newSelectedCpu = null;
            if (serial != -1) {
                for (var cpu : lastCpuSet) {
                    if (cpuSerialMap.getOrDefault(cpu, -1) == serial) {
                        newSelectedCpu = cpu;
                        break;
                    }
                }
            }

            if (newSelectedCpu != selectedCpu) {
                setCPU(newSelectedCpu);
            }
        }
    }

    public record CraftingCpuList(List<CraftingCpuListEntry> cpus) implements PacketWritable {
        public CraftingCpuList(FriendlyByteBuf data) {
            this(readFromPacket(data));
        }

        private static List<CraftingCpuListEntry> readFromPacket(FriendlyByteBuf data) {
            var count = data.readInt();
            var result = new ArrayList<CraftingCpuListEntry>(count);
            for (int i = 0; i < count; i++) {
                result.add(CraftingCpuListEntry.readFromPacket(data));
            }
            return result;
        }

        @Override
        public void writeToPacket(FriendlyByteBuf data) {
            data.writeInt(cpus.size());
            for (var entry : cpus) {
                entry.writeToPacket(data);
            }
        }
    }

    public record CraftingCpuListEntry(
            int serial,
            long storage,
            int coProcessors,
            Component name,
            CpuSelectionMode mode,
            GenericStack currentJob,
            float progress,
            long elapsedTimeNanos) {
        public static CraftingCpuListEntry readFromPacket(FriendlyByteBuf data) {
            return new CraftingCpuListEntry(
                    data.readInt(),
                    data.readLong(),
                    data.readInt(),
                    data.readBoolean() ? data.readComponent() : null,
                    data.readEnum(CpuSelectionMode.class),
                    GenericStack.readBuffer(data),
                    data.readFloat(),
                    data.readVarLong());
        }

        public void writeToPacket(FriendlyByteBuf data) {
            data.writeInt(serial);
            data.writeLong(storage);
            data.writeInt(coProcessors);
            data.writeBoolean(name != null);
            if (name != null) {
                data.writeComponent(name);
            }
            data.writeEnum(mode);
            GenericStack.writeBuffer(currentJob, data);
            data.writeFloat(progress);
            data.writeVarLong(elapsedTimeNanos);
        }
    }
}
