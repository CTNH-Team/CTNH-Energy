package tech.luckyblock.mcmod.ctnhenergy.registry;

import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.CN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.EN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.Prefix;

import static tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy.REGISTRATE;

@Prefix("creativemodetab")
public class CECreativeModeTabs {
    public static void init() {

    }

    @EN("CTNH Energy Items")
    @CN("CTNH Energy 物品")
    static Lang itemGroup;
    public static RegistryEntry<CreativeModeTab> ITEM = REGISTRATE.defaultCreativeTab("item",
                    builder -> builder.displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator("item", REGISTRATE))
                            .title(itemGroup.translate())
                            .build())
            .register();
}
