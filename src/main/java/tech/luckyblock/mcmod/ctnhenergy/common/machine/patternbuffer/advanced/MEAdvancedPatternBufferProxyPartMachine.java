package tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.advanced;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;

import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;

import net.minecraft.world.item.crafting.Ingredient;
import tech.luckyblock.mcmod.ctnhenergy.api.ProxyRecipeHandler;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.standard.MEPatternBufferPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.standard.MEPatternBufferProxyPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEMachines;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MEAdvancedPatternBufferProxyPartMachine extends MEPatternBufferProxyPartMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MEAdvancedPatternBufferProxyPartMachine.class, MEPatternBufferProxyPartMachine.MANAGED_FIELD_HOLDER);

    protected final ProxyRecipeHandler<Ingredient> itemOutput;
    protected final ProxyRecipeHandler<FluidIngredient> fluidOutput;

    public MEAdvancedPatternBufferProxyPartMachine(IMachineBlockEntity holder){
        this(holder, GTValues.ZPM);
    }

    public MEAdvancedPatternBufferProxyPartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier, MEAdvancedPatternBufferPartMachine.MAX_PATTERN_COUNT);
        itemOutput = ProxyRecipeHandler.createItemHandler(this, IO.OUT);
        fluidOutput = ProxyRecipeHandler.createFluidHandler(this, IO.OUT);
    }

    public boolean isBuffer(MetaMachine machine){
        return machine.getDefinition() == CEMachines.ME_ADVANCED_PATTERN_BUFFER;
    }

    @Override
    public void updateProxy(MEPatternBufferPartMachine machine) {
        super.updateProxy(machine);
        if(machine instanceof MEAdvancedPatternBufferPartMachine amachine){
            itemOutput.setProxy(amachine.getOutputInventory());
            fluidOutput.setProxy(amachine.getOutputTank());
        }
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

}
