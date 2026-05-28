// 代码来源于Create Delights，原作者为SSW，已获得授权
package tech.luckyblock.mcmod.ctnhenergy.client.ponder;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

import appeng.api.ids.AEBlockIds;
import appeng.api.ids.AEPartIds;
import appeng.core.definitions.AEItems;
import tech.luckyblock.mcmod.ctnhenergy.client.ponder.ae2.*;

import java.util.ArrayList;

import static tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderTags.AEOriginal;

public final class CTNHEnergyPonderScenes {

    private CTNHEnergyPonderScenes() {}

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        // 破坏面板
        helper.forComponents(AEPartIds.ANNIHILATION_PLANE)
                .addStoryBoard("annihilation_plane/common", AnnihilationPlane::annihilationPlane, AEOriginal)
                .addStoryBoard("annihilation_plane/filter", AnnihilationPlane::filter, AEOriginal);

        // 赛特斯石英母岩
        helper.forComponents(
                AEBlockIds.FLAWLESS_BUDDING_QUARTZ,
                AEBlockIds.FLAWED_BUDDING_QUARTZ,
                AEBlockIds.CHIPPED_BUDDING_QUARTZ,
                AEBlockIds.DAMAGED_BUDDING_QUARTZ,
                AEItems.CERTUS_QUARTZ_CRYSTAL.id())
                .addStoryBoard("budding_quartz/budding_quart", BuddingQuartz::obtain, AEOriginal)
                .addStoryBoard("budding_quartz/budding_quart", BuddingQuartz::grow, AEOriginal)
                .addStoryBoard("budding_quartz/budding_quart", BuddingQuartz::repair, AEOriginal);

        // 线缆
        var cables = new ArrayList<ResourceLocation>();
        cables.addAll(AEPartIds.CABLE_GLASS.values());
        cables.addAll(AEPartIds.CABLE_COVERED.values());
        cables.addAll(AEPartIds.CABLE_DENSE_COVERED.values());
        cables.addAll(AEPartIds.CABLE_SMART.values());
        cables.addAll(AEPartIds.CABLE_DENSE_SMART.values());
        helper.forComponents(cables.toArray(ResourceLocation[]::new))
                .addStoryBoard("cable/cable", Cable::cable, AEOriginal)
                .addStoryBoard("cable/small_cable", Cable::smallCable, AEOriginal)
                .addStoryBoard("cable/dense_cable", Cable::denseCable, AEOriginal);

        // ME控制器
        helper.forComponents(AEBlockIds.CONTROLLER)
                .addStoryBoard("controller/controller", Controller::controller, AEOriginal);

        // 合成处理器
        helper.forComponents(AEBlockIds.CRAFTING_UNIT)
                .addStoryBoard("crafting_process_unit/unit", CraftingProcessUnit::unit, AEOriginal);

        // 自动合成系统
        helper.forComponents(
                AEBlockIds.PATTERN_PROVIDER,
                AEBlockIds.CRAFTING_UNIT,
                AEBlockIds.MOLECULAR_ASSEMBLER)
                .addStoryBoard("crafting_system/system", CraftingSystem::system, AEOriginal);

        // 成型面板
        helper.forComponents(AEPartIds.FORMATION_PLANE)
                .addStoryBoard("formation_plane/common", FormationPlane::common, AEOriginal);

        // 输入/输出总线
        helper.forComponents(
                AEPartIds.IMPORT_BUS,
                AEPartIds.EXPORT_BUS)
                .addStoryBoard("import_export_bus/common", ImportExportBus::common, AEOriginal)
                .addStoryBoard("import_export_bus/transport", ImportExportBus::transport, AEOriginal);

        // ME接口
        helper.forComponents(
                AEBlockIds.INTERFACE,
                AEPartIds.INTERFACE)
                .addStoryBoard("interface/common", Interface::common, AEOriginal);

        // IO端口
        helper.forComponents(AEBlockIds.IO_PORT)
                .addStoryBoard("io_port/io_port", IOPort::ioPort, AEOriginal)
                .addStoryBoard("io_port/output", IOPort::output, AEOriginal);

        // 分子装配室
        helper.forComponents(AEBlockIds.MOLECULAR_ASSEMBLER)
                .addStoryBoard("molecular_assembler/common", MolecularAssembler::common, AEOriginal);

        // 样板供应器
        helper.forComponents(
                AEBlockIds.PATTERN_PROVIDER,
                AEPartIds.PATTERN_PROVIDER)
                .addStoryBoard("pattern_provider/common", PatternProvider::common, AEOriginal)
                .addStoryBoard("pattern_provider/parallel", PatternProvider::parallel, AEOriginal)
                .addStoryBoard("pattern_provider/interaction", PatternProvider::interaction, AEOriginal);

        // 量子网桥
        helper.forComponents(
                AEBlockIds.QUANTUM_RING,
                AEBlockIds.QUANTUM_LINK)
                .addStoryBoard("quantum_network_bridge/bridge", QuantumNetworkBridge::bridge, AEOriginal);

        // 存储总线
        helper.forComponents(AEPartIds.STORAGE_BUS)
                .addStoryBoard("storage_bus/common", StorageBus::common, AEOriginal)
                .addStoryBoard("storage_bus/interface", StorageBus::interfaceInteraction, AEOriginal);
    }
}
