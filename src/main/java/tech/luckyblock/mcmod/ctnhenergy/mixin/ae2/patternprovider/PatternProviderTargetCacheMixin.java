package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.patternprovider;

import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;

import appeng.api.config.Actionable;
import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import appeng.helpers.patternprovider.PatternProviderTarget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.api.ICircuitPattern;
import tech.luckyblock.mcmod.ctnhenergy.utils.CEPatternProviderTarget;

import java.util.Arrays;
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
            cancellable = true)
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
                    public boolean onlyHasPatternInput(IPatternDetails patternDetails, boolean fuzzy) {
                        Set<AEKey> matchSet = Arrays.stream(patternDetails.getInputs())
                                .map(i -> i.getPossibleInputs()[0])
                                .map(GenericStack::what)
                                .collect(Collectors.toSet());

                        int circuitNumber = patternDetails instanceof ICircuitPattern pattern ?
                                pattern.CE$getCircuitNumber() : ICircuitPattern.NO_CIRCUIT;

                        boolean allCircuit = true;
                        boolean allMatch = true;

                        for (var stack : storage.getAvailableStacks()) {
                            AEKey key = stack.getKey();

                            boolean isCircuit = key instanceof AEItemKey itemKey &&
                                    itemKey.getItem() == GTItems.PROGRAMMED_CIRCUIT.asItem();

                            boolean isMatch;

                            if (isCircuit) {
                                // 对于不包含编程电路的样板，忽略编程电路的匹配
                                isMatch = circuitNumber == -1 ||
                                        IntCircuitBehaviour.getCircuitConfiguration(
                                                ((AEItemKey) key).getReadOnlyStack()) == circuitNumber;
                            } else {
                                allCircuit = false;
                                isMatch = fuzzy ? matchSet.contains(key.dropSecondary()) :
                                        matchSet.contains(key);
                            }

                            if (!isMatch) {
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
                });
    }
}
