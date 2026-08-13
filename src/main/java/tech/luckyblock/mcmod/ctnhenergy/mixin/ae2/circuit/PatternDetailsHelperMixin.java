package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.circuit;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.api.ICircuitPattern;
import tech.luckyblock.mcmod.ctnhenergy.common.circuit.CircuitPatternData;

@Mixin(value = PatternDetailsHelper.class, remap = false)
public class PatternDetailsHelperMixin {

    @Inject(method = "decodePattern(Lappeng/api/stacks/AEItemKey;Lnet/minecraft/world/level/Level;)Lappeng/api/crafting/IPatternDetails;",
            at = @At("RETURN"))
    private static void decode(AEItemKey key, Level level, CallbackInfoReturnable<IPatternDetails> cir) {
        CE$apply(key.getTag(), cir.getReturnValue());
    }

    @Inject(method = "decodePattern(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Z)Lappeng/api/crafting/IPatternDetails;",
            at = @At("RETURN"))
    private static void decode(ItemStack stack, Level level, boolean autoRecovery,
                               CallbackInfoReturnable<IPatternDetails> cir) {
        CE$apply(stack.getTag(), cir.getReturnValue());
    }

    @Unique
    private static void CE$apply(CompoundTag tag, IPatternDetails details) {
        if (details instanceof ICircuitPattern pattern) pattern.CE$setCircuitNumber(CircuitPatternData.read(tag));
    }
}
