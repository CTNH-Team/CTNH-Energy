package tech.luckyblock.mcmod.ctnhenergy.common.machine.handler;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.ingredient.fluid.FluidIngredient;
import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.AbstractMapIngredient;
import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.fluid.FluidStackMapIngredient;
import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.fluid.FluidTagMapIngredient;

import net.minecraftforge.fluids.FluidStack;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.utils.GenericStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MEStorageFluidHandler extends NotifiableRecipeHandlerTrait<FluidIngredient> {

    @Getter
    public final IO handlerIO;

    @Nullable
    private final GenericStackHandler handler;

    private final Supplier<IGridNode> nodeSupplier;

    public MEStorageFluidHandler(MetaMachine machine, IO io, Supplier<IGridNode> nodeSupplier,
                                 @Nullable GenericStackHandler handler) {
        super(machine);
        this.handlerIO = io;
        this.nodeSupplier = nodeSupplier;
        this.handler = handler;
    }

    @Override
    public boolean handleRecipe(IO io, GTRecipe recipe, List<FluidIngredient> left, boolean simulate) {
        if (!handlerIO.support(io)) return false;
        if (io == IO.IN && handler == null) return false;

        var service = getStorageService();
        if (service == null) return false;

        IActionSource actionSource = IActionSource.ofMachine(nodeSupplier::get);
        var storage = service.getInventory();
        var cache = service.getCachedInventory();

        for (var it = left.listIterator(); it.hasNext();) {
            var ingredient = it.next();
            int amount;
            if (io == IO.IN) {
                if (simulate) {
                    amount = ingredient.getAmount();
                } else {
                    FluidStack stack = ingredient.toStack();
                    if (stack.isEmpty()) {
                        it.remove();
                        continue;
                    }
                    amount = stack.getAmount();
                }

                for (int slot = 0; slot < handler.getSlots(); slot++) {
                    GenericStack genericStack = handler.getStackInSlot(slot);
                    if (genericStack != null && genericStack.what() instanceof AEFluidKey fluidKey &&
                            ingredient.test(fluidKey.toStack(1))) {
                        if (simulate) {
                            int cachedAmount = (int) Math.min(genericStack.amount(), cache.get(fluidKey));
                            if (cachedAmount == 0) continue;
                            int toExtract = Math.min(amount, cachedAmount);
                            amount -= (int) storage.extract(fluidKey, toExtract, Actionable.SIMULATE, actionSource);
                        } else {
                            int toExtract = Math.min(amount, (int) genericStack.amount());
                            amount -= (int) storage.extract(fluidKey, toExtract, Actionable.MODULATE, actionSource);
                        }
                        if (amount <= 0) {
                            it.remove();
                            break;
                        }
                    }
                }
            } else {
                FluidStack outputStack;
                if (simulate) {
                    FluidStack[] items = ingredient.getFluids();
                    if (items.length == 0 || items[0].isEmpty()) {
                        it.remove();
                        continue;
                    }
                    outputStack = items[0];
                    amount = ingredient.getAmount();
                } else {
                    outputStack = ingredient.toStack();
                    if (outputStack.isEmpty()) {
                        it.remove();
                        continue;
                    }
                    amount = outputStack.getAmount();
                }
                AEFluidKey output = AEFluidKey.of(outputStack);
                amount -= (int) storage.insert(output, amount, simulate ? Actionable.SIMULATE : Actionable.MODULATE,
                        actionSource);
                if (amount <= 0) {
                    it.remove();
                }
            }
            if (amount > 0) {
                it.set(ingredient.copyWithAmount(amount));
            }
        }
        return left.isEmpty();
    }

    protected @Nullable IStorageService getStorageService() {
        var mainNode = nodeSupplier.get();
        if (mainNode == null || !mainNode.isActive()) return null;
        return mainNode.getGrid().getStorageService();
    }

    @Override
    public @NotNull List<Object> getContents() {
        List<FluidStack> contents = new ArrayList<>();
        if (handlerIO.support(IO.IN) && handler != null) {
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                GenericStack genericStack = handler.getStackInSlot(slot);
                if (genericStack != null && genericStack.what() instanceof AEFluidKey fluidKey) {
                    contents.add(fluidKey.toStack((int) genericStack.amount()));
                }
            }
        }
        return new ArrayList<>(contents);
    }

    @Override
    public double getTotalContentAmount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public RecipeCapability<FluidIngredient> getCapability() {
        return FluidRecipeCapability.CAP;
    }

    @Override
    public @NotNull List<AbstractMapIngredient> getMapIngredients() {
        List<AbstractMapIngredient> ingredients = new ArrayList<>();
        for (var stack : getContents()) {
            FluidStack fluidStack = (FluidStack) stack;
            if (fluidStack.isEmpty()) continue;
            ingredients.addAll(FluidStackMapIngredient.from(fluidStack));
            ingredients.addAll(FluidTagMapIngredient.from(fluidStack));
        }
        return ingredients;
    }
}
