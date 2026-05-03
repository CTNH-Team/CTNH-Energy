package tech.luckyblock.mcmod.ctnhenergy.registry;

import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;

import net.minecraft.world.item.CreativeModeTab;

import com.ctnhlang.CN;
import com.ctnhlang.Category;
import com.ctnhlang.EN;
import com.tterrag.registrate.util.entry.RegistryEntry;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import static tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy.REGISTRATE;

@Category("creativemodetab")
public class CECreativeModeTabs {

    public static void init() {}

    @EN("CTNH Energy Items")
    @CN("CTNH Energy 物品")
    static Lang itemGroup;
    public static RegistryEntry<CreativeModeTab> ITEM = REGISTRATE.defaultCreativeTab("item",
            builder -> builder.displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator("item", REGISTRATE))
                    .title(itemGroup.translate())
                    .build())
            .register();
}
