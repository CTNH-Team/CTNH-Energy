package tech.luckyblock.mcmod.ctnhenergy.common.machine.handler;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.ingredient.item.ItemIngredient;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.MEPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.utils.GenericStackHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class MEStorageItemHandler extends NotifiableRecipeHandlerTrait<ItemIngredient> {

    @Getter
    public final IO handlerIO;

    @Nullable
    private final GenericStackHandler handler;

    private final Supplier<IGridNode> nodeSupplier;

    public MEStorageItemHandler(MetaMachine machine, IO io, Supplier<IGridNode> nodeSupplier, @Nullable GenericStackHandler handler) {
        super(machine);
        this.handlerIO = io;
        this.nodeSupplier = nodeSupplier;
        this.handler = handler;
    }

    @Override
    public boolean handleRecipe(IO io, GTRecipe recipe, List<ItemIngredient> left, boolean simulate) {
        if (!handlerIO.support(io)) return false;
        if(io == IO.IN && handler == null) return false;

        var service = getStorageService();
        if(service == null) return false;

        IActionSource actionSource = IActionSource.ofMachine(nodeSupplier::get);
        var storage = service.getInventory();
        var cache = service.getCachedInventory();

        for (var it = left.listIterator(); it.hasNext();) {
            var ingredient = it.next();
            int amount;
            if (io == IO.IN) {
                if (simulate) {
                    amount = ingredient.getCount();
                } else {
                    ItemStack stack = ingredient.toStack();
                    if (stack.isEmpty()) {
                        it.remove();
                        continue;
                    }
                    amount = stack.getCount();
                }

                for(int slot = 0; slot < handler.getSlots(); slot++) {
                    GenericStack genericStack = handler.getStackInSlot(slot);
                    if(genericStack != null && genericStack.what() instanceof AEItemKey itemKey
                        && ingredient.test(itemKey.getReadOnlyStack())) {
                        if(simulate) {
                            int cachedAmount = (int) Math.min(genericStack.amount(), cache.get(itemKey));
                            if (cachedAmount == 0) continue;
                            int toExtract = Math.min(amount, cachedAmount);
                            amount -= (int) storage.extract(itemKey, toExtract, Actionable.SIMULATE, actionSource);
                        } else {
                            int toExtract = Math.min(amount, (int) genericStack.amount());
                            amount -= (int) storage.extract(itemKey, toExtract, Actionable.MODULATE, actionSource);
                        }
                        if(amount <= 0) {
                            it.remove();
                            break;
                        }
                    }
                }
            } else {
                ItemStack outputStack;
                if (simulate) {
                    ItemStack[] items = ingredient.getItems();
                    if (items.length == 0 || items[0].isEmpty()) {
                        it.remove();
                        continue;
                    }
                    outputStack = items[0];
                    amount = ingredient.getCount();
                } else {
                    outputStack = ingredient.toStack();
                    if (outputStack.isEmpty()) {
                        it.remove();
                        continue;
                    }
                    amount = outputStack.getCount();
                }
                AEItemKey output = AEItemKey.of(outputStack);
                amount -= (int) storage.insert(output, amount, simulate ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
                if(amount <= 0) {
                    it.remove();
                }
            }
            if (amount > 0) {
                it.set(ingredient.copyWithCount(amount));
            }
        }
        return left.isEmpty();
    }

    protected @Nullable IStorageService getStorageService() {
        var mainNode =  nodeSupplier.get();
        if (mainNode == null || !mainNode.isActive()) return null;
        return mainNode.getGrid().getStorageService();
    }

    @Override
    public @NotNull List<Object> getContents() {
        List<ItemStack> contents = new ArrayList<>();
        if(handlerIO.support(IO.IN) && handler != null) {
            for(int slot = 0; slot < handler.getSlots(); slot++) {
                GenericStack genericStack = handler.getStackInSlot(slot);
                if(genericStack != null && genericStack.what() instanceof AEItemKey itemKey) {
                    contents.add(itemKey.getReadOnlyStack().copyWithCount((int) genericStack.amount()));
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
    public RecipeCapability<ItemIngredient> getCapability() {
        return ItemRecipeCapability.CAP;
    }
}
