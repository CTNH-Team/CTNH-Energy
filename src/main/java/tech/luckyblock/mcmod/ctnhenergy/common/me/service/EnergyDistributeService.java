package tech.luckyblock.mcmod.ctnhenergy.common.me.service;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridService;
import appeng.api.networking.IGridServiceProvider;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.utils.CEUtil;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class EnergyDistributeService implements IGridService, IGridServiceProvider {

    private final Map<IGridNode, IEnergyDistributor> distributors = new IdentityHashMap<>();
    // IdentityHashMap is faster
    private final Set<IEnergyDistributor> activeNodes = Collections.newSetFromMap(new IdentityHashMap<>());

    private final IGrid grid;

    @Getter
    int voltageTier = -1;

    public EnergyDistributeService(IGrid grid) {
        this.grid = grid;
    }

    @Override
    public void onLevelStartTick(Level level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getServer().getTickCount() % 100 == 0) {
            voltageTier = -1;
            voltageTier = CEUtil.getGridTier(grid.getPivot());
            for (var dis : this.distributors.values()) {
                dis.updateSleep();
            }
        }
    }

    @Override
    public void onServerEndTick() {
        for (var dis : this.activeNodes) {
            if (dis.isActive()) {
                dis.distribute();
            }
        }
    }

    @Override
    public void removeNode(IGridNode gridNode) {
        var node = this.distributors.get(gridNode);
        if (node != null) {
            node.setServiceHost(null);
            this.activeNodes.remove(node);
            this.distributors.remove(gridNode);
        }
    }

    @Override
    public void addNode(IGridNode gridNode, @Nullable CompoundTag savedData) {
        var distributor = gridNode.getService(IEnergyDistributor.class);
        if (distributor != null) {
            this.distributors.put(gridNode, distributor);
            distributor.setServiceHost(this);
        }
    }

    public void wake(IEnergyDistributor node) {
        this.activeNodes.add(node);
    }

    public void sleep(IEnergyDistributor node) {
        this.activeNodes.remove(node);
    }
}
