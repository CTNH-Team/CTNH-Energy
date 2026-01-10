package tech.luckyblock.mcmod.ctnhenergy.common.machine.ultimatepatternbuffer;

import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.ingredient.EnergyStack;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import org.jetbrains.annotations.NotNull;
import tech.luckyblock.mcmod.ctnhenergy.api.ProxyRecipeHandler;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.advancedpatternbuffer.MEAdvancedPatternBufferPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.advancedpatternbuffer.MEAdvancedPatternBufferProxyPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEMachines;

import java.util.ArrayList;
import java.util.List;

public class MEUltimatePatternBufferProxyPartMachine extends MEAdvancedPatternBufferProxyPartMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MEUltimatePatternBufferProxyPartMachine.class, MEAdvancedPatternBufferProxyPartMachine.MANAGED_FIELD_HOLDER);

    private final ProxyRecipeHandler.ProxyEnergyRecipeHandler proxyEnergyRecipeHandler;

    public MEUltimatePatternBufferProxyPartMachine(IMachineBlockEntity holder) {
        super(holder);
        proxyEnergyRecipeHandler = ProxyRecipeHandler.createEnergyHandler(this, IO.IN);
    }

    @Override
    public boolean isBuffer(MetaMachine machine) {
        return machine.getDefinition() == CEMachines.ME_ULTIMATE_PATTERN_BUFFER;
    }

    @Override
    public void updateProxy(MEAdvancedPatternBufferPartMachine machine) {
        super.updateProxy(machine);
        if(machine instanceof MEUltimatePatternBufferPartMachine buffer)
            proxyEnergyRecipeHandler.setProxy(buffer.getEnergyContainer());
        getControllers().forEach(IMultiController::onStructureFormed);
    }

    @Override
    public void clearProxy() {
        super.clearProxy();
        proxyEnergyRecipeHandler.setProxy(null);
    }

    @Override
    public List<RecipeHandlerList> getRecipeHandlers() {
        var list = new ArrayList<>(super.getRecipeHandlers());
        list.add(RecipeHandlerList.of(IO.IN, proxyEnergyRecipeHandler));
        return list;
    }


}
