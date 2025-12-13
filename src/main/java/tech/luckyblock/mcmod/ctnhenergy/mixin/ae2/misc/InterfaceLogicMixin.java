package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.misc;

import appeng.api.networking.IManagedGridNode;
import appeng.helpers.InterfaceLogic;
import appeng.helpers.InterfaceLogicHost;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.common.me.service.EnergyDistributeService;
import tech.luckyblock.mcmod.ctnhenergy.common.me.service.IEnergyDistributor;

import java.util.List;

@Mixin(value = InterfaceLogic.class, remap = false)
public class InterfaceLogicMixin implements IEnergyDistributor {
    @Shadow
    @Final
    protected IManagedGridNode mainNode;

    @Shadow
    @Final
    protected InterfaceLogicHost host;

    @Inject(method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/InterfaceLogicHost;Lnet/minecraft/world/item/Item;I)V",
    at = @At("TAIL"))
    private void addEnergyService(IManagedGridNode gridNode, InterfaceLogicHost host, Item is, int slots, CallbackInfo ci){
        gridNode.addService(IEnergyDistributor.class, this);
    }

    @Override
    public boolean isActive() {
        return mainNode.isActive();
    }

    @Override
    public BlockEntity getHostBlockEntity() {
        return host.getBlockEntity();
    }

    @Unique
    private EnergyDistributeService CE$service = null;
    @Unique
    private List<Direction> CE$sides = List.of();

    @Override
    public List<Direction> getAvailableSides() {
        return CE$sides;
    }

    @Override
    public void setAAvailableSides(List<Direction> sides) {
        CE$sides = sides;
    }

    @Override
    public EnergyDistributeService getService() {
        return CE$service;
    }

    @Override
    public void setService(EnergyDistributeService service) {
        CE$service = service;
    }

    @Override
    public Object getHost() {
        return host;
    }

    @Inject(method = "onUpgradesChanged", at = @At("TAIL"))
    private void notifyUpgrade(CallbackInfo ci) {
        updateSleep();
    }
}
