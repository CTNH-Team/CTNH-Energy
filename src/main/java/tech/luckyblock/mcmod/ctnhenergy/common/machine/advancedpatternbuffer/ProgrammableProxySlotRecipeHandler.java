package tech.luckyblock.mcmod.ctnhenergy.common.machine.advancedpatternbuffer;

import com.gregtechceu.gtceu.api.capability.recipe.*;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.IRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerGroupDistinctness;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;

import tech.luckyblock.mcmod.ctnhenergy.api.ProxyRecipeHandler;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.advancedpatternbuffer.ProgrammableSlotRecipeHandler.SlotRHL;
import com.lowdragmc.lowdraglib.syncdata.ISubscription;

import net.minecraft.world.item.crafting.Ingredient;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public final class ProgrammableProxySlotRecipeHandler {

    private final List<RecipeHandlerList> proxySlotHandlers;
    private final ProxyRecipeHandler<Ingredient> itemOutput;
    private final ProxyRecipeHandler<FluidIngredient> fluidOutput;
    private final int slots;

    public ProgrammableProxySlotRecipeHandler(MEAdvancedPatternBufferProxyPartMachine machine, int slots) {
        this.slots = slots;
        proxySlotHandlers = new ArrayList<>(slots + 1);
        for (int i = 0; i < slots; ++i) {
            proxySlotHandlers.add(new ProxyRHL(machine));
        }
        itemOutput = ProxyRecipeHandler.createItemHandler(machine, IO.OUT);
        fluidOutput = ProxyRecipeHandler.createFluidHandler(machine, IO.OUT);

        List<IRecipeHandler<?>> handlers = new ArrayList<>();
        handlers.add(itemOutput);
        handlers.add(fluidOutput);
        proxySlotHandlers.add(RecipeHandlerList.of(IO.OUT, handlers));
    }

    public void updateProxy(MEAdvancedPatternBufferPartMachine patternBuffer) {
        var slotHandlers = patternBuffer.getInternalRecipeHandler().getSlotHandlers();
        for (int i = 0; i <slots; ++i) {
            ProxyRHL proxyRHL = (ProxyRHL) proxySlotHandlers.get(i);
            ProgrammableSlotRecipeHandler.SlotRHL slotRHL = (SlotRHL) slotHandlers.get(i);
            proxyRHL.setBuffer(patternBuffer, slotRHL);
        }
        itemOutput.setProxy(patternBuffer.getOutputInventory());
        fluidOutput.setProxy(patternBuffer.getOutputTank());
    }

    public void clearProxy() {
        for (int i = 0; i < slots; ++i) {
            ((ProxyRHL) proxySlotHandlers.get(i)).clearBuffer();
        }
    }

    private static class ProxyRHL extends RecipeHandlerList {

        private final ProxyRecipeHandler<Ingredient> circuit;
        private final ProxyRecipeHandler<Ingredient> sharedItem;
        private final ProxyRecipeHandler<Ingredient> slotItem;
        private final ProxyRecipeHandler<FluidIngredient> sharedFluid;
        private final ProxyRecipeHandler<FluidIngredient> slotFluid;


        public ProxyRHL(MEAdvancedPatternBufferProxyPartMachine machine) {
            super(IO.IN);
            circuit = ProxyRecipeHandler.createItemHandler(machine, IO.IN);
            sharedItem = ProxyRecipeHandler.createItemHandler(machine, IO.IN);
            slotItem = ProxyRecipeHandler.createItemHandler(machine, IO.IN);
            sharedFluid = ProxyRecipeHandler.createFluidHandler(machine, IO.IN);
            slotFluid = ProxyRecipeHandler.createFluidHandler(machine, IO.IN);


            addHandlers(circuit, sharedItem, slotItem, sharedFluid, slotFluid);
            this.setGroup(RecipeHandlerGroupDistinctness.BUS_DISTINCT);
        }

        public void setBuffer(MEAdvancedPatternBufferPartMachine buffer, SlotRHL slotRHL) {
            circuit.setProxy(slotRHL.getCircuitInventory());
            sharedItem.setProxy(buffer.getShareInventory());
            sharedFluid.setProxy(buffer.getShareTank());
            slotItem.setProxy(slotRHL.getItemRecipeHandler());
            slotFluid.setProxy(slotRHL.getFluidRecipeHandler());

        }

        public void clearBuffer() {
            circuit.setProxy(null);
            sharedItem.setProxy(null);
            sharedFluid.setProxy(null);
            slotItem.setProxy(null);
            slotFluid.setProxy(null);
        }

        @Override
        public boolean isDistinct() {
            return true;
        }

        @Override
        public void setDistinct(boolean ignored, boolean notify) {}
    }
}
