package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.energy;

import appeng.api.config.AccessRestriction;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.events.GridPowerStorageStateChanged;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IElectricItem;
import com.gregtechceu.gtceu.api.capability.compat.FeCompat;

import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.PowerUnits;
import appeng.api.networking.energy.IEnergySource;
import appeng.blockentity.grid.AENetworkInvBlockEntity;
import appeng.blockentity.inventory.AppEngCellInventory;
import appeng.blockentity.storage.DriveBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DriveBlockEntity.class, remap = false)
public abstract class DriveBlockEntityMixin extends AENetworkInvBlockEntity implements IAEPowerStorage {

    @Shadow
    @Final
    private AppEngCellInventory inv;

    @Shadow
    private int priority;

    public DriveBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    void addEnergyService(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState, CallbackInfo ci) {
        getMainNode().addService(IAEPowerStorage.class, this);
    }

    @Inject(method = "onChangeInventory", at = @At("HEAD"))
    void sendEnergyEvent(InternalInventory inv, int slot, CallbackInfo ci){
        for (var stack : inv) {
            var electricItem = stack.getCapability(GTCapability.CAPABILITY_ELECTRIC_ITEM).resolve();
            if(electricItem.isPresent()){
                getMainNode().ifPresent(grid -> grid.postEvent(new GridPowerStorageStateChanged(this, GridPowerStorageStateChanged.PowerEventType.PROVIDE_POWER)));
                return;
            }
        }
    }


    @Override
    public double extractAEPower(double amt, Actionable mode, PowerMultiplier pm) {
        return pm.divide(this.CE$extractAEPower(pm.multiply(amt), mode));
    }

    @Unique
    protected double CE$extractAEPower(double amt, Actionable mode) {
        double extracted = 0.0;

        for (var stack : inv) {
            if (extracted >= amt) {
                break;
            }

            var electricItem = stack.getCapability(GTCapability.CAPABILITY_ELECTRIC_ITEM)
                    .resolve()
                    .orElse(null);

            if (electricItem != null) {
                double remainingAE = amt - extracted;

                // AE -> GT（2:1），向上取整避免损失
                long requestGT = (long) Math.ceil(remainingAE / 2.0);

                if (requestGT <= 0) {
                    continue;
                }

                long extractedGT = electricItem.discharge(
                        requestGT,
                        GTValues.MAX,
                        true,
                        true,
                        mode == Actionable.SIMULATE
                );

                // GT -> AE
                double providedAE = extractedGT * 2.0;

                extracted += providedAE;
            }
        }

        return extracted;
    }

    @Override
    public double injectAEPower(double amt, Actionable mode) {
        return 0;
    }

    @Override
    public double getAEMaxPower() {
        return 0;
    }

    @Override
    public double getAECurrentPower() {
        return 0;
    }

    @Override
    public boolean isAEPublicPowerStorage() {
        return true;
    }

    @Override
    public AccessRestriction getPowerFlow() {
        return AccessRestriction.READ;
    }

}
