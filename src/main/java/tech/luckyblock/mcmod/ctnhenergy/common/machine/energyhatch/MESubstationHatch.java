package tech.luckyblock.mcmod.ctnhenergy.common.machine.energyhatch;

import appeng.api.config.Actionable;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.IStorageMounts;
import appeng.api.storage.IStorageProvider;
import appeng.api.storage.MEStorage;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.PowerSubstationMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.feature.IGridConnectedMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.trait.GridNodeHolder;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKey;
import tech.luckyblock.mcmod.ctnhenergy.mixin.gtm.PowerSubstationMachineAccessor;

import java.math.BigInteger;

public class MESubstationHatch extends TieredIOPartMachine implements IGridConnectedMachine, IStorageProvider {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(MESubstationHatch.class,
        TieredIOPartMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    protected final GridNodeHolder nodeHolder;

    @DescSynced
    @Getter
    @Setter
    protected boolean isOnline;

    @Persisted
    @DescSynced
    @Getter
    private int priority = 0;

    protected final IActionSource actionSource;

    private final SubstationEUStorage storage = new SubstationEUStorage();

    PowerSubstationMachine.PowerStationEnergyBank mountedEnergyBank;

    public MESubstationHatch(IMachineBlockEntity holder) {
        super(holder, GTValues.IV, IO.BOTH);
        this.nodeHolder = createNodeHolder();
        this.actionSource = IActionSource.ofMachine(nodeHolder.getMainNode()::getNode);
        nodeHolder.getMainNode().addService(IStorageProvider.class, this);
    }

    protected GridNodeHolder createNodeHolder() {
        return new GridNodeHolder(this);
    }

    @Override
    public IManagedGridNode getMainNode() {
        return nodeHolder.getMainNode();
    }

    @Override
    public void mountInventories(IStorageMounts storageMounts) {
        if(getMainNode().isOnline() && storage.getPowerBank() != null)
            storageMounts.mount(storage, priority);
    }

    public void setPriority(int newValue) {
        priority = newValue;
        remountStorage();
    }

    private void remountStorage() {
        IStorageProvider.requestUpdate(getMainNode());
    }

    @Override
    public boolean canShared() {
        return false;
    }

    @Override
    public void addedToController(IMultiController controller) {
        super.addedToController(controller);
        if(controller instanceof PowerSubstationMachine machine){
            storage.powerStation = machine;
            remountStorage();
        }

    }

    @MustBeInvokedByOverriders
    @Override
    public void removedFromController(IMultiController controller) {
        super.removedFromController(controller);
        storage.reset();
    }

    public class SubstationEUStorage implements MEStorage{

        PowerSubstationMachine powerStation;
        //PowerSubstationMachine.PowerStationEnergyBank getPowerBank();

        @Override
        public Component getDescription() {
            if(getPowerStation() != null){
                return powerStation.getTitle();
            }
            return MESubstationHatch.this.getTitle();
        }

        public PowerSubstationMachine getPowerStation() {
            if(powerStation == null && isFormed()){
                powerStation = (PowerSubstationMachine) getControllers().stream().filter(c -> c instanceof PowerSubstationMachine)
                        .findFirst().orElse(null);
            }
            return powerStation;
        }

        public PowerSubstationMachine.PowerStationEnergyBank getPowerBank() {
            if(getPowerStation() instanceof PowerSubstationMachineAccessor accessor){
                return accessor.getEnergyBank();
            }
            return null;
        }

        @Override
        public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
            return what == EUKey.EU;
        }

        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
            if(what == EUKey.EU && getPowerBank() != null){
                if(mode.isSimulate()) {
                    var canInsert =  getPowerBank().getCapacity().subtract(getPowerBank().getStored());
                    return Math.min(amount, canInsert.longValue());
                }
                else {
                    ((PowerSubstationMachineAccessor)getPowerStation()).getSubscription().updateSubscription();
                    return getPowerBank().fill(amount);
                }

            }
            return 0;
        }

        @Override
        public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
            if(what == EUKey.EU && getPowerBank() != null){
                if(mode.isSimulate()) {
                    var canExtract = getPowerBank().getStored();
                    return Math.min(amount, canExtract.longValue());
                }
                else {
                    ((PowerSubstationMachineAccessor)getPowerStation()).getSubscription().updateSubscription();
                    return getPowerBank().drain(amount);
                }
            }
            return 0;
        }

        @Override
        public void getAvailableStacks(KeyCounter out) {
            if(getPowerBank() != null && getPowerBank().getStored().compareTo(BigInteger.ZERO) > 0){
                out.add(EUKey.EU, getPowerBank().getStored().longValue());
            }
        }

        void reset(){
            powerStation = null;
        }
    }

}
