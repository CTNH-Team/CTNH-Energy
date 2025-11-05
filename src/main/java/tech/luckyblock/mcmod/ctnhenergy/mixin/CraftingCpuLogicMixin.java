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
import appeng.me.cluster.implementations.CraftingCPUCluster;
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

    @Final
    @Shadow
    CraftingCPUCluster cluster;

    @Shadow
    public abstract long insert(AEKey what, long amount, Actionable type);

    @Unique
    private static final Logger LOG = LoggerFactory.getLogger("CTNHEnergy-CraftingCPU");


    /**
     * 主逻辑拦截
     */
//    @Inject(method = "executeCrafting", at = @At("HEAD"), cancellable = true)
//    private void onExecuteCrafting(int maxPatterns,
//                                   CraftingService craftingService,
//                                   IEnergyService energyService,
//                                   Level level,
//                                   CallbackInfoReturnable<Integer> cir) {
//
//        var jobLocal = this.job;
//        if (jobLocal == null) {
//            cir.setReturnValue(0);
//            return;
//        }
//
//        ExecutingCraftingJobAccessor jobAccessor = (ExecutingCraftingJobAccessor) jobLocal;
//        ListCraftingInventory waitingFor = jobAccessor.getWaitingFor();
//        var timeTracker = jobAccessor.getTimeTracker();
//
//        int pushedPatterns = 0;
//
//        // Step2: 推送
//        var it = jobAccessor.getTasks().entrySet().iterator();
//        taskLoop: while (it.hasNext()) {
//            var task = it.next();
//            TaskProgressAccessor progressAccessor = (TaskProgressAccessor)(task.getValue());
//            long totalCount = progressAccessor.getValue();
//
//            if (totalCount <= 0) {
//                it.remove();
//                continue;
//            }
//
//            IPatternDetails pattern = task.getKey();
//            boolean isProcessing = pattern instanceof AEProcessingPattern;
//
//            List<ICraftingProvider> providers = new ArrayList<>();
//            boolean pushed = false;
//
//            for (var provider : craftingService.getProviders(pattern)) {
//                if (!provider.isBusy()) {
//                    providers.add(provider);
//                }
//            }
//            int remains = providers.size();
//            for (var provider : providers) {
//
//                boolean isBlocking = false;
//                if(provider instanceof PatternProviderLogic){
//                    isBlocking = ((PatternProviderLogicAccessor)provider).getConfigManager().getSetting(Settings.BLOCKING_MODE) == YesNo.YES;
//                }
//
//
//                long multiplier;
//                IPatternDetails realPattern;
//
//                if(isBlocking || !isProcessing){
//                    multiplier = 1;
//                    realPattern = pattern;
//                }
//                else {
//                    multiplier = totalCount/remains;
//                    realPattern = new DynamicProcessingPattern((AEProcessingPattern) pattern).multiplyInPlace(multiplier);
//                }
//
//                KeyCounter expectedOutputs = new KeyCounter();
//                KeyCounter expectedContainerItems = new KeyCounter();
//                var craftingContainer = CraftingCpuHelper.extractPatternInputs(realPattern, inventory, level, expectedOutputs, expectedContainerItems);
//
//                if (craftingContainer == null) {
//                    break;
//                }
//
//                double patternPower = CraftingCpuHelper.calculatePatternPower(craftingContainer);
//
//                double extracted = energyService.extractAEPower(patternPower, Actionable.SIMULATE, PowerMultiplier.CONFIG);
//
//                if (extracted + 0.01 >= patternPower && provider.pushPattern(realPattern, craftingContainer)) {
//                    energyService.extractAEPower(patternPower, Actionable.MODULATE, PowerMultiplier.CONFIG);
//                    pushed = true;
//                    //pushedPatterns++;
//
//                    for (var ao : expectedOutputs) {
//                        waitingFor.insert(ao.getKey(), ao.getLongValue(), Actionable.MODULATE);
//                    }
//                    for (var expected : expectedContainerItems) {
//                        waitingFor.insert(expected.getKey(), expected.getLongValue(), Actionable.MODULATE);
//                        ((ElapsedTimeTrackerInvoker) timeTracker).invokeAddMaxItems(expected.getLongValue(), expected.getKey().getType());
//                    }
//
//                    cluster.markDirty();
//
//                    totalCount -= multiplier;
//
//                    if(totalCount <= 0 )
//                    {
//                        progressAccessor.setValue(0);
//                        it.remove();
//                        continue taskLoop;
//                    }
//                    else{
//                        progressAccessor.setValue(totalCount);
//                    }
//
//                    //expectedOutputs.reset();
//                    //expectedContainerItems.reset();
//                    //不用reset，直接释放
//                }
//                else {
//                    CraftingCpuHelper.reinjectPatternInputs(inventory, craftingContainer);
//                }
//                remains--;
//            }
//            if(pushed) pushedPatterns++;
//            if(pushedPatterns == maxPatterns)
//            {
//                break;
//            }
//
//        }
//
//        cir.setReturnValue(pushedPatterns);
//    }
}
