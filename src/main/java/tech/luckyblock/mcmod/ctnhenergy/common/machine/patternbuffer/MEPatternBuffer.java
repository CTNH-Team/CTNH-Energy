package tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.KeyCounter;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.MEPartMachine;

import java.util.List;

public class MEPatternBuffer extends MEPartMachine implements ICraftingProvider {

    public MEPatternBuffer(IMachineBlockEntity holder, int tier, IO io) {
        super(holder, tier, io);
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        return List.of();
    }

    @Override
    public boolean pushPattern(IPatternDetails iPatternDetails, KeyCounter[] keyCounters) {
        return false;
    }

    @Override
    public boolean isBusy() {
        return false;
    }
}
