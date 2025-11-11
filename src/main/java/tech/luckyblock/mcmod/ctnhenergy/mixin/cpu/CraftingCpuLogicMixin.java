package tech.luckyblock.mcmod.ctnhenergy.mixin.cpu;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.CEConfig;
import tech.luckyblock.mcmod.ctnhenergy.common.CESettings;
import tech.luckyblock.mcmod.ctnhenergy.common.pattern.DynamicProcessingPattern;
import tech.luckyblock.mcmod.ctnhenergy.mixin.patternprovider.PatternProviderLogicAccessor;
import tech.luckyblock.mcmod.ctnhenergy.utils.ProviderRecord;

import java.util.ArrayList;
import java.util.List;


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

    @Unique
    boolean CE$isBlock(ICraftingProvider provider){
        if(provider instanceof PatternProviderLogic){
            var configManager = ((PatternProviderLogicAccessor)provider).getConfigManager();
            return configManager.getSetting(Settings.BLOCKING_MODE) == YesNo.YES
                    && configManager.getSetting(CESettings.BLOCKING_TYPE) != CESettings.BlockingType.SMART;
        }
        return false;
    }

    /**
     * 自动翻倍样板
     */
    @Inject(method = "executeCrafting", at = @At("HEAD"), cancellable = true)
    private void onExecuteCrafting(int maxProviders,
                                   CraftingService craftingService,
                                   IEnergyService energyService,
                                   Level level,
                                   CallbackInfoReturnable<Integer> cir) {
        maxProviders = Math.min(maxProviders, CEConfig.INSTANCE.cpu.maxProviders);

        var jobLocal = this.job;
        if (jobLocal == null) {
            cir.setReturnValue(0);
            return;
        }

        ExecutingCraftingJobAccessor jobAccessor = (ExecutingCraftingJobAccessor) jobLocal;
        ListCraftingInventory waitingFor = jobAccessor.getWaitingFor();
        var timeTracker = jobAccessor.getTimeTracker();

        int pushedPatterns = 0;

        var it = jobAccessor.getTasks().entrySet().iterator();
        while (it.hasNext() && maxProviders >0) {
            var task = it.next();
            TaskProgressAccessor progressAccessor = (TaskProgressAccessor)(task.getValue());
            long totalCount = progressAccessor.getValue();

            if (totalCount <= 0) {
                it.remove();
                continue;
            }

            IPatternDetails pattern = task.getKey(), mulPattern;
            boolean isProcessing = pattern instanceof AEProcessingPattern;
            List<ProviderRecord> providerRecords = new ArrayList<>();

            int blocking = 0, nonBlocking = 0;
            for (var provider : craftingService.getProviders(pattern)) {
                if(maxProviders <= 0) break;
                if (!provider.isBusy()) {
                    maxProviders--; //每调用一个样板供应器，消耗一并行
                    var isBlock = !isProcessing || CE$isBlock(provider);
                    if(isBlock)
                        blocking++;
                    else
                        nonBlocking++;
                    providerRecords.add(new ProviderRecord(provider, isBlock));
                }
            }

            long multiplier = 1;
            if(nonBlocking != 0) {
                //神秘公式
                multiplier = Math.max( (totalCount - blocking -1)/nonBlocking + 1,  1);
                multiplier = Math.min(multiplier, CEConfig.INSTANCE.cpu.maxMultiple);
            }


            mulPattern = (multiplier == 1 ? pattern : new DynamicProcessingPattern((AEProcessingPattern) pattern).multiplyInPlace(multiplier));

            for (var r : providerRecords) {
                var workingPattern = r.block() ? pattern : mulPattern;
                //最后一个供应器可能会分配到大于倍数的样板
                if(totalCount < multiplier) workingPattern = new DynamicProcessingPattern((AEProcessingPattern) pattern).multiplyInPlace(totalCount);

                KeyCounter expectedOutputs = new KeyCounter();
                KeyCounter expectedContainerItems = new KeyCounter();
                var craftingContainer = CraftingCpuHelper.extractPatternInputs(workingPattern, inventory, level, expectedOutputs, expectedContainerItems);

                if (craftingContainer == null) {
                    break;
                }

                double patternPower = CraftingCpuHelper.calculatePatternPower(craftingContainer);

                double extracted = energyService.extractAEPower(patternPower, Actionable.SIMULATE, PowerMultiplier.CONFIG);

                if (extracted + 0.01 >= patternPower && r.provider().pushPattern(workingPattern, craftingContainer)) {
                    energyService.extractAEPower(patternPower, Actionable.MODULATE, PowerMultiplier.CONFIG);

                    for (var ao : expectedOutputs) {
                        waitingFor.insert(ao.getKey(), ao.getLongValue(), Actionable.MODULATE);
                    }
                    for (var expected : expectedContainerItems) {
                        waitingFor.insert(expected.getKey(), expected.getLongValue(), Actionable.MODULATE);
                        ((ElapsedTimeTrackerInvoker) timeTracker).invokeAddMaxItems(expected.getLongValue(), expected.getKey().getType());
                    }

                    cluster.markDirty();
                    pushedPatterns++;
                    totalCount -= (r.block() ? 1 : multiplier);

                    if(totalCount <= 0 )
                    {
                        progressAccessor.setValue(0);
                        it.remove();
                        break;
                    }
                    else{
                        progressAccessor.setValue(totalCount);
                    }
                }
                else {
                    CraftingCpuHelper.reinjectPatternInputs(inventory, craftingContainer);
                }

            }

        }

        cir.setReturnValue(pushedPatterns);
    }
}
