package tech.luckyblock.mcmod.ctnhenergy.api;

import appeng.block.crafting.CraftingUnitBlock;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.lowdragmc.lowdraglib.utils.BlockInfo;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static appeng.core.definitions.AEBlocks.*;

/**
 * @author aaaAlant
 * @date 2025/8/14 18:04
 **/
public class CEPredicates {
    public static final Map<Integer, CraftingUnitBlock> craftingUnitBlocks = new HashMap();
    static {
        craftingUnitBlocks.put(1, CRAFTING_STORAGE_1K.block());
        craftingUnitBlocks.put(4, CRAFTING_STORAGE_4K.block());
        craftingUnitBlocks.put(16, CRAFTING_STORAGE_16K.block());
        craftingUnitBlocks.put(64, CRAFTING_STORAGE_64K.block());
        craftingUnitBlocks.put(256, CRAFTING_STORAGE_256K.block());
    }
    
        
    public static TraceabilityPredicate craftingUnitBlock() {
        return new TraceabilityPredicate(blockWorldState -> {
            var blockState = blockWorldState.getBlockState();
            for (Map.Entry<Integer, CraftingUnitBlock> entry : craftingUnitBlocks.entrySet()) {
                if (blockState.is(entry.getValue())) {
                    int storageKb = entry.getKey();
                    int currentStorageKb = blockWorldState.getMatchContext().getOrPut("StorageKb", 0);
                    currentStorageKb += storageKb;
                    blockWorldState.getMatchContext().set("StorageKb", currentStorageKb);
                    return true;
                }
            }
            return false;
        }, () -> craftingUnitBlocks.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(block-> BlockInfo.fromBlockState(block.getValue().defaultBlockState()))
                .toArray(BlockInfo[]::new));
//                .addTooltips(Component.translatable("ctnh.multiblock.pattern.error.reactor")
    }
}
