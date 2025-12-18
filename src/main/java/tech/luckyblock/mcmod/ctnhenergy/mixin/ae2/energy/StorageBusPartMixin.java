package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.energy;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.parts.IPartItem;
import appeng.me.energy.IEnergyOverlayGridConnection;
import appeng.me.service.EnergyService;
import appeng.parts.AEBasePart;
import appeng.parts.storagebus.StorageBusPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Mixin(value = StorageBusPart.class, remap = false)
public abstract class StorageBusPartMixin extends AEBasePart{
    public StorageBusPartMixin(IPartItem<?> partItem) {
        super(partItem);
    }

    @Inject(method = "<init>",  at = @At("TAIL"))
    @SuppressWarnings("all")
    private void EnergyOverlayGridConnection(IPartItem<?> partItem, CallbackInfo ci){
        getMainNode().addService(IEnergyOverlayGridConnection.class, this::CE$getOtherEnergyServices);
    }

    @Unique
    List<EnergyService> CE$getOtherEnergyServices() {
        var self = getHost().getBlockEntity();
        if (self == null || self.getLevel() == null) {
            return List.of();
        }

        var level = self.getLevel();
        var pos = self.getBlockPos();

        return Stream.of(getSide())
                .filter(Objects::nonNull)
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

}
