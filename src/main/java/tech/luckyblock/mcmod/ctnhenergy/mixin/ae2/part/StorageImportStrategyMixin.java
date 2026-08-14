package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.part;

import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import appeng.parts.automation.StorageImportStrategy;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tech.luckyblock.mcmod.ctnhenergy.api.IMaintainingContext;

@Mixin(value = StorageImportStrategy.class, remap = false)
public class StorageImportStrategyMixin {

    @Redirect(method = "transfer",
              at = @At(value = "INVOKE",
                       target = "Lappeng/api/storage/MEStorage;insert(Lappeng/api/stacks/AEKey;JLappeng/api/config/Actionable;Lappeng/api/networking/security/IActionSource;)J",
                       ordinal = 0))
    private long checkMaintainingCount(MEStorage instance, AEKey what, long amount, Actionable mode,
                                       IActionSource source,
                                       @Local(argsOnly = true) StackTransferContext context,
                                       @Local(name = "resource") GenericStack resource) {
        if (context instanceof IMaintainingContext maintainingContext) {
            amount = Math.min(amount, resource.amount() - maintainingContext.getMaintainingAmount());
            if (amount <= 0) return 0;
        }
        return instance.insert(what, amount, mode, source);
    }
}
