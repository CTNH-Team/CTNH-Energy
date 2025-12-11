package tech.luckyblock.mcmod.ctnhenergy.registry;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import tech.luckyblock.mcmod.ctnhenergy.common.item.EUCellItem;
import tech.luckyblock.mcmod.ctnhenergy.common.item.EUCellStats;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy.REGISTRATE;

public class CEItems {
    static {
        REGISTRATE.creativeModeTab(() -> CECreativeModeTabs.ITEM);
    }

    public static ItemEntry<EUCellItem>[] EU_CELL = new ItemEntry[TIER_COUNT];

    public static void init(){
        registerEUCell();
    }

    public static void registerEUCell(){
        for(int tier : GTValues.tiersBetween(LV, MAX)){
            EU_CELL[tier] = REGISTRATE
                    .item(VN[tier].toLowerCase() + "_eu_cell", EUCellItem::new)
                    .cnlang(VNF[tier] + "§r ME EU存储元件")
                    .lang(VNF[tier] + "§r ME EU Storage Cell")
                    .model(NonNullBiConsumer.noop())
                    .onRegister(attach(EUCellStats.createCell(tier)))
                    .register();
        }
    }

    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent components) {
        return item -> item.attachComponents(components);
    }

}
