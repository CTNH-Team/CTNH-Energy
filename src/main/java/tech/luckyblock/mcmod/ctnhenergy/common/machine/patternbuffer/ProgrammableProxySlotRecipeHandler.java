package tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer;

import com.gregtechceu.gtceu.api.capability.recipe.*;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerGroupDistinctness;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;

import tech.luckyblock.mcmod.ctnhenergy.api.ProxyRecipeHandler;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.ProgrammableSlotRecipeHandler.SlotRHL;

import net.minecraft.world.item.crafting.Ingredient;

import lombok.Getter;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.standard.MEPatternBufferPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.standard.MEPatternBufferProxyPartMachine;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class ProgrammableProxySlotRecipeHandler {

    private final List<RecipeHandlerList> proxySlotHandlers;
    private final int slots;

    public ProgrammableProxySlotRecipeHandler(MEPatternBufferProxyPartMachine machine, int slots) {
        this.slots = slots;
        proxySlotHandlers = new ArrayList<>(slots + 1);
        for (int i = 0; i < slots; ++i) {
            proxySlotHandlers.add(new ProxyRHL(machine));
        }

        List<IRecipeHandler<?>> handlers = new ArrayList<>();
        proxySlotHandlers.add(RecipeHandlerList.of(IO.OUT, handlers));
    }

    public void updateProxy(MEPatternBufferPartMachine patternBuffer) {
        var slotHandlers = patternBuffer.getInternalRecipeHandler().getSlotHandlers();
        for (int i = 0; i <slots; ++i) {
            ProxyRHL proxyRHL = (ProxyRHL) proxySlotHandlers.get(i);
            ProgrammableSlotRecipeHandler.SlotRHL slotRHL = (SlotRHL) slotHandlers.get(i);
            proxyRHL.setBuffer(patternBuffer, slotRHL);
        }
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


        public ProxyRHL(MEPatternBufferProxyPartMachine machine) {
            super(IO.IN);
            circuit = ProxyRecipeHandler.createItemHandler(machine, IO.IN);
            sharedItem = ProxyRecipeHandler.createItemHandler(machine, IO.IN);
            slotItem = ProxyRecipeHandler.createItemHandler(machine, IO.IN);
            sharedFluid = ProxyRecipeHandler.createFluidHandler(machine, IO.IN);
            slotFluid = ProxyRecipeHandler.createFluidHandler(machine, IO.IN);


            addHandlers(circuit, sharedItem, slotItem, sharedFluid, slotFluid);
            this.setGroup(RecipeHandlerGroupDistinctness.BUS_DISTINCT);
        }

        public void setBuffer(MEPatternBufferPartMachine buffer, SlotRHL slotRHL) {
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
