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
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

import net.minecraft.network.chat.Component;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.IStorageMounts;
import appeng.api.storage.IStorageProvider;
import appeng.api.storage.MEStorage;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.MEPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.multi.PowerSubstationMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKey;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.VoltageKey;
import tech.luckyblock.mcmod.ctnhenergy.utils.CEUtil;

public class MESubstationHatch extends MEPartMachine implements IStorageProvider {

    @DescSynced
    @Getter
    @Setter
    protected boolean isOnline;

    @Persisted
    @Getter
    private int priority = 0;

    private final SubstationEUStorage storage = new SubstationEUStorage();

    PowerSubstationMachine.PowerStationEnergyBank mountedEnergyBank;

    public MESubstationHatch(IMachineBlockEntity holder) {
        super(holder, GTValues.IV, IO.BOTH);
        nodeHost.getMainNode().addService(IStorageProvider.class, this);
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
                        () -> this.isOnline ? "gtceu.gui.me_network.online" : "gtceu.gui.me_network.offline"),
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
        storage.reset();
        remountStorage();
    }

    public class SubstationEUStorage implements MEStorage {

        PowerSubstationMachine powerStation;

        @Override
        public Component getDescription() {
            if (getPowerStation() != null) {
                return powerStation.getTitle();
            }
            return MESubstationHatch.this.getTitle();
        }

        public PowerSubstationMachine getPowerStation() {
            if (powerStation == null && isFormed()) {
                powerStation = (PowerSubstationMachine) getControllers().stream()
                        .filter(c -> c instanceof PowerSubstationMachine)
                        .findFirst().orElse(null);
            }
            return powerStation;
        }

        public PowerSubstationMachine.PowerStationEnergyBank getPowerBank() {
            if (getPowerStation() != null) {
                return getPowerStation().getEnergyBank();
            }
            return null;
        }

        @Override
        public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
            return what == EUKey.EU;
        }

        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
            if (what == EUKey.EU && getPowerBank() != null && getPowerStation().isWorkingEnabled()) {
                if (mode.isSimulate()) {
                    long canInsert = CEUtil
                            .clampToLong(getPowerBank().getCapacity().subtract(getPowerBank().getStored()));
                    return Math.min(amount, canInsert);
                } else {
                    getPowerStation().getWorkLogic().updateTickSubscription();
                    return getPowerBank().fill(amount);
                }

            }
            return 0;
        }

        @Override
        public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
            if (getPowerBank() == null || !getPowerStation().isWorkingEnabled()) return 0;

            if (what instanceof VoltageKey voltageKey && voltageKey.getTier() <= getPowerBank().getTier() &&
                    mode.isSimulate()) {
                return 1;
            }

            if (what == EUKey.EU) {
                if (mode.isSimulate()) {
                    long canExtract = CEUtil.clampToLong(getPowerBank().getStored());
                    return Math.min(amount, canExtract);
                } else {
                    getPowerStation().getWorkLogic().updateTickSubscription();
                    return getPowerBank().drain(amount);
                }
            }
            return 0;
        }

        @Override
        public void getAvailableStacks(KeyCounter out) {
            if (getPowerBank() != null && getPowerStation().isWorkingEnabled()) {
                out.add(EUKey.EU, CEUtil.clampToLong(getPowerBank().getStored()));
            }
        }

        void reset() {
            powerStation = null;
        }
    }
}
