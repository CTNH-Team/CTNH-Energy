package tech.luckyblock.mcmod.ctnhenergy.mixin;

import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.api.crafting.IPatternDetails;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = PatternProviderLogic.class, remap = false)
public abstract class PatternProviderLogicMixin {

    /**
     * 重定向 this.patterns.contains(patternDetails) 调用。
     */
    @Redirect(
            method = "pushPattern",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;contains(Ljava/lang/Object;)Z"
            )
    )
    private boolean redirectContains(List<IPatternDetails> patterns, Object obj) {
        if (!(obj instanceof IPatternDetails pattern)) {
            return patterns.contains(obj);
        }

        for (IPatternDetails existing : patterns) {
            if (existing.getDefinition().equals(pattern.getDefinition())) {
                return true;
            }
        }

        return false;
    }
}
