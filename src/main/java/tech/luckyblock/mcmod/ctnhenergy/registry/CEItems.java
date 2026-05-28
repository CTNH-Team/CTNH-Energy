package tech.luckyblock.mcmod.ctnhenergy.registry;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;

import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ModelFile;

import appeng.api.parts.PartModels;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import tech.luckyblock.mcmod.ctnhenergy.common.item.DynamoCardItem;
import tech.luckyblock.mcmod.ctnhenergy.common.item.EUCellItem;
import tech.luckyblock.mcmod.ctnhenergy.common.item.EUCellStats;
import tech.luckyblock.mcmod.ctnhenergy.common.me.parts.p2p.EUP2PTunnelPart;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy.REGISTRATE;

public class CEItems {

    static {
        REGISTRATE.creativeModeTab(() -> CECreativeModeTabs.ITEM);
    }

    public static ItemEntry<EUCellItem>[] EU_CELL = new ItemEntry[TIER_COUNT];
    static {
        for (int tier : GTValues.tiersBetween(ULV, MAX)) {
            EU_CELL[tier] = REGISTRATE
                    .item(VN[tier].toLowerCase() + "_eu_cell", EUCellItem::new)
                    .cnlang(VNF[tier] + "§r ME EU存储元件")
                    .lang(VNF[tier] + "§r ME EU Storage Cell")
                    .model((ctx, prov) -> prov.generated(ctx::getEntry,
                            prov.modLoc("item/cells/" + VN[tier].toLowerCase())))
                    .onRegister(attach(EUCellStats.createCell(tier)))
                    .register();
        }
    }

    public static ItemEntry<DynamoCardItem> DYNAMO_CARD = REGISTRATE.item("dynamo_card", DynamoCardItem::new)
            .cnlang("动力卡")
            .lang("Dynamo Card")
            .model((ctx, prov) -> {
                // 基础模型
                var baseModel = prov.withExistingParent(ctx.getName(), prov.mcLoc("item/generated"))
                        .texture("layer0", prov.modLoc("item/dynamo_card/" + VN[0].toLowerCase()));

                // 先创建所有变体模型
                for (int v = 0; v < VN.length; v++) {
                    String name = "dynamo_card_" + VN[v].toLowerCase();
                    prov.getBuilder(name)
                            .parent(new ModelFile.UncheckedModelFile("item/generated"))
                            .texture("layer0", "ctnhenergy:item/dynamo_card/" + VN[v].toLowerCase());
                }

                // 然后添加 override
                for (int v = 1; v < VN.length; v++) {
                    baseModel.override()
                            .predicate(prov.modLoc("voltage"), v)
                            .model(new ModelFile.UncheckedModelFile(
                                    prov.modLoc("item/dynamo_card_" + VN[v].toLowerCase())))
                            .end();
                }
            })
            .register();

    public static ItemEntry<Item> EU_CELL_HOUSING = REGISTRATE.item("eu_cell_housing", Item::new)
            .cnlang("EU存储元件外壳")
            .model((ctx, prov) -> prov.generated(ctx::getEntry, prov.modLoc("item/cells/empty")))
            .register();

    public static ItemEntry<PartItem<EUP2PTunnelPart>> EU_P2P = REGISTRATE
            .item("eu_p2p_tunnel", p -> new PartItem<>(p, EUP2PTunnelPart.class, EUP2PTunnelPart::new))
            .cnlang("EU能源P2P通道")
            .lang("EU P2P Tunnel")
            .model(NonNullBiConsumer.noop())
            .register();

    public static void init() {
        PartModels.registerModels(PartModelsHelper.createModels(EUP2PTunnelPart.class));
    }

    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent components) {
        return item -> item.attachComponents(components);
    }
}
