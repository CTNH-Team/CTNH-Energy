package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.energy;

import appeng.api.config.IncludeExclude;
import appeng.api.stacks.AEKey;
import appeng.me.storage.MEInventoryHandler;
import appeng.util.prioritylist.IPartitionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKey;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.VoltageKeyType;

@Mixin(value = MEInventoryHandler.class, remap = false)
public class MEInventoryHandlerMixin {
    @Shadow
    private IncludeExclude partitionListMode;

    @Shadow
    private IPartitionList partitionList;

    @Inject(method = "passesBlackOrWhitelist", at = @At("RETURN"), cancellable = true)
    void includeVoltageKey(AEKey input, CallbackInfoReturnable<Boolean> cir){
        if(
                (!cir.getReturnValue() && partitionListMode == IncludeExclude.WHITELIST) ||
                (cir.getReturnValue() && partitionListMode == IncludeExclude.BLACKLIST)
        ){
            if(input.getType() == VoltageKeyType.INSTANCE){
                cir.setReturnValue(partitionList.matchesFilter(EUKey.EU, partitionListMode));
            }
        }
    }
}
