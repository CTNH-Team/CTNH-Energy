package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.part;

import appeng.api.behaviors.StackTransferContext;
import appeng.api.parts.IPartItem;
import appeng.api.storage.AEKeyFilter;
import appeng.core.settings.TickRates;
import appeng.parts.automation.IOBusPart;
import appeng.parts.automation.ImportBusPart;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import tech.luckyblock.mcmod.ctnhenergy.api.IMaintainingContext;
import tech.luckyblock.mcmod.ctnhenergy.common.item.MaintainingCardItem;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEItems;

@Mixin(value = ImportBusPart.class, remap = false)
public abstract class ImportBusPartMixin extends IOBusPart {

    public ImportBusPartMixin(TickRates tickRates, @Nullable AEKeyFilter filter, IPartItem<?> partItem) {
        super(tickRates, filter, partItem);
    }

    @ModifyArg(method = "doBusWork",
               at = @At(value = "INVOKE",
                        target = "Lappeng/api/behaviors/StackImportStrategy;transfer(Lappeng/api/behaviors/StackTransferContext;)Z"))
    StackTransferContext injectMaintainingAmount(StackTransferContext context) {
        if (isUpgradedWith(CEItems.MAINTAINING_CARD) && context instanceof IMaintainingContext maintainingContext) {
            maintainingContext.setMaintainingAmount(CE$getMaintainingAmount());
        }
        return context;
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
