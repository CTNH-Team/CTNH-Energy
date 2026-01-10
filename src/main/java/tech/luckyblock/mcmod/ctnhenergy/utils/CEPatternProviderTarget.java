package tech.luckyblock.mcmod.ctnhenergy.utils;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import appeng.helpers.patternprovider.PatternProviderTarget;

import java.util.Set;

public interface CEPatternProviderTarget extends PatternProviderTarget {
//    private static CEPatternProviderTarget wrapMeStorage(MEStorage storage, IActionSource src) {
//        return new CEPatternProviderTarget() {
//            @Override
//            public long insert(AEKey what, long amount, Actionable type) {
//                return storage.insert(what, amount, type, src);
//            }
//
//            @Override
//            public boolean containsPatternInput(Set<AEKey> patternInputs) {
//                for (var stack : storage.getAvailableStacks()) {
//                    if (patternInputs.contains(stack.getKey().dropSecondary())) {
//                        return true;
//                    }
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onlyHasPatternInput(Set<AEKey> patternInputs) {
//                for (var stack : storage.getAvailableStacks()) {
//                    if (patternInputs.contains(stack.getKey().dropSecondary())) continue;
//                    return false;
//                }
//                return true;
//            }
//
//            @Override
//            public MEStorage getStorage() {
//                return storage;
//            }
//        };
//    }

    boolean onlyHasPatternInput(Set<AEKey> var1, boolean fuzzy);

    MEStorage getStorage();
}
