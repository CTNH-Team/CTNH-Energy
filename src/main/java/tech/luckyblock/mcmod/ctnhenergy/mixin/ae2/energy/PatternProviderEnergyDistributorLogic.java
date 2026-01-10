package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.energy;

import appeng.api.networking.IManagedGridNode;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.me.energy.IEnergyOverlayGridConnection;
import appeng.me.service.EnergyService;
import net.minecraft.core.Direction;
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
import tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.misc.MachineUpgradeInventoryAccessor;

import java.util.List;

@Mixin(value = PatternProviderLogic.class, remap = false, priority = 1100)
public class PatternProviderEnergyDistributorLogic implements IEnergyDistributor {
    @Shadow
    @Final
    private IManagedGridNode mainNode;

    @Shadow
    @Final
    private PatternProviderLogicHost host;

    @Inject(
            method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/patternprovider/PatternProviderLogicHost;I)V",
            at = @At("TAIL")
    )
    @SuppressWarnings("all")
    private void AddEnergyDistributorService(IManagedGridNode mainNode, PatternProviderLogicHost host, int patternInventorySize, CallbackInfo ci) {
        mainNode.addService(IEnergyDistributor.class, this)
                .addService(IEnergyOverlayGridConnection.class, this::getOtherEnergyServices);
        CE$injectUpgradeCallback();
    }

    @Unique
    private EnergyDistributeService CE$service = null;
    @Unique
    private List<Direction> CE$sides = List.of();


    @Override
    public boolean isActive() {
        return mainNode.isActive();
    }

    @Override
    public BlockEntity getHostBlockEntity() {
        return host.getBlockEntity();
    }

    @Override
    public List<Direction> getAvailableSides() {
        return CE$sides;
    }

    @Override
    public void setAvailableSides(List<Direction> sides) {
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
    public PatternProviderLogicHost getHost() {
        return host;
    }

    @Unique
    boolean CE$injected = false;

    @Unique
    private void CE$injectUpgradeCallback() {
        if (CE$injected) return;
        if (getUpgrades() instanceof MachineUpgradeInventoryAccessor accessor) {
            CE$injected = true;
            var oldCallback = accessor.getChangeCallback();
            accessor.setChangeCallback(() -> {
                if (oldCallback != null)
                    oldCallback.onUpgradesChanged();
                this.updateSleep();
            });
        }
    }
}

