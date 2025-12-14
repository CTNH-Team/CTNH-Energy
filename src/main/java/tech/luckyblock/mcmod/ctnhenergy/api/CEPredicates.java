package tech.luckyblock.mcmod.ctnhenergy.api;

import appeng.block.crafting.CraftingUnitBlock;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.machine.multiblock.IBatteryData;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.common.block.BatteryBlock;

import com.lowdragmc.lowdraglib.utils.BlockInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.PowerSubstationMachine;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static appeng.core.definitions.AEBlocks.*;
import static tech.luckyblock.mcmod.ctnhenergy.common.machine.PowerSubstationMachine.PMC_BATTERY_HEADER;


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

    public static TraceabilityPredicate powerSubstationBatteries() {
        return new TraceabilityPredicate(blockWorldState -> {
            BlockState state = blockWorldState.getBlockState();
            for (Map.Entry<IBatteryData, Supplier<BatteryBlock>> entry : GTCEuAPI.PSS_BATTERIES.entrySet()) {
                if (state.is(entry.getValue().get())) {
                    IBatteryData battery = entry.getKey();
                    // Allow unfilled batteries in the structure, but do not add them to match context.
                    // This lets you use empty batteries as "filler slots" for convenience if desired.
                    if (battery.getTier() != -1 && battery.getCapacity() > 0) {
                        String key = PMC_BATTERY_HEADER + battery.getBatteryName();
                        PowerSubstationMachine.BatteryMatchWrapper wrapper = blockWorldState.getMatchContext().get(key);
                        if (wrapper == null) wrapper = new PowerSubstationMachine.BatteryMatchWrapper(battery);
                        blockWorldState.getMatchContext().set(key, wrapper.increment());
                    }
                    return true;
                }
            }
            return false;
        }, () -> GTCEuAPI.PSS_BATTERIES.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().getTier()))
                .map(entry -> new BlockInfo(entry.getValue().get().defaultBlockState(), null))
                .toArray(BlockInfo[]::new))
                .addTooltips(Component.translatable("gtceu.multiblock.pattern.error.batteries"));
    }
}
