package tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;

import appeng.api.networking.IGridNodeListener;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.MEPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.handler.MEStorageFluidHandler;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.handler.MEStorageItemHandler;

public class MEOutputMachine extends MEPartMachine {

    public MEOutputMachine(IMachineBlockEntity holder, int tier, IO io, boolean allowItem, boolean allowFluid) {
        super(holder, tier, io);
        if (allowItem) new MEStorageItemHandler(this, io, this.nodeSupplier, null);
        if (allowFluid) new MEStorageFluidHandler(this, io, this.nodeSupplier, null);
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        super.onMainNodeStateChanged(reason);
        getControllers().forEach(controller -> {
            if (controller instanceof IRecipeLogicMachine rlm) {
                rlm.getRecipeLogic().updateTickSubscription();
            }
        });
    }
}
