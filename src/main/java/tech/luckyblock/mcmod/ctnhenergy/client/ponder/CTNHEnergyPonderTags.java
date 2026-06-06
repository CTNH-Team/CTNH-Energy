package tech.luckyblock.mcmod.ctnhenergy.client.ponder;

import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

import appeng.api.ids.AEBlockIds;
import appeng.api.ids.AEPartIds;
import appeng.core.definitions.AEItems;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.vixhentx.mcmod.ctnhlib.client.ponder.CTNHPonderTagHelper;

import static tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy.REGISTRATE;

public final class CTNHEnergyPonderTags {

    public static final ResourceLocation AEOriginal = ResourceLocation.tryBuild(CTNHEnergy.MODID, "ae_original");

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        CTNHPonderTagHelper.registerTag(REGISTRATE, helper, AEOriginal,
                "AE Original", "AE原版",
                "Ponders on original Applied Energistics 2 blocks and parts", "AE2原版方块与部件思索")
                .addToIndex()
                .item(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem(), true, false)
                .register();

        var aeTag = helper.addToTag(AEOriginal);
        // 破坏面板
        aeTag.add(AEPartIds.ANNIHILATION_PLANE);
        // 赛特斯石英母岩
        aeTag.add(AEBlockIds.FLAWLESS_BUDDING_QUARTZ);
        aeTag.add(AEBlockIds.FLAWED_BUDDING_QUARTZ);
        aeTag.add(AEBlockIds.CHIPPED_BUDDING_QUARTZ);
        aeTag.add(AEBlockIds.DAMAGED_BUDDING_QUARTZ);
        aeTag.add(AEItems.CERTUS_QUARTZ_CRYSTAL.id());
        // 线缆 (所有颜色)
        for (var rl : AEPartIds.CABLE_GLASS.values()) aeTag.add(rl);
        for (var rl : AEPartIds.CABLE_COVERED.values()) aeTag.add(rl);
        for (var rl : AEPartIds.CABLE_DENSE_COVERED.values()) aeTag.add(rl);
        for (var rl : AEPartIds.CABLE_SMART.values()) aeTag.add(rl);
        for (var rl : AEPartIds.CABLE_DENSE_SMART.values()) aeTag.add(rl);
        // ME控制器
        aeTag.add(AEBlockIds.CONTROLLER);
        // 合成处理器
        aeTag.add(AEBlockIds.CRAFTING_UNIT);
        // 成型面板
        aeTag.add(AEPartIds.FORMATION_PLANE);
        // 输入/输出总线
        aeTag.add(AEPartIds.IMPORT_BUS);
        aeTag.add(AEPartIds.EXPORT_BUS);
        // ME接口
        aeTag.add(AEBlockIds.INTERFACE);
        aeTag.add(AEPartIds.INTERFACE);
        // IO端口
        aeTag.add(AEBlockIds.IO_PORT);
        // 分子装配室
        aeTag.add(AEBlockIds.MOLECULAR_ASSEMBLER);
        // 样板供应器
        aeTag.add(AEBlockIds.PATTERN_PROVIDER);
        aeTag.add(AEPartIds.PATTERN_PROVIDER);
        // 量子网桥
        aeTag.add(AEBlockIds.QUANTUM_RING);
        aeTag.add(AEBlockIds.QUANTUM_LINK);
        // 存储总线
        aeTag.add(AEPartIds.STORAGE_BUS);
    }

    private CTNHEnergyPonderTags() {}
}
