package tech.luckyblock.mcmod.ctnhenergy.mixin;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.GenericStack;
import appeng.crafting.execution.ExecutingCraftingJob;
import appeng.crafting.execution.ElapsedTimeTracker;
import appeng.crafting.inv.ListCraftingInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = ExecutingCraftingJob.class, remap = false)
public interface ExecutingCraftingJobAccessor {

    /**
     * 返回一个 Map<IPatternDetails, TaskProgressAsObject>
     * 这里的 TaskProgressAsObject 实际上是 ExecutingCraftingJob.TaskProgress 的实例，但我们必须用 Object 来接收它。
     */
    @Accessor("tasks")
    Map<IPatternDetails, Object> getTasks();

    @Accessor("waitingFor")
    ListCraftingInventory getWaitingFor();

    @Accessor("timeTracker")
    ElapsedTimeTracker getTimeTracker();

    @Accessor("finalOutput")
    GenericStack getFinalOutput();


}
