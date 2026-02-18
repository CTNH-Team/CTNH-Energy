package tech.luckyblock.mcmod.ctnhenergy.mixin.gtm;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import com.gregtechceu.gtceu.integration.ae2.utils.KeyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = KeyStorage.class, remap = false)
public class KeyStorageMixin {
    @Redirect(method = "insertInventory", at = @At(value = "INVOKE", target = "Lappeng/api/storage/MEStorage;insert(Lappeng/api/stacks/AEKey;JLappeng/api/config/Actionable;Lappeng/api/networking/security/IActionSource;)J"))
    long checkNull(MEStorage instance,
                   AEKey what,
                   long amount,
                   Actionable mode,
                   IActionSource source
    ){
        if(what == null) return amount;
        else {
            return instance.insert(what, amount, mode, source);
        }
    }
}
