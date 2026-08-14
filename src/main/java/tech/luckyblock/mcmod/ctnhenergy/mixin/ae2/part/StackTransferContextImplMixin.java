package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.part;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tech.luckyblock.mcmod.ctnhenergy.api.IMaintainingContext;

@Mixin(targets = "appeng.parts.automation.StackTransferContextImpl", remap = false)
public class StackTransferContextImplMixin implements IMaintainingContext {

    @Unique
    private long CE$maintainingAmount = 0;

    @Override
    public long getMaintainingAmount() {
        return CE$maintainingAmount;
    }

    @Override
    public void setMaintainingAmount(long amount) {
        CE$maintainingAmount = amount;
    }
}
