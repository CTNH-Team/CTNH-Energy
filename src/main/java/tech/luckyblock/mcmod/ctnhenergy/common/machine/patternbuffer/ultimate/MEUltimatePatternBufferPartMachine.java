package tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.ultimate;

import appeng.api.networking.IGridNodeListener;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.advanced.MEAdvancedPatternBufferPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.energyhatch.MEEnergyInputConfigurator;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.energyhatch.MEEnergyPartMachine;

import java.util.List;

public class MEUltimatePatternBufferPartMachine extends MEAdvancedPatternBufferPartMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MEUltimatePatternBufferPartMachine.class, MEAdvancedPatternBufferPartMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @Getter
    public final MEEnergyPartMachine.MEEnergyContainer energyContainer;

    @Nullable
    protected TickableSubscription energySubs;

    public MEUltimatePatternBufferPartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
        energyContainer = new MEEnergyPartMachine.MEEnergyContainer(this, io);
    }

    @Override
    public @NotNull List<RecipeHandlerList> getRecipeHandlers() {
        var energyIN = RecipeHandlerList.of(IO.IN, energyContainer);
        var all = super.getRecipeHandlers();
        all.add(energyIN);
        return all;
    }

    @Override
    public void attachSideTabs(TabsWidget sideTabs) {
        super.attachSideTabs(sideTabs);
        sideTabs.attachSubTab(new MEEnergyInputConfigurator(this, energyContainer));
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        super.onMainNodeStateChanged(reason);
        updateEnergySubscription();
    }

    protected void updateEnergySubscription(){
        if(isWorkingEnabled() && isOnline()){
            energySubs = subscribeServerTick(energySubs, this::updateEnergy);
        } else if (energySubs != null){
            energySubs.unsubscribe();
            energySubs = null;
        }
    }

    protected void updateEnergy(){
        if (shouldSyncME() && updateMEStatus()) {
            energyContainer.updateEnergyCapacity();
            updateEnergySubscription();
        }
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
