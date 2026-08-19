package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.cpu;

import net.minecraft.nbt.CompoundTag;

import appeng.api.crafting.IPatternDetails;
import appeng.crafting.execution.ExecutingCraftingJob;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.api.ICircuitPattern;
import tech.luckyblock.mcmod.ctnhenergy.common.circuit.CircuitPatternData;

import java.util.Map;

@Mixin(value = ExecutingCraftingJob.class)
public class ExecutingCraftingJobCircuitMixin {

    @Inject(method = "writeToNBT",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/nbt/CompoundTag;putLong(Ljava/lang/String;J)V",
                     remap = true,
                     ordinal = 0),
            remap = false)
    private void write(CallbackInfoReturnable<CompoundTag> cir, @Local(name = "e") Map.Entry<IPatternDetails, ?> entry,
                       @Local(name = "item") CompoundTag item) {
        if (entry.getKey() instanceof ICircuitPattern pattern &&
                pattern.CE$getCircuitNumber() != ICircuitPattern.NO_CIRCUIT) {
            CircuitPatternData.write(item, pattern.CE$getCircuitNumber());
        }
    }
}
