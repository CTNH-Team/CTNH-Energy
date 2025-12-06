package tech.luckyblock.mcmod.ctnhenergy.mixin.gtm.GTmepatternbuffer;

import appeng.api.crafting.IPatternDetails;
import com.google.common.collect.BiMap;
import com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferPartMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MEPatternBufferPartMachine.class, remap = false)
public class MEPatternBufferPartMachineMixin {
    @Redirect(
            method = "pushPattern",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/BiMap;containsKey(Ljava/lang/Object;)Z")
    )
    private boolean redirectContains(BiMap<IPatternDetails, MEPatternBufferPartMachine.InternalSlot> instance, Object obj) {
        if (!(obj instanceof IPatternDetails pattern)) {
            return instance.containsKey(obj);
        }
        for (IPatternDetails existing : instance.keySet()) {
            if (existing.getDefinition().equals(pattern.getDefinition())) {
                return true;
            }
        }
        return false;
    }

    @Redirect(
            method = "pushPattern",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/BiMap;get(Ljava/lang/Object;)Ljava/lang/Object;")
    )
    private Object redirectGet(BiMap<IPatternDetails, MEPatternBufferPartMachine.InternalSlot> instance, Object obj) {
        if (!(obj instanceof IPatternDetails pattern)) {
            return instance.get(obj);
        }
        for (IPatternDetails existing : instance.keySet()) {
            if (existing.getDefinition().equals(pattern.getDefinition())) {
                return instance.get(existing);
            }
        }

        return null;
    }
}

