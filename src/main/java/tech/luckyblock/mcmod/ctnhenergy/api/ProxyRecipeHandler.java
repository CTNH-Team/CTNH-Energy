package tech.luckyblock.mcmod.ctnhenergy.api;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.*;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.IRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.ingredient.EnergyStack;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.lowdragmc.lowdraglib.syncdata.ISubscription;
import lombok.Getter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@Getter
public class ProxyRecipeHandler<T> extends NotifiableRecipeHandlerTrait<T> {

    private IRecipeHandlerTrait<T> proxy;
    private ISubscription proxySub;

    private final IO handlerIO;
    private final RecipeCapability<T> capability;
    private final boolean isDistinct = true;

    public ProxyRecipeHandler(
            MetaMachine machine,
            IO io,
            RecipeCapability<T> capability
    ) {
        super(machine);
        this.handlerIO = io;
        this.capability = capability;
    }

    public void setProxy(@Nullable IRecipeHandlerTrait<T> proxy) {
        this.proxy = proxy;

        if (proxySub != null) {
            proxySub.unsubscribe();
            proxySub = null;
        }

        if (proxy != null) {
            proxySub = proxy.addChangedListener(this::notifyListeners);
        }
    }

    @Override
    public List<T> handleRecipeInner(
            IO io, GTRecipe recipe, List<T> left, boolean simulate) {

        return proxy == null
                ? left
                : proxy.handleRecipeInner(io, recipe, left, simulate);
    }

    @Override
    public int getSize() {
        return proxy == null ? 0 : proxy.getSize();
    }

    @Override
    public @NotNull List<Object> getContents() {
        return proxy == null ? Collections.emptyList() : proxy.getContents();
    }

    @Override
    public double getTotalContentAmount() {
        return proxy == null ? 0 : proxy.getTotalContentAmount();
    }

    public int getPriority() {
        return proxy == null ? IFilteredHandler.LOW : proxy.getPriority();
    }

    public static ProxyRecipeHandler<Ingredient> createItemHandler(MetaMachine machine, IO io){
        return new ProxyRecipeHandler<>(machine, io, ItemRecipeCapability.CAP);
    }

    public static ProxyRecipeHandler<FluidIngredient> createFluidHandler(MetaMachine machine, IO io){
        return new ProxyRecipeHandler<>(machine, io, FluidRecipeCapability.CAP);
    }

    public static ProxyEnergyRecipeHandler createEnergyHandler(MetaMachine machine, IO io){
        return new ProxyEnergyRecipeHandler(machine,  io);
    }

    public static class ProxyEnergyRecipeHandler extends ProxyRecipeHandler<EnergyStack>
            implements IEnergyContainer {

        protected IEnergyContainer energyProxy;

        public ProxyEnergyRecipeHandler(MetaMachine machine, IO io) {
            super(machine, io, EURecipeCapability.CAP);
        }

        protected IEnergyContainer delegate() {
            return energyProxy != null ? energyProxy : IEnergyContainer.DEFAULT;
        }

        @Override
        public void setProxy(@org.jetbrains.annotations.Nullable IRecipeHandlerTrait<EnergyStack> proxy) {
            super.setProxy(proxy);
            if(proxy instanceof IEnergyContainer container)
                energyProxy = container;
            else if (proxy == null)
                energyProxy = null;
        }

        @Override
        public long acceptEnergyFromNetwork(Direction side, long voltage, long amperage) {
            return delegate().acceptEnergyFromNetwork(side, voltage, amperage);
        }

        @Override
        public boolean inputsEnergy(Direction side) {
            return delegate().inputsEnergy(side);
        }

        @Override
        public boolean outputsEnergy(Direction side) {
            return delegate().outputsEnergy(side);
        }

        @Override
        public long changeEnergy(long differenceAmount) {
            return delegate().changeEnergy(differenceAmount);
        }

        @Override
        public long addEnergy(long energyToAdd) {
            return delegate().addEnergy(energyToAdd);
        }

        @Override
        public long removeEnergy(long energyToRemove) {
            return delegate().removeEnergy(energyToRemove);
        }

        @Override
        public long getEnergyStored() {
            return delegate().getEnergyStored();
        }

        @Override
        public long getEnergyCapacity() {
            return delegate().getEnergyCapacity();
        }

        @Override
        public long getInputAmperage() {
            return delegate().getInputAmperage();
        }

        @Override
        public long getInputVoltage() {
            return delegate().getInputVoltage();
        }

        @Override
        public long getOutputAmperage() {
            return delegate().getOutputAmperage();
        }

        @Override
        public long getOutputVoltage() {
            return delegate().getOutputVoltage();
        }

        @Override
        public EnergyInfo getEnergyInfo() {
            return delegate().getEnergyInfo();
        }

        @Override
        public boolean supportsBigIntEnergyValues() {
            return delegate().supportsBigIntEnergyValues();
        }

        @Override
        public long getInputPerSec() {
            return delegate().getInputPerSec();
        }

        @Override
        public long getOutputPerSec() {
            return delegate().getOutputPerSec();
        }
    }

}

