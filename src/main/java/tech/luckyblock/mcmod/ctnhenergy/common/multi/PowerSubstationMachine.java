package tech.luckyblock.mcmod.ctnhenergy.common.multi;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.IEnergyInfoProvider;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TooltipsPanel;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMaintenanceMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.IBatteryData;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.WorkLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PowerSubstationMachine extends WorkableMultiblockMachine
                                    implements IEnergyInfoProvider, IFancyUIMachine, IDisplayUIMachine {

    @CN("电压等级：")
    @EN("Voltage Tier: ")
    static Lang voltage_tier;

    @CN("缺失有效电容。")
    @EN("No valid batteries found.")
    static Lang invalid_batteries;

    @CN("动力仓等级高于最高电容等级。")
    @EN("Dynamo hatch tier higher than battery tier.")
    static Lang invalid_dynamo_tier;

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

    @Getter
    @Persisted
    private final PowerStationEnergyBank energyBank;
    @NotNull
    private EnergyContainerList inputHatches = EnergyContainerList.EMPTY;
    @NotNull
    private EnergyContainerList outputHatches = EnergyContainerList.EMPTY;
    private long passiveDrain;

    // Stats tracked for UI display
    private long netInLastSec;
    @Getter
    private long inputPerSec;
    private long netOutLastSec;
    @Getter
    private long outputPerSec;

    @Setter
    BigInteger legacyEnergy = BigInteger.ZERO;

    private Lang invalidReason;

    public PowerSubstationMachine(IMachineBlockEntity holder) {
        super(holder);
        this.energyBank = attachTrait(PowerStationEnergyBank.createEnergyBank(this));
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
            invalidReason = invalid_batteries;
            onStructureInvalid();
            return;
        }
        this.energyBank.rebuild(batteries);
        energyBank.fillBig(legacyEnergy);
        legacyEnergy = BigInteger.ZERO;

        List<IEnergyContainer> inputs = new ArrayList<>();
        List<IEnergyContainer> outputs = new ArrayList<>();

        for (IMultiPart part : getParts()) {
            if (part instanceof IMaintenanceMachine maintenanceMachine) {
                this.maintenance = maintenanceMachine;
            }

            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                var containers = handlerList.getCapability(EURecipeCapability.CAP).stream()
                        .filter(IEnergyContainer.class::isInstance)
                        .map(IEnergyContainer.class::cast)
                        .toList();

                if (!containers.isEmpty() && part instanceof TieredPartMachine machine &&
                        machine.getTier() > energyBank.getTier()) {
                    invalidReason = invalid_dynamo_tier;
                    onStructureInvalid();
                    return;
                }

                containers.forEach(c -> {
                    if (((IRecipeHandler<?>) c).getHandlerIO().support(IO.IN)) {
                        inputs.add(c);
                    } else if (((IRecipeHandler<?>) c).getHandlerIO().support(IO.OUT)) {
                        outputs.add(c);
                    }
                });
                workLogic.addNotifier(handlerList::subscribe);
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
        inputHatches = EnergyContainerList.EMPTY;
        outputHatches = EnergyContainerList.EMPTY;
        passiveDrain = 0;
        netInLastSec = 0;
        inputPerSec = 0;
        netOutLastSec = 0;
        outputPerSec = 0;
        maintenance = null;
        super.onStructureInvalid();
    }

    @Override
    public boolean checkPatternWithLock() {
        invalidReason = null;
        return super.checkPatternWithLock();
    }

    @Override
    public void serverRunningTick() {
        if (getOffsetTimer() % 20 == 0) {
            // active here is just used for rendering
            getWorkLogic()
                    .setStatus(energyBank.hasEnergy() ? WorkLogic.Status.WORKING : WorkLogic.Status.IDLE);
            inputPerSec = netInLastSec;
            outputPerSec = netOutLastSec;
            netInLastSec = 0;
            netOutLastSec = 0;
        }
        long energyBanked = energyBank.fill(inputHatches.getEnergyStored());
        inputHatches.changeEnergy(-energyBanked);
        // Passive drain
        energyBank.drain(getPassiveDrain());

        long energyDebanked = energyBank
                .drain(outputHatches.getEnergyCapacity() - outputHatches.getEnergyStored());
        outputHatches.changeEnergy(energyDebanked);
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

            if (energyBank != null) {
                BigInteger energyStored = energyBank.getStored();
                BigInteger energyCapacity = energyBank.getCapacity();

                var STYLE_GOLD = Style.EMPTY.withColor(ChatFormatting.GOLD);
                var STYLE_DARK_RED = Style.EMPTY.withColor(ChatFormatting.DARK_RED);
                var STYLE_GREEN = Style.EMPTY.withColor(ChatFormatting.GREEN);
                var STYLE_RED = Style.EMPTY.withColor(ChatFormatting.RED);

                var voltageComponent = voltage_tier.translate()
                        .append(Component.literal(GTValues.VNF[energyBank.getTier()]));
                textList.add(voltageComponent);

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
        } else {
            MultiblockDisplayText.builder(textList, false);
            if (invalidReason != null) {
                textList.add(invalidReason.translate()
                        .withStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
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
            return (long) (passiveDrain * multiplier);
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
        return getParts().stream().filter(Objects::nonNull).map(IFancyUIProvider.class::cast)
                .toList();
    }

    @Override
    public void attachTooltips(TooltipsPanel tooltipsPanel) {
        for (IMultiPart part : getParts()) {
            part.attachFancyTooltipsToController(this, tooltipsPanel);
        }
    }

    public static class PowerStationEnergyBank extends MachineTrait {

        private final LongArrayList storage = new LongArrayList();

        private final LongArrayList maximums = new LongArrayList();

        private static final String NBT_STORAGE = "PowerStationEnergyBankStorage";
        private static final String NBT_MAXIMUMS = "PowerStationEnergyBankMaximums";

        @Persisted
        private int index = 0;

        private transient boolean indexNormalized;

        private BigInteger capacity = BigInteger.ZERO;

        @Persisted
        @Getter
        int tier;

        private PowerStationEnergyBank(PowerSubstationMachine machine) {
            super(machine);
        }

        public static PowerStationEnergyBank createEnergyBank(PowerSubstationMachine machine) {
            return new PowerStationEnergyBank(machine);
        }

        @Override
        public PowerSubstationMachine getMachine() {
            return (PowerSubstationMachine) super.getMachine();
        }

        @Override
        public void saveCustomPersistedData(@NotNull CompoundTag tag, boolean forDrop) {
            super.saveCustomPersistedData(tag, forDrop);
            tag.putLongArray(NBT_STORAGE, storage.toLongArray());
            tag.putLongArray(NBT_MAXIMUMS, maximums.toLongArray());
        }

        @Override
        public void loadCustomPersistedData(@NotNull CompoundTag tag) {
            super.loadCustomPersistedData(tag);

            storage.clear();
            maximums.clear();
            storage.addElements(0, tag.getLongArray(NBT_STORAGE));
            maximums.addElements(0, tag.getLongArray(NBT_MAXIMUMS));

            if (storage.size() != maximums.size()) {
                storage.clear();
                maximums.clear();
                index = 0;
            }
            capacity = null;
            indexNormalized = false;
        }

        public void rebuild(@NotNull List<IBatteryData> batteries) {
            if (batteries.isEmpty()) {
                throw new IllegalArgumentException("Cannot rebuild Power Substation power bank with no batteries!");
            }

            BigInteger stored = getStored();

            storage.clear();
            maximums.clear();
            tier = 0;

            for (IBatteryData battery : batteries) {
                storage.add(0L);
                maximums.add(battery.getCapacity());
                tier = Math.max(tier, battery.getTier());
            }

            index = 0;
            indexNormalized = true;
            capacity = null; // 强制重新计算

            fillBig(stored);
        }

        public BigInteger fillBig(BigInteger amount) {
            if (amount == null || amount.signum() <= 0 || storage.isEmpty()) {
                return BigInteger.ZERO;
            }

            BigInteger filled = BigInteger.ZERO;

            normalizeIndexIfNeeded();

            while (amount.signum() > 0 && index < storage.size()) {
                long stored = storage.getLong(index);
                long max = maximums.getLong(index);

                long space = max - stored;
                if (space > 0) {
                    long toFill;
                    if (amount.compareTo(BigInteger.valueOf(space)) >= 0) {
                        toFill = space;
                    } else {
                        toFill = amount.longValue();
                    }

                    storage.set(index, stored + toFill);
                    amount = amount.subtract(BigInteger.valueOf(toFill));
                    filled = filled.add(BigInteger.valueOf(toFill));
                }

                if (storage.getLong(index) == max && index < storage.size() - 1) {
                    index++;
                } else {
                    break;
                }
            }

            return filled;
        }

        public long fill(long amount) {
            if (amount <= 0 || storage.isEmpty()) return 0;

            long filled = 0;

            normalizeIndexIfNeeded();

            while (amount > 0 && index < storage.size()) {
                long stored = storage.getLong(index);
                long max = maximums.getLong(index);

                long canFill = Math.min(max - stored, amount);
                if (canFill > 0) {
                    storage.set(index, stored + canFill);
                    amount -= canFill;
                    filled += canFill;
                }

                if (storage.getLong(index) == max && index < storage.size() - 1) {
                    index++;
                } else {
                    break;
                }
            }
            getMachine().netInLastSec += filled;
            return filled;
        }

        public long drain(long amount) {
            if (amount <= 0 || storage.isEmpty()) return 0;

            long drained = 0;

            normalizeIndexIfNeeded();

            while (amount > 0 && index >= 0) {
                long stored = storage.getLong(index);

                long canDrain = Math.min(stored, amount);
                if (canDrain > 0) {
                    storage.set(index, stored - canDrain);
                    amount -= canDrain;
                    drained += canDrain;
                }

                if (storage.getLong(index) == 0 && index > 0) {
                    index--;
                } else {
                    break;
                }
            }
            getMachine().netOutLastSec += drained;
            return drained;
        }

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

        private void normalizeIndexIfNeeded() {
            if (indexNormalized) {
                return;
            }

            if (storage.isEmpty()) {
                index = 0;
                indexNormalized = true;
                return;
            }

            if (index < 0) {
                index = 0;
            } else if (index >= storage.size()) {
                index = storage.size() - 1;
            }

            while (index < storage.size() - 1 && storage.getLong(index) == maximums.getLong(index)) {
                index++;
            }
            while (index > 0 && storage.getLong(index) == 0) {
                index--;
            }

            indexNormalized = true;
        }

        /*
         * ----------------------------
         * 工具
         * ----------------------------
         */

        private static BigInteger summarize(LongList values) {
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
