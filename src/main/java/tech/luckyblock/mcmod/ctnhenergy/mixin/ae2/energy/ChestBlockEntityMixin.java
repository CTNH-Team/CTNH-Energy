package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.energy;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.PowerUnits;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.events.GridPowerStorageStateChanged;
import appeng.blockentity.grid.AENetworkPowerBlockEntity;
import appeng.blockentity.storage.ChestBlockEntity;
import appeng.util.inv.AppEngInternalInventory;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.compat.FeCompat;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChestBlockEntity.class, remap = false)
public abstract class ChestBlockEntityMixin extends AENetworkPowerBlockEntity {
    @Shadow
    @Final
    private AppEngInternalInventory cellInventory;

    public ChestBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    void allowExtract(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState, CallbackInfo ci) {
        setInternalPowerFlow(AccessRestriction.READ_WRITE);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected double extractAEPower(double amt, Actionable mode) {
        // allow ui to open
        if(amt <= PowerMultiplier.CONFIG.multiply(1.1F) && mode == Actionable.SIMULATE) {
            return PowerMultiplier.CONFIG.multiply(1.0F);
        }


        double extracted = 0.0;

        for (var stack : cellInventory) {
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

        return extracted + super.extractAEPower(amt - extracted, mode);
    }

//    @Override
//    public double getInternalCurrentPower() {
//        double extracted = 0;
//        for (var stack : cellInventory) {
//            var electricItem = stack.getCapability(GTCapability.CAPABILITY_ELECTRIC_ITEM).resolve().orElse(null);
//            if(electricItem != null) {
//                extracted += electricItem.getCharge() * 2;
//            };
//        }
//        return super.getInternalCurrentPower() + extracted;
//    }

    @Inject(method = "onChangeInventory", at = @At("HEAD"))
    void sendEnergyEvent(InternalInventory inv, int slot, CallbackInfo ci){
        if (inv == cellInventory) {
            for (var stack : cellInventory) {
                var electricItem = stack.getCapability(GTCapability.CAPABILITY_ELECTRIC_ITEM).resolve();
                if(electricItem.isPresent()){
                    getMainNode().ifPresent(grid -> grid.postEvent(new GridPowerStorageStateChanged(this, GridPowerStorageStateChanged.PowerEventType.PROVIDE_POWER)));
                    return;
                }
            }
        }
    }

}
