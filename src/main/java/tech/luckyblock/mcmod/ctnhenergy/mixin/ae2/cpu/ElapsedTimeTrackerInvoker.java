package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.cpu;

import appeng.api.stacks.AEKeyType;
import appeng.crafting.execution.ElapsedTimeTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ElapsedTimeTracker.class, remap = false)
public interface ElapsedTimeTrackerInvoker {

    /**
     * 调用私有的 addMaxItems(long, AEKeyType)
     */
    @Invoker("addMaxItems")
    void invokeAddMaxItems(long itemDiff, AEKeyType keyType);
}
