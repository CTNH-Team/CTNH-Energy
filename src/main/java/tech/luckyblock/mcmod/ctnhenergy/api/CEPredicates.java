package tech.luckyblock.mcmod.ctnhenergy.api;

import appeng.block.crafting.CraftingUnitBlock;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.machine.multiblock.IBatteryData;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.common.block.BatteryBlock;

import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.wintercogs.ae2omnicells.common.init.OCBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.PowerSubstationMachine;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.CN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.EN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.Prefix;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static appeng.core.definitions.AEBlocks.*;
import static com.wintercogs.ae2omnicells.common.init.OCBlocks.*;

import static tech.luckyblock.mcmod.ctnhenergy.common.machine.PowerSubstationMachine.PMC_BATTERY_HEADER;


/**
 * @author aaaAlant
 * @date 2025/8/14 18:04
 **/
@Prefix("predicates")
public class CEPredicates {
    public static Map<Block, Integer> CRAFTING_UNIT_STORAGE_KB;

    public static void init(){
        CRAFTING_UNIT_STORAGE_KB = Map.of(
                CRAFTING_STORAGE_1K.block(),   1,
                CRAFTING_STORAGE_4K.block(),   4,
                CRAFTING_STORAGE_16K.block(), 16,
                CRAFTING_STORAGE_64K.block(), 64,
                CRAFTING_STORAGE_256K.block(), 256,
                OMNI_CRAFTING_STORAGE_1M_BLOCK.get(), 1024,
                OMNI_CRAFTING_STORAGE_4M_BLOCK.get(), 4096,
                OMNI_CRAFTING_STORAGE_16M_BLOCK.get(), 16384,
                OMNI_CRAFTING_STORAGE_64M_BLOCK.get(), 65536,
                OMNI_CRAFTING_STORAGE_256M_BLOCK.get(), 262144
        );
    }

    @CN("可以使用不同种合成存储器")
    @EN("Crafting storages do not need to be the same")
    static Lang crafting_storage;

    public static TraceabilityPredicate craftingUnitBlock() {
        if(CRAFTING_UNIT_STORAGE_KB == null)
            init();
        return new TraceabilityPredicate(
                blockWorldState -> {
                    BlockState state = blockWorldState.getBlockState();
                    Integer storageKb = CRAFTING_UNIT_STORAGE_KB.get(state.getBlock());

                    if (storageKb == null) {
                        return false;
                    }

                    int current = blockWorldState
                            .getMatchContext()
                            .getOrPut("StorageKb", 0);

                    blockWorldState
                            .getMatchContext()
                            .set("StorageKb", current + storageKb);

                    return true;
                },
                () -> CRAFTING_UNIT_STORAGE_KB.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .map(e -> BlockInfo.fromBlockState(e.getKey().defaultBlockState()))
                        .toArray(BlockInfo[]::new))
                .addTooltips(crafting_storage.translate().withStyle(ChatFormatting.GREEN));
    }

    @CN("可以使用不同种电池")
    @EN("Batteries do not need to be the same tier")
    static Lang batteries;

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
        .addTooltips(batteries.translate().withStyle(ChatFormatting.GREEN));
    }
}
