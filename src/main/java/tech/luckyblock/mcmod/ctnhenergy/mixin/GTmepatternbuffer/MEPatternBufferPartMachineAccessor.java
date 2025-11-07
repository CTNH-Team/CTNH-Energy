package tech.luckyblock.mcmod.ctnhenergy.mixin.GTmepatternbuffer;

import appeng.api.stacks.KeyCounter;
import com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferPartMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = MEPatternBufferPartMachine.class, remap = false)
public interface MEPatternBufferPartMachineAccessor {
}
