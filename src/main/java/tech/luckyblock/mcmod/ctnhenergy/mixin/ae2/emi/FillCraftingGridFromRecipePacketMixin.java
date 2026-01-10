package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.emi;

import appeng.api.networking.crafting.ICraftingService;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.StorageHelper;
import appeng.core.AELog;
import appeng.core.sync.packets.FillCraftingGridFromRecipePacket;
import appeng.helpers.IMenuCraftingPacket;
import appeng.integration.modules.emi.EmiStackHelper;
import appeng.items.storage.ViewCellItem;
import appeng.util.prioritylist.IPartitionList;
import com.google.common.primitives.Ints;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.utils.CEUtil;
import tech.luckyblock.mcmod.ctnhenergy.utils.FakeSizedIntList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Mixin(value = FillCraftingGridFromRecipePacket.class, remap = false)
public abstract class FillCraftingGridFromRecipePacketMixin {
    @Shadow
    private ResourceLocation recipeId;

    @Shadow
    protected abstract List<AEItemKey> findBestMatchingItemStack(Ingredient ingredient, IPartitionList filter, KeyCounter storage);

    @Shadow
    protected abstract ItemStack takeIngredientFromPlayer(IMenuCraftingPacket cct, ServerPlayer player, Ingredient ingredient);

    @Shadow
    private boolean craftMissing;

    @Shadow
    protected abstract Optional<AEItemKey> findCraftableKey(Ingredient ingredient, ICraftingService craftingService);

    @Inject(method = "serverPacketData", at = @At("HEAD"), cancellable = true)
    void handNonCraftingRecipe(ServerPlayer player, CallbackInfo ci) {
        if (recipeId == null) return;

        var emiRecipe = EmiApi.getRecipeManager().getRecipe(recipeId);
        if(emiRecipe == null || CEUtil.isCrafting(emiRecipe)) return;

        var menu = player.containerMenu;
        if (!(menu instanceof IMenuCraftingPacket cct)) return;

        if (!cct.useRealItems()) {
            AELog.warn("Trying to use real items for crafting in a pattern encoding terminal");
            ci.cancel();
        }

        var node = cct.getNetworkNode();
        if (node == null) ci.cancel();

        var grid = node.getGrid();
        var storageService = grid.getStorageService();
        var energy = grid.getEnergyService();

        var storage = storageService.getInventory();
        var cachedStorage = storageService.getCachedInventory();
        var filter = ViewCellItem.createItemFilter(cct.getViewCells());

        var craftingService = grid.getCraftingService();
        var toAutoCraft = new LinkedHashMap<AEItemKey, IntList>();
        boolean touchedGridStorage = false;

        for (var list : EmiStackHelper.ofInputs(emiRecipe)) {
            var ingredient = CEUtil.ingredientFromGenericStacks(list);
            if (ingredient.isEmpty()) continue;

            // === ① 计算所需数量 ===
            int required = ingredient.getItems().length > 0
                    ? ingredient.getItems()[0].getCount()
                    : 1;
            if (required <= 0) required = 1;

            int remaining = required;
            List<ItemStack> toGivePlayer = new ArrayList<>();

            // === ② 优先从 AE 网络提取（可能是多次）===
            var request = findBestMatchingItemStack(ingredient, filter, cachedStorage);
            for (var what : request) {
                if (remaining <= 0) break;

                long extracted = StorageHelper.poweredExtraction(
                        energy,
                        storage,
                        what,
                        remaining,
                        cct.getActionSource()
                );

                if (extracted > 0) {
                    touchedGridStorage = true;
                    remaining -= (int) extracted;
                    toGivePlayer.add(what.toStack(Ints.saturatedCast(extracted)));
                }
            }

            // === ③ 网络不足 → 从玩家背包补 ===
            while (remaining > 0) {
                var taken = takeIngredientFromPlayer(cct, player, ingredient);
                if (taken.isEmpty()) break;

                remaining--;
                toGivePlayer.add(taken);
            }

            for (var stack : toGivePlayer) {
                player.getInventory().add(stack);
            }

            // === ④ 仍然不足 → autocrafting ===
            if (remaining > 0 && craftMissing) {
                int missing = remaining;

                findCraftableKey(ingredient, craftingService).ifPresent(key -> {
                    toAutoCraft.merge(
                            key,
                            FakeSizedIntList.ofSize(missing),
                            (oldList, newList) ->
                                    FakeSizedIntList.ofSize(oldList.size() + newList.size())
                    );
                });
            }
        }

        // === ⑤ AE 原有 autocrafting 逻辑 ===
        if (!toAutoCraft.isEmpty()) {
            if (touchedGridStorage) {
                storageService.invalidateCache();
            }

            var stacks = toAutoCraft.entrySet().stream()
                    .map(e -> new IMenuCraftingPacket.AutoCraftEntry(e.getKey(), e.getValue()))
                    .toList();

            cct.startAutoCrafting(stacks);
        }

        ci.cancel();
    }


}
