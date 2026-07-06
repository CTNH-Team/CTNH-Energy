package tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.MEPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.handler.MEStorageItemHandler;

public class MEOutputBusMachine extends MEPartMachine {
    public MEOutputBusMachine(IMachineBlockEntity holder, int tier, IO io) {
        super(holder, tier, io);
        new MEStorageItemHandler(this, io,  this.nodeSupplier, null);
    }
}
