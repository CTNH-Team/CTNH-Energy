package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.patternprovider;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import appeng.helpers.patternprovider.PatternProviderTarget;
import com.gregtechceu.gtceu.common.data.GTItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.utils.CEPatternProviderTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mixin(targets = "appeng.helpers.patternprovider.PatternProviderTargetCache", remap = false)
public class PatternProviderTargetCacheMixin {

    @Final
    @Shadow
    private IActionSource src;

    @Inject(
            method = "wrapMeStorage",
            at = @At("HEAD"),
            cancellable = true
    )
    private void wrapMeStorage(MEStorage storage, CallbackInfoReturnable<PatternProviderTarget> cir) {
        cir.setReturnValue(
                new CEPatternProviderTarget() {
                    @Override
                    public long insert(AEKey what, long amount, Actionable type) {
                        return storage.insert(what, amount, type, src);
                    }

                    @Override
                    public boolean containsPatternInput(Set<AEKey> patternInputs) {
                        for (var stack : storage.getAvailableStacks()) {
                            if (patternInputs.contains(stack.getKey().dropSecondary())) {
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean onlyHasPatternInput(Set<AEKey> patternInputs, boolean fuzzy) {

                        Set<AEKey> matchSet = fuzzy
                                ? patternInputs.stream()
                                .map(AEKey::dropSecondary)
                                .collect(Collectors.toSet())
                                : patternInputs;

                        boolean allCircuit = true;
                        boolean allMatch = true;

                        for (var stack : storage.getAvailableStacks()) {
                            AEKey key = stack.getKey();

                            boolean isCircuit = key instanceof AEItemKey itemKey && itemKey.getItem() == GTItems.PROGRAMMED_CIRCUIT.asItem();

                            if (!isCircuit) {
                                allCircuit = false;
                            }

                            boolean matches = fuzzy&!isCircuit ? matchSet.contains(key.dropSecondary()) : matchSet.contains(key);

                            if (!matches) {
                                allMatch = false;

                                // 非电路且不匹配，直接失败
                                if (!isCircuit) {
                                    return false;
                                }
                            }
                        }

                        return allCircuit || allMatch;
                    }


                    public MEStorage getStorage() {
                        return storage;
                    }
                }
        );
    }
}
