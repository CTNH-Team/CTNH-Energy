package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.patternprovider;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.Actionable;
import appeng.api.config.LockCraftingMode;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.networking.IGrid;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.definitions.AEItems;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.helpers.patternprovider.PatternProviderTarget;
import appeng.util.ConfigManager;
import appeng.util.inv.AppEngInternalInventory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.luckyblock.mcmod.ctnhenergy.api.ICircuitPattern;
import tech.luckyblock.mcmod.ctnhenergy.common.CESettings;
import tech.luckyblock.mcmod.ctnhenergy.common.circuit.CircuitPatternService;
import tech.luckyblock.mcmod.ctnhenergy.common.me.service.IEnergyDistributor;
import tech.luckyblock.mcmod.ctnhenergy.utils.CEPatternProviderTarget;

import java.util.*;

@Mixin(value = PatternProviderLogic.class, remap = false)
public abstract class PatternProviderLogicMixin implements IUpgradeableObject {

    @Unique
    private IUpgradeInventory CE$upgrades;

    @Final
    @Shadow
    private Set<AEKey> patternInputs;

    @Final
    @Shadow
    private AppEngInternalInventory patternInventory;

    @Shadow
    @Final
    private IActionSource actionSource;

    @Final
    @Shadow
    private PatternProviderLogicHost host;

    @Final
    @Shadow
    private IManagedGridNode mainNode;

    @Shadow
    @Final
    private ConfigManager configManager;

    @Shadow
    private int roundRobinIndex;

    @Shadow
    @Final
    private List<GenericStack> sendList;

    @Shadow
    private Direction sendDirection;

    @Shadow
    public abstract LockCraftingMode getCraftingLockedReason();

    @Shadow
    public abstract void updatePatterns();

    @Shadow
    protected abstract Set<Direction> getActiveSides();

    @Shadow
    protected abstract void onPushPatternSuccess(IPatternDetails pattern);

    @Shadow
    protected abstract <T> void rearrangeRoundRobin(List<T> list);

    @Shadow
    public abstract boolean isBlocking();

    @Shadow
    protected abstract boolean sendStacksOut();

    @Shadow
    protected abstract void addToSendList(AEKey what, long amount);

    @Shadow
    protected abstract boolean adapterAcceptsAll(PatternProviderTarget target, KeyCounter[] inputHolder);

    @Shadow
    protected abstract PatternProviderTarget findAdapter(Direction side);

    @Shadow
    public abstract @Nullable IGrid getGrid();

    // use a map rather than the original list to allow pattern to be dynamically modified
    @Unique
    private final Map<AEItemKey, IPatternDetails> CE$patternsMap = new HashMap<>();

