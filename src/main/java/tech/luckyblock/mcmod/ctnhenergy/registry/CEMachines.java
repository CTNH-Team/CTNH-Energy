package tech.luckyblock.mcmod.ctnhenergy.registry;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.client.util.TooltipHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import com.ctnhlang.*;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.energyhatch.*;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch.*;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.DUAL_INPUT_HATCH_ABILITIES;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.DUAL_OUTPUT_HATCH_ABILITIES;
import static tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy.REGISTRATE;

@Category("machine")
@Suffix("tooltip")
public class CEMachines {

    static {
        REGISTRATE.creativeModeTab(() -> CECreativeModeTabs.ITEM);
    }

    public static MachineDefinition ITEM_IMPORT_BUS_ME = REGISTRATE
            .machine("me_input_bus",
                    be -> new MEInputMachine(be, EV, IO.IN, 16, AEItemKey.class::isInstance))
            .cnLangValue("ME输入总线")
            .langValue("ME Stocking Input Bus")
            .tier(EV)
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.IMPORT_ITEMS)
            .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_input_bus"))
            .tooltips(
                    Component.translatable("gtceu.machine.item_bus.import.tooltip"),
                    Component.translatable("gtceu.machine.me.stocking_item.tooltip.0"),
                    Component.translatable("gtceu.machine.me_import_item_hatch.configs.tooltip"),
                    Component.translatable("gtceu.machine.me.copy_paste.tooltip"),
                    Component.translatable("gtceu.part_sharing.enabled"))
            .register();

    public final static MachineDefinition FLUID_IMPORT_HATCH_ME = REGISTRATE
            .machine("me_input_hatch",
                    be -> new MEInputMachine(be, EV, IO.IN, 16, AEFluidKey.class::isInstance))
            .cnLangValue("ME输入仓")
            .langValue("ME Input Hatch")
            .tier(EV)
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.IMPORT_FLUIDS)
            .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_input_hatch"))
            .tooltips(
                    Component.translatable("gtceu.machine.fluid_hatch.import.tooltip"),
                    Component.translatable("gtceu.machine.me.stocking_fluid.tooltip.0"),
                    Component.translatable("gtceu.machine.me_import_fluid_hatch.configs.tooltip"),
                    Component.translatable("gtceu.machine.me.copy_paste.tooltip"),
                    Component.translatable("gtceu.part_sharing.enabled"))
            .register();

    @EN("Keeps 16 item or fluid types in stock")
    @CN("可标记16种物品或流体")
    static Lang me_dual_input_hatch_configs;

    public final static MachineDefinition DUAL_INPUT_HATCH_ME = REGISTRATE
            .machine("me_dual_input_hatch",
                    be -> new MEInputMachine(be, EV, IO.IN, 16,
                            aeKey -> aeKey instanceof AEItemKey || aeKey instanceof AEFluidKey))
            .cnLangValue("ME输入总成")
            .langValue("ME Dual Input Hatch")
            .tier(EV)
            .rotationState(RotationState.ALL)
            .abilities(DUAL_INPUT_HATCH_ABILITIES)
            .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_input_hatch"))
            .tooltips(
                    Component.translatable("gtceu.machine.dual_hatch.import.tooltip"),
                    me_dual_input_hatch_configs.translate(),
                    Component.translatable("gtceu.machine.me.copy_paste.tooltip"),
                    Component.translatable("gtceu.part_sharing.enabled"))
            .register();

    @CN("ME自动拉取模式将自动标记ME网络中储量最高的的前16项")
    @EN("ME Auto Pull mode automatically marks the 16 most abundant resources in the ME network")
    static Lang auto_pull;

    @CN("可使用标签过滤自动拉取的项目")
    @EN("Allows entries to be filtered for Auto-Pull using Tags")
    static Lang tag_filter;

    public static MachineDefinition ITEM_STOKING_IMPORT_BUS_ME = REGISTRATE
            .machine("me_stoking_input_bus",
                    be -> new MEStokingInputMachine(be, IV, IO.IN, 16, AEItemKey.class::isInstance))
            .cnLangValue("ME库存输入总线")
            .langValue("ME Stocking Input Bus")
            .tier(IV)
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.IMPORT_ITEMS)
            .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_input_bus"))
            .tooltips(
                    Component.translatable("gtceu.machine.item_bus.import.tooltip"),
                    Component.translatable("gtceu.machine.me.stocking_item.tooltip.0"),
                    Component.translatable("gtceu.machine.me_import_item_hatch.configs.tooltip"),
                    auto_pull.translate(),
                    tag_filter.translate().withStyle(ChatFormatting.AQUA),
                    Component.translatable("gtceu.machine.me.copy_paste.tooltip"),
                    Component.translatable("gtceu.part_sharing.enabled"))
            .register();

    public final static MachineDefinition FLUID_STOKING_IMPORT_HATCH_ME = REGISTRATE
            .machine("me_stoking_input_hatch",
                    be -> new MEStokingInputMachine(be, IV, IO.IN, 16, AEFluidKey.class::isInstance))
            .cnLangValue("ME库存输入仓")
            .langValue("ME Stocking Input Hatch")
            .tier(IV)
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.IMPORT_FLUIDS)
            .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_input_hatch"))
            .tooltips(
                    Component.translatable("gtceu.machine.fluid_hatch.import.tooltip"),
                    Component.translatable("gtceu.machine.me.stocking_fluid.tooltip.0"),
                    Component.translatable("gtceu.machine.me_import_fluid_hatch.configs.tooltip"),
                    auto_pull.translate(),
                    tag_filter.translate().withStyle(ChatFormatting.AQUA),
                    Component.translatable("gtceu.machine.me.copy_paste.tooltip"),
                    Component.translatable("gtceu.part_sharing.enabled"))
            .register();

    public final static MachineDefinition DUAL_STOKING_INPUT_HATCH_ME = REGISTRATE
            .machine("me_stoking_dual_input_hatch",
                    be -> new MEStokingInputMachine(be, IV, IO.IN, 16,
                            aeKey -> aeKey instanceof AEItemKey || aeKey instanceof AEFluidKey))
            .cnLangValue("ME库存输入总成")
            .langValue("ME Dual Stocking Input Hatch")
            .tier(IV)
            .rotationState(RotationState.ALL)
            .abilities(DUAL_INPUT_HATCH_ABILITIES)
            .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_input_hatch"))
            .tooltips(
                    Component.translatable("gtceu.machine.dual_hatch.import.tooltip"),
                    me_dual_input_hatch_configs.translate(),
                    auto_pull.translate(),
                    tag_filter.translate().withStyle(ChatFormatting.AQUA),
                    Component.translatable("gtceu.machine.me.copy_paste.tooltip"),
                    Component.translatable("gtceu.part_sharing.enabled"))
            .register();

    public static MachineDefinition ITEM_EXPORT_BUS_ME = REGISTRATE
            .machine("me_output_bus", be -> new MEOutputMachine(be, EV, IO.OUT, true, false))
            .cnLangValue("ME输出总线")
            .langValue("ME Output Bus")
            .tier(EV)
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.EXPORT_ITEMS)
            .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_output_bus"))
            .tooltips(
                    Component.translatable("gtceu.machine.item_bus.export.tooltip"),
                    Component.translatable("gtceu.machine.me.item_export.tooltip"),
                    Component.translatable("gtceu.part_sharing.enabled"))
            .register();

    public final static MachineDefinition FLUID_EXPORT_HATCH_ME = REGISTRATE
            .machine("me_output_hatch", be -> new MEOutputMachine(be, EV, IO.OUT, false, true))
            .cnLangValue("ME输出仓")
            .langValue("ME Output Hatch")
            .tier(EV)
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.EXPORT_FLUIDS)
            .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_output_hatch"))
            .tooltips(
                    Component.translatable("gtceu.machine.fluid_hatch.export.tooltip"),
                    Component.translatable("gtceu.machine.me.fluid_export.tooltip"),
                    Component.translatable("gtceu.part_sharing.enabled"))
            .register();

    public static MachineDefinition DUAL_OUTPUT_HATCH_ME = REGISTRATE
            .machine("me_dual_output_hatch", be -> new MEOutputMachine(be, EV, IO.OUT, true, true))
            .cnLangValue("ME输出总成")
            .langValue("ME Dual Output Hatch")
            .tier(EV)
            .rotationState(RotationState.ALL)
            .abilities(DUAL_OUTPUT_HATCH_ABILITIES)
            .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_output_bus"))
            .tooltips(
                    Component.translatable("gtceu.machine.dual_hatch.export.tooltip"),
                    Component.translatable("gtceu.machine.me.export.tooltip"),
                    Component.translatable("gtceu.part_sharing.enabled"))
            .register();

    @CN({
            "直接使用ME网络中存储的EU为机器供能",
            "§a可以通过UI设置电压、电流§r",
            "§4输入电压等级不能超过ME网络的电压等级,输入电流不能超过64A§r",
            "§a输入电压：§r",
            "§e输入电流：§r"
    })
    @EN({
            "Directly uses the stored EU in ME network to supply energy for Multiblocks",
            "§Input Voltage and Amperage can be set inside UI§r",
            "§4Input Voltage Tier must not exceed ME Network Voltage Tier and the Input Amperage is capped at  64A§r",
            "§aVoltage IN: §r",
            "§eAmperage IN: §r"
    })
    static Lang[] me_energy_in;

    @CN("可配置")
    @EN("Configurable")
    static Lang configurable;
    public static MachineDefinition ENERGY_INPUT_HATCH_ME = REGISTRATE
            .machine("me_energy_input_hatch", holder -> new MEEnergyPartMachine(holder, IO.IN))
            .cnLangValue("ME能源仓")
            .langValue("ME Energy Hatch")
            .tooltips(
                    me_energy_in[0].translate(),
                    me_energy_in[1].translate(),
                    me_energy_in[2].translate())
            .tooltipBuilder((is, components) -> {
                components.add(me_energy_in[3].translate().append(
                        configurable.translate().withStyle(TooltipHelper.RAINBOW_HSL_SLOW)));
                components.add(me_energy_in[4].translate().append(
                        configurable.translate().withStyle(TooltipHelper.RAINBOW_HSL_SLOW)));
                components.add(Component.translatable("gtceu.part_sharing.enabled"));
            })
            .tier(UV)
            .abilities(PartAbility.INPUT_ENERGY)
            .modelProperty(GTMachineModelProperties.IS_FORMED, false)
            .colorOverlayTieredHullModel("me_energy_in", null, null)
            .register();

    @CN({
            "将发电机产出的能量直接存入到ME网络中",
            "§4最大输出功率为 1024A §r"
    })
    @EN({
            "Output Energy into ME Network from generators",
            "§4The max Output Power is 1024A §r"
    })
    static Lang[] me_energy_out;
    public static MachineDefinition ENERGY_OUTPUT_HATCH_ME = REGISTRATE
            .machine("me_energy_output_hatch", holder -> new MEEnergyPartMachine(holder, IO.OUT))
            .cnLangValue("ME动力仓")
            .langValue("ME Dynamo Hatch")
            .tooltips(
                    me_energy_out[0].translate(),
                    me_energy_out[1].translate().append(Component.literal(VNF[MAX])),
                    Component.translatable("gtceu.part_sharing.enabled"))
            .tier(UV)
            .abilities(PartAbility.OUTPUT_ENERGY)
            .modelProperty(GTMachineModelProperties.IS_FORMED, false)
            .colorOverlayTieredHullModel("me_energy_out", null, null)
            .register();

    @CN({
            "将蓄能变电站接入ME网络",
            "允许通过ME网络为蓄能变电站输入或输出能量",
            "可设置优先级"
    })
    @EN({
            "Connects the Power Substation to the ME Network",
            "Allows energy stored in the Power Substation to be input or output via the ME Network",
            "Supports priority configuration"
    })
    static Lang[] substation_hatch;
    public static MachineDefinition ME_SUBSTATION_HATCH = REGISTRATE
            .machine("me_substation_hatch", MESubstationHatch::new)
            .cnLangValue("ME变电仓")
            .langValue("ME Substation Hatch")
            .tooltips(
                    substation_hatch[0].translate(),
                    substation_hatch[1].translate(),
                    substation_hatch[2].translate(),
                    Component.translatable("gtceu.part_sharing.disabled"))
            .tier(IV)
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.SUBSTATION_INPUT_ENERGY, PartAbility.SUBSTATION_OUTPUT_ENERGY)
            .modelProperty(GTMachineModelProperties.IS_FORMED, false)
            .colorOverlayTieredHullModel("me_substation", null, null)
            .register();

    public static MachineDefinition ME_PATTERN_BUFFER;
    public static MachineDefinition ME_PATTERN_BUFFER_PROXY;

    public static MachineDefinition ME_ADVANCED_PATTERN_BUFFER;
    public static MachineDefinition ME_ADVANCED_PATTERN_BUFFER_PROXY;

    public static MachineDefinition ME_ULTIMATE_PATTERN_BUFFER;
    public static MachineDefinition ME_ULTIMATE_PATTERN_BUFFER_PROXY;

    @CN("具有%s个样板槽位")
    @EN("")
    static Lang slot_number;

    @CN("支持带有§6编程电路§r的样板，每个样板槽位有§6独立§r的虚拟电路槽")
    @EN("")
    static Lang circuit_ability;

    @CN("兼具§6输出功能§r，直接将产物存入ME网络")
    @EN("")
    static Lang output_ability;

    public static void init() {}

    // private static void initAdvancedMEPatternBuffer() {
    // ME_PATTERN_BUFFER = REGISTRATE
    // .machine("me_pattern_buffer", holder -> new MEPatternBufferPartMachine(holder, LuV))
    // .cnLangValue("ME样板总成")
    // .tier(LuV)
    // .rotationState(RotationState.ALL)
    // .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS)
    // .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch"))
    // .langValue("ME Pattern Buffer")
    // .tooltips(
    // slot_number.translate(MEPatternBufferPartMachine.MAX_PATTERN_COUNT),
    // Component.translatable("block.gtceu.pattern_buffer.desc.0"),
    // Component.translatable("block.gtceu.pattern_buffer.desc.1"),
    // circuit_ability.translate(),
    // Component.translatable("block.gtceu.pattern_buffer.desc.2"),
    // Component.translatable("gtceu.part_sharing.enabled"))
    // .register();
    //
    // ME_PATTERN_BUFFER_PROXY = REGISTRATE
    // .machine("me_pattern_buffer_proxy", MEPatternBufferProxyPartMachine::new)
    // .cnLangValue("ME样板总成镜像")
    // .tier(LuV)
    // .rotationState(RotationState.ALL)
    // .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
    // PartAbility.EXPORT_ITEMS)
    // .rotationState(RotationState.ALL)
    // .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch_proxy"))
    // .langValue("ME Pattern Buffer Proxy")
    // .tooltips(
    // Component.translatable("block.gtceu.pattern_buffer_proxy.desc.0"),
    // Component.translatable("block.gtceu.pattern_buffer_proxy.desc.1"),
    // Component.translatable("block.gtceu.pattern_buffer_proxy.desc.2"),
    // Component.translatable("gtceu.part_sharing.enabled"))
    // .register();
    //
    // ME_ADVANCED_PATTERN_BUFFER = REGISTRATE
    // .machine("advanced_me_pattern_buffer",
    // holder -> new MEAdvancedPatternBufferPartMachine(holder, GTValues.ZPM))
    // .cnLangValue("§5ME高级样板总成§r")
    // .langValue("§5ME Advanced Pattern Buffer§r")
    // .tier(ZPM)
    // .rotationState(RotationState.ALL)
    // .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
    // PartAbility.EXPORT_ITEMS)
    // .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch"))
    // .tooltips(
    // slot_number.translate(MEAdvancedPatternBufferPartMachine.MAX_PATTERN_COUNT),
    // circuit_ability.translate(),
    // output_ability.translate(),
    // Component.translatable("gtceu.part_sharing.enabled"))
    // .register();
    //
    // ME_ADVANCED_PATTERN_BUFFER_PROXY = REGISTRATE
    // .machine("advanced_me_pattern_buffer_proxy", MEAdvancedPatternBufferProxyPartMachine::new)
    // .cnLangValue("ME高级样板总成镜像")
    // .langValue("ME Advanced Pattern Buffer Proxy")
    // .tier(ZPM)
    // .rotationState(RotationState.ALL)
    // .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
    // PartAbility.EXPORT_ITEMS)
    // .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch_proxy"))
    // .tooltips(
    // Component.translatable("block.gtceu.pattern_buffer_proxy.desc.0"),
    // Component.translatable("block.gtceu.pattern_buffer_proxy.desc.1"),
    // Component.translatable("gtceu.part_sharing.enabled"))
    // .register();
    // }
    //
    // @CN("§7§o仓室终结者§r")
    // @EN("§7§oHatch Terminator§r")
    // static Lang hatch_terminator;
    //
    // @CN("§6使用ME网络中存储的EU为机器供能§r，可设置电压和电流")
    // @EN("")
    // static Lang energy_ability;
    //
    // private static void initUltimateMEPatternBuffer() {
    // ME_ULTIMATE_PATTERN_BUFFER = REGISTRATE
    // .machine("me_ultimate_pattern_buffer", holder -> new MEUltimatePatternBufferPartMachine(holder, UV))
    // .cnLangValue("§6ME究极样板总成§r")
    // .langValue("§6ME Ultimate Pattern Buffer§r")
    // .tier(UV)
    // .rotationState(RotationState.ALL)
    // .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
    // PartAbility.EXPORT_ITEMS, PartAbility.INPUT_ENERGY)
    // .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch"))
    // .tooltips(
    // hatch_terminator.translate(),
    // slot_number.translate(MEAdvancedPatternBufferPartMachine.MAX_PATTERN_COUNT),
    // circuit_ability.translate(),
    // output_ability.translate(),
    // energy_ability.translate(),
    // Component.translatable("gtceu.part_sharing.enabled"))
    // .register();
    //
    // ME_ULTIMATE_PATTERN_BUFFER_PROXY = REGISTRATE
    // .machine("me_ultimate_pattern_buffer_proxy", MEUltimatePatternBufferProxyPartMachine::new)
    // .cnLangValue("ME究极样板总成镜像")
    // .langValue("ME Ultimate Pattern Buffer Proxy")
    // .tier(UV)
    // .rotationState(RotationState.ALL)
    // .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
    // PartAbility.EXPORT_ITEMS, PartAbility.INPUT_ENERGY)
    // .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch_proxy"))
    // .tooltips(
    // Component.translatable("block.gtceu.pattern_buffer_proxy.desc.0"),
    // Component.translatable("block.gtceu.pattern_buffer_proxy.desc.1"),
    // Component.translatable("gtceu.part_sharing.enabled"))
    // .register();
    // }
}
