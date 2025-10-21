package tech.luckyblock.mcmod.ctnhenergy.mixin;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.crafting.execution.CraftingCpuHelper;
import appeng.crafting.execution.CraftingCpuLogic;
import appeng.crafting.execution.ExecutingCraftingJob;
import appeng.crafting.inv.ListCraftingInventory;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.me.service.CraftingService;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.luckyblock.mcmod.ctnhenergy.common.pattern.DynamicProcessingPattern;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 优化 AE2 合成 CPU 逻辑：
 *  1️⃣ 将相同 pattern 合并为单个并行任务
 *  2️⃣ 合并输入，一次性推送
 */
@Mixin(value = CraftingCpuLogic.class, remap = false)
public abstract class CraftingCpuLogicMixin {

    @Shadow
    private ExecutingCraftingJob job;

    @Final
    @Shadow
    private ListCraftingInventory inventory;

    @Shadow public abstract long insert(AEKey what, long amount, Actionable type);

    @Unique
    private static final Logger LOG = LoggerFactory.getLogger("CTNHEnergy-CraftingCPU");


    @Unique
    private static Field resolveTaskProgressValueField(Object taskProgressInstance) {
        Class<?> cls = taskProgressInstance.getClass();
        try {
            Field f = cls.getDeclaredField("value");
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException e) {
            for (Field f : cls.getDeclaredFields()) {
                if (f.getType() == long.class) {
                    f.setAccessible(true);
                    return f;
                }
            }
            throw new RuntimeException("无法找到 TaskProgress.value 字段", e);
        }
    }

    @Unique
    private static long getTaskProgressValue(Object taskProgressInstance) {
        try {
            Field f = resolveTaskProgressValueField(taskProgressInstance);
            return f.getLong(taskProgressInstance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Unique
    private static void setTaskProgressValue(Object taskProgressInstance, long newVal) {
        try {
            Field f = resolveTaskProgressValueField(taskProgressInstance);
            f.setLong(taskProgressInstance, newVal);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 主逻辑拦截
     */
    @Inject(method = "executeCrafting", at = @At("HEAD"), cancellable = true)
    private void onExecuteCrafting(int maxPatterns,
                                   CraftingService craftingService,
                                   IEnergyService energyService,
                                   Level level,
                                   CallbackInfoReturnable<Integer> cir) {

        var jobLocal = this.job;
        if (jobLocal == null) {
            cir.setReturnValue(0);
            return;
        }

        ExecutingCraftingJobAccessor accessor = (ExecutingCraftingJobAccessor) jobLocal;
        ListCraftingInventory waitingFor = accessor.getWaitingFor();
        var timeTracker = accessor.getTimeTracker();

        int pushedPatterns = 0;

        // Step2: 推送
        var it = accessor.getTasks().entrySet().iterator();
        taskLoop: while (it.hasNext()) {
            var task = it.next();

            long totalCount = getTaskProgressValue(task.getValue());

            if (totalCount <= 0) continue;

            IPatternDetails pattern = task.getKey();
            boolean isProcessing = pattern instanceof AEProcessingPattern;

            List<ICraftingProvider> providers = new ArrayList<>();
            //boolean pushed = false;

            for (var provider : craftingService.getProviders(pattern)) {
                if (!provider.isBusy()) {
                    providers.add(provider);
                }
            }

            for (var provider : providers) {

                boolean isBlocking = false;
                if(provider instanceof PatternProviderLogic){
                    isBlocking = ((PatternProviderLogicAccessor)provider).getConfigManager().getSetting(Settings.BLOCKING_MODE) == YesNo.YES;
                }


                long multiplier;
                IPatternDetails realPattern;

                if(isBlocking || !isProcessing){
                    multiplier = 1;
                    realPattern = pattern;
                }
                else {
                    multiplier = totalCount/providers.size();
                    realPattern = new DynamicProcessingPattern((AEProcessingPattern) pattern).multiplyInPlace(multiplier);
                }

                KeyCounter expectedOutputs = new KeyCounter();
                KeyCounter expectedContainerItems = new KeyCounter();
                var craftingContainer = CraftingCpuHelper.extractPatternInputs(realPattern, inventory, level, expectedOutputs, expectedContainerItems);

                if (craftingContainer == null) {
                    break;
                }

                double singlePower = CraftingCpuHelper.calculatePatternPower(craftingContainer);
                double totalPower = singlePower * multiplier;

                double extracted = energyService.extractAEPower(totalPower, Actionable.SIMULATE, PowerMultiplier.CONFIG);

                boolean pushed = provider.pushPattern(realPattern, craftingContainer);

                if (extracted + 0.01 >= totalPower && pushed) {
                    energyService.extractAEPower(totalPower, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    //pushed = true;
                    pushedPatterns++;

                    for (var ao : expectedOutputs) {
                        waitingFor.insert(ao.getKey(), ao.getLongValue(), Actionable.MODULATE);
                    }
                    for (var expected : expectedContainerItems) {
                        waitingFor.insert(expected.getKey(), expected.getLongValue(), Actionable.MODULATE);
                        ((ElapsedTimeTrackerInvoker) timeTracker).invokeAddMaxItems(expected.getLongValue(), expected.getKey().getType());
                    }
                    totalCount -= multiplier;

                    if(totalCount <= 0 )
                    {
                        setTaskProgressValue(task.getValue(), 0);
                        it.remove();
                        continue taskLoop;
                    }

                    if(pushedPatterns == maxPatterns)
                    {
                        break taskLoop;
                    }

                    expectedOutputs.reset();
                    expectedContainerItems.reset();
                }
                else {
                    CraftingCpuHelper.reinjectPatternInputs(inventory, craftingContainer);
                }
            }

        }

        cir.setReturnValue(pushedPatterns);
    }
}
