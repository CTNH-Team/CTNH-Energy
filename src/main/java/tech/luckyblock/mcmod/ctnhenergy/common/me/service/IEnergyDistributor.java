package tech.luckyblock.mcmod.ctnhenergy.common.me.service;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeService;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.me.service.EnergyService;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEItems;
import tech.luckyblock.mcmod.ctnhenergy.utils.CEUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface IEnergyDistributor extends IGridNodeService, IUpgradeableObject {
    default void distribute() {
        var self = getHostBlockEntity();
        if(self.getLevel() != null){
            for(Direction side : getAvailableSides()){
                var oppositeSide = side.getOpposite();
                var source = GTCapabilityHelper.getEnergyContainer(self.getLevel(), self.getBlockPos(), side);
                var target = GTCapabilityHelper.getEnergyContainer(self.getLevel(), self.getBlockPos().relative(side), oppositeSide);
                if(source != null && target != null && !CEUtil.isInSameGrid(source, target)){
                    if(!source.outputsEnergy(side)) continue;
                    long outputVoltage = source.getOutputVoltage();
                    long outputAmperes = Math.min(source.getEnergyStored() / outputVoltage, source.getOutputAmperage());
                    if (outputAmperes == 0) return;
                    if(target.inputsEnergy(oppositeSide)){
                        outputAmperes = target.acceptEnergyFromNetwork(oppositeSide, outputVoltage, outputAmperes);
                        source.changeEnergy(- outputAmperes * outputVoltage);
                    }
                }
            }
        }
    }

    default void setServiceHost(@Nullable EnergyDistributeService service) {
        setService(service);
        updateSleep();
        if(getService() != null){
            setAvailableSides(CEUtil.getSides(getHost()));
        }
    }

    default void updateSleep(){
        if(getService() != null){
            if(getUpgrades().isInstalled(CEItems.DYNAMO_CARD)){
                getService().wake(this);
            }
            else{
                getService().sleep(this);
            }
        }
    }

    default List<EnergyService> getOtherEnergyServices() {
        var self = getHostBlockEntity();
        if (self == null || self.getLevel() == null) {
            return List.of();
        }

        var level = self.getLevel();
        var pos = self.getBlockPos();

        return getAvailableSides().stream()
                .map(side -> (EnergyService) Optional.ofNullable(
                                GridHelper.getNodeHost(level, pos.relative(side)))
                        .map(host -> host.getGridNode(side.getOpposite()))
                        .map(IGridNode::getGrid)
                        .map(node -> node.getService(IEnergyService.class))
                        .orElse(null)
                )
                .filter(Objects::nonNull)
                .toList();
    }


    boolean isActive();

    BlockEntity getHostBlockEntity();

    List<Direction> getAvailableSides();

    void setAvailableSides(List<Direction> sides);

    EnergyDistributeService getService();

    void setService(EnergyDistributeService service);

    Object getHost();

}
