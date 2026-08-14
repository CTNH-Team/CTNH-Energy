package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.part;

import appeng.api.behaviors.StackTransferContext;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.parts.IPartItem;
import appeng.api.storage.AEKeyFilter;
import appeng.core.settings.TickRates;
import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.IOBusPart;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.api.IMaintainingContext;
import tech.luckyblock.mcmod.ctnhenergy.common.item.MaintainingCardItem;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEItems;

@Mixin(value = ExportBusPart.class, remap = false)
public abstract class ExportBusPartMixin extends IOBusPart {

    public ExportBusPartMixin(TickRates tickRates, @Nullable AEKeyFilter filter, IPartItem<?> partItem) {
        super(tickRates, filter, partItem);
    }

    @Inject(method = "createTransferContext", at = @At("RETURN"))
    void injectMaintainingAmount(IStorageService storageService, IEnergyService energyService,
                                 CallbackInfoReturnable<StackTransferContext> cir) {
        var context = cir.getReturnValue();
        if (isUpgradedWith(CEItems.MAINTAINING_CARD) && context instanceof IMaintainingContext maintainingContext) {
            maintainingContext.setMaintainingAmount(CE$getMaintainingAmount());
        }
    }

    @Unique
    private long CE$getMaintainingAmount() {
        for (var stack : getUpgrades()) {
            if (stack.is(CEItems.MAINTAINING_CARD.asItem())) {
                return MaintainingCardItem.getMaintainingAmount(stack);
            }
        }
        return MaintainingCardItem.DEFAULT_MAINTAINING_AMOUNT;
    }
}
