package tech.luckyblock.mcmod.ctnhenergy.common.machine;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.IEnergyInfoProvider;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TooltipsPanel;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TieredMachine;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMaintenanceMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.IBatteryData;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PowerSubstationMachine extends WorkableMultiblockMachine
        implements IEnergyInfoProvider, IFancyUIMachine, IDisplayUIMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            PowerSubstationMachine.class, WorkableMultiblockMachine.MANAGED_FIELD_HOLDER);

    // Structure Constants
    public static final int MAX_BATTERY_LAYERS = 18;
    public static final int MIN_CASINGS = 14;

    // Passive Drain Constants
    // 1% capacity per 24 hours
    public static final long PASSIVE_DRAIN_DIVISOR = 20 * 60 * 60 * 24 * 100;
    // no more than 100kEU/t per storage block
    public static final long PASSIVE_DRAIN_MAX_PER_STORAGE = 100_000L;

    // Match Context Headers
    public static final String PMC_BATTERY_HEADER = "PSSBattery_";

    private static final BigInteger BIG_INTEGER_MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

    private IMaintenanceMachine maintenance;

    @Persisted
    @Getter
    private final PowerStationEnergyBank energyBank;
    private EnergyContainerList inputHatches;
    private EnergyContainerList outputHatches;
    private long passiveDrain;

    // Stats tracked for UI display
    private long netInLastSec;
    @Getter
    private long inputPerSec;
    private long netOutLastSec;
    @Getter
    private long outputPerSec;
    @Getter
    protected ConditionalSubscriptionHandler tickSubscription;

    public PowerSubstationMachine(IMachineBlockEntity holder) {
        super(holder);
        this.tickSubscription = new ConditionalSubscriptionHandler(this, this::transferEnergyTick, this::isFormed);
        this.energyBank = PowerStationEnergyBank.createEnergyBank(this);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();


        List<IBatteryData> batteries = new ArrayList<>();
        for (Map.Entry<String, Object> battery : getMultiblockState().getMatchContext().entrySet()) {
            if (battery.getKey().startsWith(PMC_BATTERY_HEADER) &&
                    battery.getValue() instanceof BatteryMatchWrapper wrapper) {
                for (int i = 0; i < wrapper.amount; i++) {
                    batteries.add(wrapper.partType);
                }
            }
        }
        if (batteries.isEmpty()) {
            // only empty batteries found in the structure
            onStructureInvalid();
            return;
        }
        this.energyBank.rebuild(batteries);

        List<IEnergyContainer> inputs = new ArrayList<>();
        List<IEnergyContainer> outputs = new ArrayList<>();
        Long2ObjectMap<IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap",
                Long2ObjectMaps::emptyMap);

        for (IMultiPart part : getParts()) {
            IO io = ioMap.getOrDefault(part.self().getPos().asLong(), IO.BOTH);
            if (io == IO.NONE) continue;
            if (part instanceof IMaintenanceMachine maintenanceMachine) {
                this.maintenance = maintenanceMachine;
            }
            if(part instanceof TieredMachine machine && machine.getTier() > energyBank.getTier())
                continue;
            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                if (!handlerList.isValid(io)) continue;

                var containers = handlerList.getCapability(EURecipeCapability.CAP).stream()
                        .filter(IEnergyContainer.class::isInstance)
                        .map(IEnergyContainer.class::cast)
                        .toList();

                if (handlerList.getHandlerIO().support(IO.IN)) {
                    inputs.addAll(containers);
                } else if (handlerList.getHandlerIO().support(IO.OUT)) {
                    outputs.addAll(containers);
                }

                traitSubscriptions
                        .add(handlerList.subscribe(tickSubscription::updateSubscription, EURecipeCapability.CAP));
            }
        }
        this.inputHatches = new EnergyContainerList(inputs);
        this.outputHatches = new EnergyContainerList(outputs);

        this.passiveDrain = this.energyBank.getPassiveDrainPerTick();
    }

    @Override
    public void onStructureInvalid() {
        // don't null out energyBank since it holds the stored energy, which
        // we need to hold on to across rebuilds to not void all energy if a
        // multiblock part or block other than the controller is broken.
        inputHatches = null;
        outputHatches = null;
        passiveDrain = 0;
        netInLastSec = 0;
        inputPerSec = 0;
        netOutLastSec = 0;
        outputPerSec = 0;
        super.onStructureInvalid();
    }

    protected void transferEnergyTick() {
        if (!getLevel().isClientSide) {
            if (getOffsetTimer() % 20 == 0) {
                // active here is just used for rendering
                getRecipeLogic()
                        .setStatus(energyBank.hasEnergy() ? RecipeLogic.Status.WORKING : RecipeLogic.Status.IDLE);
                inputPerSec = netInLastSec;
                outputPerSec = netOutLastSec;
                netInLastSec = 0;
                netOutLastSec = 0;
            }

            if (isWorkingEnabled() && isFormed()) {
                // Bank from Energy Input Hatches
                long energyBanked = energyBank.fill(inputHatches.getEnergyStored());
                inputHatches.changeEnergy(-energyBanked);
                netInLastSec += energyBanked;

                // Passive drain
                long energyPassiveDrained = energyBank.drain(getPassiveDrain());
                netOutLastSec += energyPassiveDrained;

                // Debank to Dynamo Hatches
                long energyDebanked = energyBank
                        .drain(outputHatches.getEnergyCapacity() - outputHatches.getEnergyStored());
                outputHatches.changeEnergy(energyDebanked);
                netOutLastSec += energyDebanked;
            }
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        IDisplayUIMachine.super.addDisplayText(textList);
        if (isFormed()) {
            if (!isWorkingEnabled()) {
                textList.add(Component.translatable("gtceu.multiblock.work_paused"));

            } else if (isActive()) {
               textList.add(Component.translatable("gtceu.multiblock.large_miner.working"));
            } else {
                textList.add(Component.translatable("gtceu.multiblock.idling"));
            }

            if (recipeLogic.isWaiting()) {
                textList.add(Component.translatable("gtceu.multiblock.waiting")
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
            }

            if (energyBank != null) {
                BigInteger energyStored = energyBank.getStored();
                BigInteger energyCapacity = energyBank.getCapacity();

                var STYLE_GOLD = Style.EMPTY.withColor(ChatFormatting.GOLD);
                var STYLE_DARK_RED = Style.EMPTY.withColor(ChatFormatting.DARK_RED);
                var STYLE_GREEN = Style.EMPTY.withColor(ChatFormatting.GREEN);
                var STYLE_RED = Style.EMPTY.withColor(ChatFormatting.RED);

                var storedComponent = Component.literal(FormattingUtil.formatNumbers(energyStored));
                textList.add(Component.translatable("gtceu.multiblock.power_substation.stored",
                        storedComponent.setStyle(STYLE_GOLD)));

                var capacityComponent = Component.literal(FormattingUtil.formatNumbers(energyCapacity));
                textList.add(Component.translatable("gtceu.multiblock.power_substation.capacity",
                        capacityComponent.setStyle(STYLE_GOLD)));

                var passiveDrainComponent = Component.literal(FormattingUtil.formatNumbers(getPassiveDrain()));
                textList.add(Component.translatable("gtceu.multiblock.power_substation.passive_drain",
                        passiveDrainComponent.setStyle(STYLE_DARK_RED)));

                var avgInComponent = Component.literal(FormattingUtil.formatNumbers(inputPerSec / 20));
                textList.add(Component
                        .translatable("gtceu.multiblock.power_substation.average_in",
                                avgInComponent.setStyle(STYLE_GREEN))
                        .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.translatable("gtceu.multiblock.power_substation.average_in_hover")))));

                var avgOutComponent = Component.literal(FormattingUtil.formatNumbers(Math.abs(outputPerSec / 20)));
                textList.add(Component
                        .translatable("gtceu.multiblock.power_substation.average_out",
                                avgOutComponent.setStyle(STYLE_RED))
                        .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.translatable("gtceu.multiblock.power_substation.average_out_hover")))));

                if (inputPerSec > outputPerSec) {
                    BigInteger timeToFillSeconds = energyCapacity.subtract(energyStored)
                            .divide(BigInteger.valueOf(inputPerSec - outputPerSec));
                    textList.add(Component.translatable("gtceu.multiblock.power_substation.time_to_fill",
                            getTimeToFillDrainText(timeToFillSeconds).setStyle(STYLE_GREEN)));
                } else if (inputPerSec < outputPerSec) {
                    BigInteger timeToDrainSeconds = energyStored
                            .divide(BigInteger.valueOf(outputPerSec - inputPerSec));
                    textList.add(Component.translatable("gtceu.multiblock.power_substation.time_to_drain",
                            getTimeToFillDrainText(timeToDrainSeconds).setStyle(STYLE_RED)));
                }
            }
        }
        getDefinition().getAdditionalDisplay().accept(this, textList);
    }

    private static MutableComponent getTimeToFillDrainText(BigInteger timeToFillSeconds) {
        if (timeToFillSeconds.compareTo(BIG_INTEGER_MAX_LONG) > 0) {
            // too large to represent in a java Duration
            timeToFillSeconds = BIG_INTEGER_MAX_LONG;
        }

        Duration duration = Duration.ofSeconds(timeToFillSeconds.longValue());
        String key;
        long fillTime;
        if (duration.getSeconds() <= 180) {
            fillTime = duration.getSeconds();
            key = "gtceu.multiblock.power_substation.time_seconds";
        } else if (duration.toMinutes() <= 180) {
            fillTime = duration.toMinutes();
            key = "gtceu.multiblock.power_substation.time_minutes";
        } else if (duration.toHours() <= 72) {
            fillTime = duration.toHours();
            key = "gtceu.multiblock.power_substation.time_hours";
        } else if (duration.toDays() <= 730) { // 2 years
            fillTime = duration.toDays();
            key = "gtceu.multiblock.power_substation.time_days";
        } else if (duration.toDays() / 365 < 1_000_000) {
            fillTime = duration.toDays() / 365;
            key = "gtceu.multiblock.power_substation.time_years";
        } else {
            return Component.translatable("gtceu.multiblock.power_substation.time_forever");
        }

        return Component.translatable(key, FormattingUtil.formatNumbers(fillTime));
    }

    public long getPassiveDrain() {
        if (ConfigHolder.INSTANCE.machines.enableMaintenance) {
            if (maintenance == null) {
                for (IMultiPart part : getParts()) {
                    if (part instanceof IMaintenanceMachine maintenanceMachine) {
                        this.maintenance = maintenanceMachine;
                        break;
                    }
                }
            }
            int multiplier = 1 + maintenance.getNumMaintenanceProblems();
            double modifier = maintenance.getDurationMultiplier();
            return (long) (passiveDrain * multiplier * modifier);
        }
        return passiveDrain;
    }


    @Override
    public EnergyInfo getEnergyInfo() {
        return new EnergyInfo(energyBank.getCapacity(), energyBank.getStored());
    }

    @Override
    public boolean supportsBigIntEnergyValues() {
        return true;
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, 182 + 8, 117 + 8);
        group.addWidget(new DraggableScrollableWidgetGroup(4, 4, 182, 117).setBackground(getScreenTexture())
                .addWidget(new LabelWidget(4, 5, self().getBlockState().getBlock().getDescriptionId()))
                .addWidget(new ComponentPanelWidget(4, 17, this::addDisplayText)
                        .setMaxWidthLimit(150)
                        .clickHandler(this::handleDisplayClick)));
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        return new ModularUI(198, 208, this, entityPlayer).widget(new FancyMachineUIWidget(this, 198, 208));
    }

    @Override
    public List<IFancyUIProvider> getSubTabs() {
        return getParts().stream().filter(IFancyUIProvider.class::isInstance).map(IFancyUIProvider.class::cast)
                .toList();
    }

    @Override
    public void attachTooltips(TooltipsPanel tooltipsPanel) {
        for (IMultiPart part : getParts()) {
            part.attachFancyTooltipsToController(this, tooltipsPanel);
        }
    }


    public static class PowerStationEnergyBank extends MachineTrait {

        protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER =
                new ManagedFieldHolder(PowerStationEnergyBank.class);

        /* ----------------------------
         * 持久化字段
         * ---------------------------- */

        @Persisted
        private final List<Long> storage = new ArrayList<>();

        @Persisted
        private final List<Long> maximums = new ArrayList<>();

        @Persisted
        private int index = 0;

        // 非持久化，懒计算
        private BigInteger capacity = BigInteger.ZERO;

        @Persisted
        @Getter
        int tier;
        /* ----------------------------
         * 构造 / 创建
         * ---------------------------- */

        private PowerStationEnergyBank(MetaMachine machine) {
            super(machine);
        }

        public static PowerStationEnergyBank createEnergyBank(MetaMachine machine) {
            return new PowerStationEnergyBank(machine);
        }

        /* ----------------------------
         * rebuild：原对象重建
         * ---------------------------- */

        public void rebuild(@NotNull List<IBatteryData> batteries) {
            if (batteries.isEmpty()) {
                throw new IllegalArgumentException("Cannot rebuild Power Substation power bank with no batteries!");
            }

            BigInteger stored = getStored();

            storage.clear();
            maximums.clear();

            for (IBatteryData battery : batteries) {
                storage.add(0L);
                maximums.add(battery.getCapacity());
                tier = Math.max(tier, battery.getTier());
            }

            index = 0;
            capacity = null; // 强制重新计算

            fill(stored.min(getCapacity()).longValue());
        }

        /* ----------------------------
         * Fill / Drain
         * ---------------------------- */

        public long fill(long amount) {
            if (amount <= 0 || storage.isEmpty()) return 0;

            long filled = 0;

            ensureIndexValid();

            while (amount > 0 && index < storage.size()) {
                long stored = storage.get(index);
                long max = maximums.get(index);

                long canFill = Math.min(max - stored, amount);
                if (canFill > 0) {
                    storage.set(index, stored + canFill);
                    amount -= canFill;
                    filled += canFill;
                }

                if (storage.get(index).equals(max) && index < storage.size() - 1) {
                    index++;
                } else {
                    break;
                }
            }

            return filled;
        }

        public long drain(long amount) {
            if (amount <= 0 || storage.isEmpty()) return 0;

            long drained = 0;

            ensureIndexValid();

            while (amount > 0 && index >= 0) {
                long stored = storage.get(index);

                long canDrain = Math.min(stored, amount);
                if (canDrain > 0) {
                    storage.set(index, stored - canDrain);
                    amount -= canDrain;
                    drained += canDrain;
                }

                if (storage.get(index) == 0 && index > 0) {
                    index--;
                } else {
                    break;
                }
            }

            return drained;
        }

        /* ----------------------------
         * 查询
         * ---------------------------- */

        public BigInteger getStored() {
            return summarize(storage);
        }

        public BigInteger getCapacity() {
            if (capacity == null || capacity.signum() == 0) {
                capacity = summarize(maximums);
            }
            return capacity;
        }

        public boolean hasEnergy() {
            for (long l : storage) {
                if (l > 0) return true;
            }
            return false;
        }

        /* ----------------------------
         * 安全辅助
         * ---------------------------- */

        private void ensureIndexValid() {
            if (storage.isEmpty()) {
                index = 0;
                return;
            }

            if (index < 0) {
                index = 0;
            } else if (index >= storage.size()) {
                index = storage.size() - 1;
            }

            // 自动跳转到最近可用 battery
            while (index < storage.size() - 1 && storage.get(index).equals(maximums.get(index))) {
                index++;
            }
            while (index > 0 && storage.get(index) == 0) {
                index--;
            }
        }

        /* ----------------------------
         * 工具
         * ---------------------------- */

        private static BigInteger summarize(List<Long> values) {
            BigInteger total = BigInteger.ZERO;
            long current = 0;

            for (long v : values) {
                if (current != 0 && v > Long.MAX_VALUE - current) {
                    total = total.add(BigInteger.valueOf(current));
                    current = 0;
                }
                current += v;
            }

            if (current != 0) {
                total = total.add(BigInteger.valueOf(current));
            }

            return total;
        }

        @VisibleForTesting
        public long getPassiveDrainPerTick() {
            long drain = 0;
            int excluded = 0;

            for (long max : maximums) {
                if (max / PASSIVE_DRAIN_DIVISOR >= PASSIVE_DRAIN_MAX_PER_STORAGE) {
                    excluded++;
                } else {
                    drain += max / PASSIVE_DRAIN_DIVISOR;
                }
            }

            return drain + PASSIVE_DRAIN_MAX_PER_STORAGE * excluded;
        }

        @Override
        public ManagedFieldHolder getFieldHolder() {
            return MANAGED_FIELD_HOLDER;
        }
    }



    @Getter
    public static class BatteryMatchWrapper {

        private final IBatteryData partType;
        private int amount;

        public BatteryMatchWrapper(IBatteryData partType) {
            this.partType = partType;
        }

        public BatteryMatchWrapper increment() {
            amount++;
            return this;
        }
    }
}