    @Inject(
            method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/patternprovider/PatternProviderLogicHost;I)V",
            at = @At("TAIL"))
    private void PatternProviderLogic(IManagedGridNode mainNode, PatternProviderLogicHost host,
                                      int patternInventorySize, CallbackInfo ci) {
        configManager.registerSetting(CESettings.BLOCKING_TYPE, CESettings.BlockingType.DEFAULT);
        CE$upgrades = UpgradeInventories.forMachine(host.getTerminalIcon().getItem(), 1,
                () -> {
                    host.saveChanges();
                    updatePatterns();
                    ((IEnergyDistributor) this).updateSleep();
                });
    }

    @Unique
    public CESettings.BlockingType CE$getBlockingMode() {
        return configManager.getSetting(CESettings.BLOCKING_TYPE);
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return CE$upgrades;
    }

    @Inject(method = "writeToNBT", at = @At("HEAD"))
    private void CE$writeUpgrades(CompoundTag tag, CallbackInfo ci) {
        CE$upgrades.writeToNBT(tag, "ctnhenergy_upgrades");
    }

    @Inject(method = "readFromNBT", at = @At("HEAD"))
    private void CE$readUpgrades(CompoundTag tag, CallbackInfo ci) {
        CE$upgrades.readFromNBT(tag, "ctnhenergy_upgrades");
    }

    @Inject(method = "addDrops", at = @At("HEAD"))
    private void CE$dropUpgrades(List<ItemStack> drops, CallbackInfo ci) {
        for (var stack : CE$upgrades) {
            if (!stack.isEmpty()) drops.add(stack.copy());
        }
    }

    @Inject(method = "clearContent", at = @At("HEAD"))
    private void CE$clearUpgrades(CallbackInfo ci) {
        CE$upgrades.clear();
    }

    @Inject(
            method = "updatePatterns",
            at = @At("HEAD"),
            cancellable = true)
    public void updatePatternsMap(CallbackInfo ci) {
        CE$patternsMap.clear();
        patternInputs.clear();
        for (var stack : this.patternInventory) {
            var details = PatternDetailsHelper.decodePattern(CircuitPatternService.updatePattern(stack,
                    isUpgradedWith(tech.luckyblock.mcmod.ctnhenergy.registry.CEItems.PROGRAMMED_CIRCUIT_CARD.get())),
                    this.host.getBlockEntity().getLevel());

            if (details != null) {
                var key = details.getDefinition();
                CE$patternsMap.put(key, details);

                for (var iinput : details.getInputs()) {
                    for (var inputCandidate : iinput.getPossibleInputs()) {
                        patternInputs.add(inputCandidate.what().dropSecondary());
                    }
                }
            }
        }
        ICraftingProvider.requestUpdate(mainNode);
        ci.cancel();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public List<IPatternDetails> getAvailablePatterns() {
        return CE$patternsMap.values().stream().toList();
    }

    @Inject(
            method = "pushPattern",
            at = @At("HEAD"),
            cancellable = true)
    public void pushPattern(IPatternDetails patternDetails, KeyCounter[] inputHolder,
                            CallbackInfoReturnable<Boolean> cir) {
        if (!sendList.isEmpty() || !this.mainNode.isActive()) {
            cir.setReturnValue(false);
            return;
        }

        var be = host.getBlockEntity();
        var level = be.getLevel();

        if (getCraftingLockedReason() != LockCraftingMode.NONE) {
            cir.setReturnValue(false);
            return;
        }

        record PushTarget(Direction direction, PatternProviderTarget target) {}

        var possibleTargets = new ArrayList<PushTarget>();

        // Push to crafting machines first
        for (var direction : getActiveSides()) {
            var adjPos = be.getBlockPos().relative(direction);
            var adjBe = level.getBlockEntity(adjPos);
            var adjBeSide = direction.getOpposite();

            var craftingMachine = ICraftingMachine.of(level, adjPos, adjBeSide, adjBe);
            if (craftingMachine != null && craftingMachine.acceptsPlans()) {
                if (craftingMachine.pushPattern(patternDetails, inputHolder, adjBeSide)) {
                    tech.luckyblock.mcmod.ctnhenergy.common.circuit.CircuitPatternService.apply(patternDetails, level,
                            List.of(adjPos));
                    onPushPatternSuccess(patternDetails);
                    cir.setReturnValue(true);
                    return;
                }
                continue;
            }

            var adapter = findAdapter(direction);
            if (adapter == null)
                continue;

            possibleTargets.add(new PushTarget(direction, adapter));
        }

        // If no dedicated crafting machine could be found, and the pattern does not support
        // generic external inventories, stop here.
        if (!patternDetails.supportsPushInputsToExternalInventory()) {
            cir.setReturnValue(false);
            return;
        }

        // Rearrange for round-robin
        rearrangeRoundRobin(possibleTargets);

        // Push to other kinds of blocks
        for (PushTarget target : possibleTargets) {
            Direction direction = target.direction();
            CEPatternProviderTarget adapter = (CEPatternProviderTarget) target.target();

            // 提前提取阻塞状态和接受条件
            boolean isBlocking = this.isBlocking();
            boolean acceptsAll = this.adapterAcceptsAll(adapter, inputHolder);

            // 如果不接受所有输入，直接跳过
            if (!acceptsAll) {
                continue;
            }

            // 如果不处于阻塞状态，直接允许推送
            boolean canPush = !isBlocking;

            // 如果处于阻塞状态，根据不同的阻塞模式检查特定条件
            if (isBlocking) {
                canPush = switch (CE$getBlockingMode()) {
                    case ALL -> adapter.getStorage().getAvailableStacks().isEmpty();
                    case SMART -> adapter.getStorage().getAvailableStacks().isEmpty() ||
                            adapter.onlyHasPatternInput(patternDetails,
                                    isUpgradedWith(AEItems.FUZZY_CARD));
                    case DEFAULT -> !adapter.containsPatternInput(this.patternInputs);
                };
            }

            if (canPush) {
                if (patternDetails instanceof ICircuitPattern) {
                    CircuitPatternService.apply(patternDetails, level,
                            CircuitPatternService.resolveTargets(
                                    level, be.getBlockPos().relative(direction), direction, 5));
                }
                // 先设置电路板，最大程度保证不串配方
                patternDetails.pushInputsToExternalInventory(inputHolder, (what, amount) -> {
                    long inserted = adapter.insert(what, amount, Actionable.MODULATE);
                    if (inserted < amount) {
                        this.addToSendList(what, amount - inserted);
                    }
                });
                this.onPushPatternSuccess(patternDetails);

                this.sendDirection = direction;
                this.sendStacksOut();
                ++this.roundRobinIndex;
                cir.setReturnValue(true);
                return;
            }
        }
        cir.setReturnValue(false);
    }
}
