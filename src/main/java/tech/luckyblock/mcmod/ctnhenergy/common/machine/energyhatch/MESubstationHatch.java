package tech.luckyblock.mcmod.ctnhenergy.common.machine.energyhatch;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.widget.IntInputWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;

import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextTextureWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

import net.minecraft.network.chat.Component;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.events.GridPowerStorageStateChanged;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.IStorageMounts;
import appeng.api.storage.IStorageProvider;
import appeng.api.storage.MEStorage;
import lombok.Getter;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.MEPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKey;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.VoltageKey;
import tech.luckyblock.mcmod.ctnhenergy.common.multi.PowerSubstationMachine;
import tech.luckyblock.mcmod.ctnhenergy.utils.CEUtil;

public class MESubstationHatch extends MEPartMachine implements IStorageProvider, IAEPowerStorage {

    @Persisted
    @Getter
    private int priority = 0;

    private final SubstationEUStorage storage = new SubstationEUStorage();
    private boolean unloading;

    public MESubstationHatch(IMachineBlockEntity holder) {
        super(holder, GTValues.IV, IO.BOTH, false, false);
        nodeHost.getMainNode().addService(IStorageProvider.class, this)
                .addService(IAEPowerStorage.class, this);
    }

    //////////////////////////////////////
    // ***** AE ******//
    //////////////////////////////////////

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        super.onMainNodeStateChanged(reason);
        remountStorage();
    }

    @Override
    public void mountInventories(IStorageMounts storageMounts) {
        if (isNodeActive() && storage.getPowerBank() != null)
            storageMounts.mount(storage, priority);
    }

    public void setPriority(int newValue) {
        priority = newValue;
        remountStorage();
    }

    private void remountStorage() {
        IStorageProvider.requestUpdate(nodeHost.getMainNode());
        nodeHost.getMainNode().ifPresent(grid -> grid.postEvent(new GridPowerStorageStateChanged(this,
                GridPowerStorageStateChanged.PowerEventType.PROVIDE_POWER)));
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        super.setWorkingEnabled(workingEnabled);
        if (isWorkingEnabled()) {
            nodeHost.getMainNode().ifPresent(grid -> grid.postEvent(new GridPowerStorageStateChanged(this,
                    GridPowerStorageStateChanged.PowerEventType.PROVIDE_POWER)));
        }
        // TODO: 监听主机的开启事件
    }

    @Override
    public boolean canShared() {
        return false;
    }

    //////////////////////////////////////
    // ***** UI ******//
    //////////////////////////////////////
    @Override
    public Widget createUIWidget() {
        WidgetGroup priorityAmountGroup = new WidgetGroup(0, 0, 100, 70);
        priorityAmountGroup.addWidgets(
                new LabelWidget(0, 2,
                        () -> isNodeActive() ? "gtceu.gui.me_network.online" : "gtceu.gui.me_network.offline"),
                new TextTextureWidget(25, 20, 50, 15, "gui.ae2.Priority"),
                new IntInputWidget(0, 35, 100, 20, this::getPriority, this::setPriority) {

                    @Override
                    protected Integer defaultMin() {
                        return Integer.MIN_VALUE;
                    }
                });
        return priorityAmountGroup;
    }

    @Override
    public void addedToController(IMultiController controller) {
        super.addedToController(controller);
        if (controller instanceof PowerSubstationMachine machine) {
            storage.powerStation = machine;
            remountStorage();
        }
    }

    @MustBeInvokedByOverriders
    @Override
    public void removedFromController(IMultiController controller) {
        super.removedFromController(controller);
        clearControllerRuntime();
    }

    @Override
    public void unloadedFromController(IMultiController controller) {
        super.unloadedFromController(controller);
        clearControllerRuntime();
    }

    private void clearControllerRuntime() {
        storage.reset();
        if (!unloading) {
            remountStorage();
        }
    }

    @Override
    public void onUnload() {
        unloading = true;
        try {
            super.onUnload();
        } finally {
            // The controller can already be outside the loaded area, so the base unload path may have no runtime
            // controller callback to deliver. Do not retain its machine and energy-bank graph through this hatch.
            storage.reset();
            unloading = false;
        }
    }

    private boolean isOperationalRuntimeController(PowerSubstationMachine controller) {
        return controller != null &&
                controllers.stream().anyMatch(candidate -> candidate == controller) &&
                controller.isStructureOperational() && controller.self().hasRuntimePart(this);
    }

    @Override
    public double injectAEPower(double v, Actionable actionable) {
        return 0;
    }

    @Override
    public double getAEMaxPower() {
        return 0;
    }

    @Override
    public double getAECurrentPower() {
        return 0;
    }

    @Override
    public boolean isAEPublicPowerStorage() {
        return true;
    }

    @Override
    public AccessRestriction getPowerFlow() {
        return AccessRestriction.READ;
    }

    @Override
    public double extractAEPower(double v, Actionable mode, PowerMultiplier pm) {
        return pm.divide(2 * storage.extract(EUKey.EU, (long) Math.ceil(pm.multiply(v) / 2), mode, actionSource));
    }

    public class SubstationEUStorage implements MEStorage {

        PowerSubstationMachine powerStation;

        @Override
        public Component getDescription() {
            var station = getPowerStation();
            if (station != null) {
                return station.getTitle();
            }
            return MESubstationHatch.this.getTitle();
        }

        public PowerSubstationMachine getPowerStation() {
            if (!isOperationalRuntimeController(powerStation)) {
                powerStation = controllers.stream()
                        .filter(PowerSubstationMachine.class::isInstance)
                        .map(PowerSubstationMachine.class::cast)
                        .filter(MESubstationHatch.this::isOperationalRuntimeController)
                        .findFirst().orElse(null);
            }
            return powerStation;
        }

        public PowerSubstationMachine.PowerStationEnergyBank getPowerBank() {
            var station = getPowerStation();
            if (isWorkingEnabled() && station != null) {
                return station.getEnergyBank();
            }
            return null;
        }

        @Override
        public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
            return what == EUKey.EU;
        }

        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
            var powerBank = getPowerBank();
            var station = getPowerStation();
            if (what == EUKey.EU && powerBank != null && station != null && station.isWorkingEnabled()) {
                if (mode.isSimulate()) {
                    long canInsert = CEUtil.clampToLong(powerBank.getCapacity().subtract(powerBank.getStored()));
                    return Math.min(amount, canInsert);
                } else {
                    station.getWorkLogic().updateTickSubscription();
                    return powerBank.fill(amount);
                }

            }
            return 0;
        }

        @Override
        public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
            var powerBank = getPowerBank();
            var station = getPowerStation();
            if (powerBank == null || station == null || !station.isWorkingEnabled()) return 0;

            if (what instanceof VoltageKey voltageKey && voltageKey.getTier() <= powerBank.getTier() &&
                    mode.isSimulate()) {
                return 1;
            }

            if (what == EUKey.EU) {
                if (mode.isSimulate()) {
                    long canExtract = CEUtil.clampToLong(powerBank.getStored());
                    return Math.min(amount, canExtract);
                } else {
                    station.getWorkLogic().updateTickSubscription();
                    return powerBank.drain(amount);
                }
            }
            return 0;
        }

        @Override
        public void getAvailableStacks(KeyCounter out) {
            var powerBank = getPowerBank();
            var station = getPowerStation();
            if (powerBank != null && station != null && station.isWorkingEnabled()) {
                out.add(EUKey.EU, CEUtil.clampToLong(powerBank.getStored()));
            }
        }

        void reset() {
            powerStation = null;
        }
    }
}
