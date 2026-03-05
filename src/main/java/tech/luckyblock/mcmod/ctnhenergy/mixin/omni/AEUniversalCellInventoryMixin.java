package tech.luckyblock.mcmod.ctnhenergy.mixin.omni;

import appeng.api.stacks.AEKey;
import com.wintercogs.ae2omnicells.common.me.AEUniversalCellInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKeyType;

@Mixin(value = AEUniversalCellInventory.class, remap = false)
public class AEUniversalCellInventoryMixin {

    @Inject(method = "matchesPartitionAndUpgrades", at = @At("HEAD"), cancellable = true)
    void rejectEU(AEKey what, CallbackInfoReturnable<Boolean> cir) {
        if (what.getType() == EUKeyType.INSTANCE) cir.setReturnValue(false);
    }
}
