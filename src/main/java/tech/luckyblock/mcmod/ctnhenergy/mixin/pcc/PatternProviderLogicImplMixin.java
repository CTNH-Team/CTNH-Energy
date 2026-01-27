package tech.luckyblock.mcmod.ctnhenergy.mixin.pcc;

import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yuuki1293.pccard.impl.PatternProviderLogicImpl;
import yuuki1293.pccard.wrapper.IAEPattern;

@Mixin(value = PatternProviderLogicImpl.class, remap = false)
public class PatternProviderLogicImplMixin {
    @ModifyConstant(method = "updatePatterns", constant = @Constant(intValue = 0))
    private static int replaceDefaultCircuit(int constant){
        return -1;
    }

    @Inject(method = "setInvNumber", at = @At(value = "HEAD"), cancellable = true)
    private static void ignore(NotifiableItemStackHandler inv, IAEPattern details, CallbackInfo ci){
        if(details.pCCard$getNumber() == -1)
            ci.cancel();
    }
}
