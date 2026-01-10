package tech.luckyblock.mcmod.ctnhenergy.utils;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.parts.IPartHost;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.helpers.InterfaceLogicHost;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.parts.AEBasePart;
import appeng.util.inv.AppEngInternalInventory;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.utils.GTUtil;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import tech.luckyblock.mcmod.ctnhenergy.common.me.MEMachineEUHandler;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.VoltageKey;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public abstract class CEUtil {
    public static IUpgradeableObject getUpgradeable(BlockEntity be, Direction side){
        if(be instanceof InterfaceBlockEntity || be instanceof PatternProviderBlockEntity)

            return be instanceof IUpgradeableObject upgradeable ? upgradeable : null;

        if(be instanceof IPartHost host
                && (host.getPart(side) instanceof InterfaceLogicHost || host.getPart(side) instanceof PatternProviderLogicHost)){
            return host.getPart(side) instanceof IUpgradeableObject upgradeable ? upgradeable : null;
        }

        return null;
    }

    public static boolean isInSameGrid(IEnergyContainer a, IEnergyContainer b){
        return a instanceof MEMachineEUHandler handlerA
                && b instanceof MEMachineEUHandler handlerB
                && handlerA.getNode().getGrid() == handlerB.getNode().getGrid();
    }

    public static List<Direction> getSides(Object host) {
        if (host instanceof BlockEntity) {
            return List.of(GTUtil.DIRECTIONS);
        } else if (host instanceof AEBasePart part) {
            if (part.getSide() == null) {
                return List.of();
            }
            return List.of(part.getSide());
        } else {
            return List.of();
        }
    }

    public static int getGridTier(IGridNode node){
        var storage = node.getGrid().getStorageService().getInventory();
        for(int i = GTValues.MAX; i >= GTValues.ULV; i--){
            if(storage.extract(VoltageKey.of(i), 1, Actionable.SIMULATE, IActionSource.ofMachine(() -> node)) > 0)
                return i;
        }
        return -1;
    }

    public static long clampToLong(BigInteger v) {
        if (v.signum() <= 0) {
            return 0L;
        } else if (v.bitLength() > 63) {
            return Long.MAX_VALUE;
        } else {
            long r = v.longValue();
            return r < 0L ? Long.MAX_VALUE : r;
        }
    }

    public static Ingredient ingredientFromGenericStacks(List<GenericStack> stacks) {
        if (stacks == null || stacks.isEmpty()) {
            return Ingredient.EMPTY;
        }

        List<ItemStack> itemStacks = new ArrayList<>();

        for (GenericStack gs : stacks) {
            if (gs == null) continue;

            if (gs.what() instanceof AEItemKey itemKey) {
                int count = (int) Math.min(Integer.MAX_VALUE, gs.amount());
                if (count <= 0) count = 1;

                ItemStack stack = itemKey.toStack(count);
                itemStacks.add(stack);
            }
        }

        if (itemStacks.isEmpty()) {
            return Ingredient.EMPTY;
        }

        return Ingredient.of(itemStacks.stream());
    }

    public static boolean isCrafting(EmiRecipe recipe){
        return recipe.getCategory().equals(VanillaEmiRecipeCategories.CRAFTING);
    }
}
