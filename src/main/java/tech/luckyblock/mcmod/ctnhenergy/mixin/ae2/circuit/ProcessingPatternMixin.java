package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.circuit;

import appeng.crafting.pattern.AEProcessingPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tech.luckyblock.mcmod.ctnhenergy.api.ICircuitPattern;

@Mixin(value = AEProcessingPattern.class, remap = false)
public class ProcessingPatternMixin implements ICircuitPattern {

    @Unique
    private int CE$circuitNumber = ICircuitPattern.NO_CIRCUIT;

    @Override
    public void CE$setCircuitNumber(int number) {
        CE$circuitNumber = number;
    }

    @Override
    public int CE$getCircuitNumber() {
        return CE$circuitNumber;
    }
}
