package tech.luckyblock.mcmod.ctnhenergy.registry;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.client.util.TooltipHelper;
import com.gregtechceu.gtceu.common.data.machines.GTAEMachines;


import com.gregtechceu.gtceu.common.registry.GTRegistration;
import net.minecraft.network.chat.Component;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch.MEStockingBusPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch.METagStockingBusPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.advanced.MEAdvancedPatternBufferPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.advanced.MEAdvancedPatternBufferProxyPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.energyhatch.MEEnergyPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.energyhatch.MESubstationHatch;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch.MEDualOutputHatchPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.standard.MEPatternBufferPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.standard.MEPatternBufferProxyPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.ultimate.MEUltimatePatternBufferPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.ultimate.MEUltimatePatternBufferProxyPartMachine;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.CN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.EN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.Prefix;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.DUAL_OUTPUT_HATCH_ABILITIES;
import static tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy.REGISTRATE;

@Prefix("machine")
public class CEMachines {

    static {
        REGISTRATE.creativeModeTab(() -> CECreativeModeTabs.ITEM);
    }

    public static MachineDefinition ME_PATTERN_BUFFER;
    public static MachineDefinition ME_PATTERN_BUFFER_PROXY;

    public static MachineDefinition ME_ADVANCED_PATTERN_BUFFER;
    public static MachineDefinition ME_ADVANCED_PATTERN_BUFFER_PROXY;
    public static MachineDefinition DUAL_OUTPUT_HATCH_ME;
    public static MachineDefinition ME_SUBSTATION_HATCH;
    public static MachineDefinition ENERGY_INPUT_HATCH_ME;
    public static MachineDefinition ENERGY_OUTPUT_HATCH_ME;
    public static MachineDefinition ME_ULTIMATE_PATTERN_BUFFER;
    public static MachineDefinition ME_ULTIMATE_PATTERN_BUFFER_PROXY;
    public static MachineDefinition STOCKING_IMPORT_BUS_ME;
    public static MachineDefinition TAG_STOCKING_IMPORT_BUS_ME;

    @CN("具有%s个样板槽位")
    @EN("")
    static Lang slot_number;

    @CN("支持带有§6编程电路§r的样板，每个样板槽位有§6独立§r的虚拟电路槽")
    @EN("")
    static Lang circuit_ability;

    @CN("兼具§6输出功能§r，直接将产物存入ME网络")
    @EN("")
    static Lang output_ability;

    private static void initAdvancedMEPatternBuffer() {
        ME_PATTERN_BUFFER = REGISTRATE
                .machine("me_pattern_buffer", holder -> new MEPatternBufferPartMachine(holder, LuV))
                .cnLangValue("ME样板总成")
                .tier(LuV)
                .rotationState(RotationState.ALL)
                .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS)
                .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch"))
                .langValue("ME Pattern Buffer")
                .tooltips(
                        slot_number.translate(MEPatternBufferPartMachine.MAX_PATTERN_COUNT),
                        Component.translatable("block.gtceu.pattern_buffer.desc.0"),
                        Component.translatable("block.gtceu.pattern_buffer.desc.1"),
                        circuit_ability.translate(),
                        Component.translatable("block.gtceu.pattern_buffer.desc.2"),
                        Component.translatable("gtceu.part_sharing.enabled"))
                .register();

        ME_PATTERN_BUFFER_PROXY = REGISTRATE
                .machine("me_pattern_buffer_proxy", MEPatternBufferProxyPartMachine::new)
                .cnLangValue("ME样板总成镜像")
                .tier(LuV)
                .rotationState(RotationState.ALL)
                .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                        PartAbility.EXPORT_ITEMS)
                .rotationState(RotationState.ALL)
                .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch_proxy"))
                .langValue("ME Pattern Buffer Proxy")
                .tooltips(
                        Component.translatable("block.gtceu.pattern_buffer_proxy.desc.0"),
                        Component.translatable("block.gtceu.pattern_buffer_proxy.desc.1"),
                        Component.translatable("block.gtceu.pattern_buffer_proxy.desc.2"),
                        Component.translatable("gtceu.part_sharing.enabled"))
                .register();

        ME_ADVANCED_PATTERN_BUFFER = REGISTRATE
                .machine("advanced_me_pattern_buffer", holder -> new MEAdvancedPatternBufferPartMachine(holder, GTValues.ZPM))
                .cnLangValue("§5ME高级样板总成§r")
                .langValue("§5ME Advanced Pattern Buffer§r")
                .tier(ZPM)
                .rotationState(RotationState.ALL)
                .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                        PartAbility.EXPORT_ITEMS)
                .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch"))
                .tooltips(
                        slot_number.translate(MEAdvancedPatternBufferPartMachine.MAX_PATTERN_COUNT),
                        circuit_ability.translate(),
                        output_ability.translate(),
                        Component.translatable("gtceu.part_sharing.enabled")
                )
                .register();

        ME_ADVANCED_PATTERN_BUFFER_PROXY = REGISTRATE
                .machine("advanced_me_pattern_buffer_proxy", MEAdvancedPatternBufferProxyPartMachine::new)
                .cnLangValue("ME高级样板总成镜像")
                .langValue("ME Advanced Pattern Buffer Proxy")
                .tier(ZPM)
                .rotationState(RotationState.ALL)
                .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                        PartAbility.EXPORT_ITEMS)
                .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch_proxy"))
                .tooltips(
                        Component.translatable("block.gtceu.pattern_buffer_proxy.desc.0"),
                        Component.translatable("block.gtceu.pattern_buffer_proxy.desc.1"),
                        Component.translatable("gtceu.part_sharing.enabled"))
                .register();
    }
    @CN("§7§o仓室终结者§r")
    @EN("§7§oHatch Terminator§r")
    static Lang hatch_terminator;


    @CN("§6使用ME网络中存储的EU为机器供能§r，可设置电压和电流")
    @EN("")
    static Lang energy_ability;

    private static void initUltimateMEPatternBuffer() {
        ME_ULTIMATE_PATTERN_BUFFER = REGISTRATE
                .machine("me_ultimate_pattern_buffer", holder -> new MEUltimatePatternBufferPartMachine(holder, UV))
                .cnLangValue("§6ME究极样板总成§r")
                .langValue("§6ME Ultimate Pattern Buffer§r")
                .tier(UV)
                .rotationState(RotationState.ALL)
                .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                        PartAbility.EXPORT_ITEMS, PartAbility.INPUT_ENERGY)
                .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch"))
                .tooltips(
                        hatch_terminator.translate(),
                        slot_number.translate(MEAdvancedPatternBufferPartMachine.MAX_PATTERN_COUNT),
                        circuit_ability.translate(),
                        output_ability.translate(),
                        energy_ability.translate(),
                        Component.translatable("gtceu.part_sharing.enabled")
                )
                .register();

        ME_ULTIMATE_PATTERN_BUFFER_PROXY = REGISTRATE
                .machine("me_ultimate_pattern_buffer_proxy", MEUltimatePatternBufferProxyPartMachine::new)
                .cnLangValue("ME究极样板总成镜像")
                .langValue("ME Ultimate Pattern Buffer Proxy")
                .tier(UV)
                .rotationState(RotationState.ALL)
                .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                        PartAbility.EXPORT_ITEMS, PartAbility.INPUT_ENERGY)
                .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch_proxy"))
                .tooltips(
                        Component.translatable("block.gtceu.pattern_buffer_proxy.desc.0"),
                        Component.translatable("block.gtceu.pattern_buffer_proxy.desc.1"),
                        Component.translatable("gtceu.part_sharing.enabled"))
                .register();
    }



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

    @CN({
            "将发电机产出的能量直接存入到ME网络中",
            "§4最大输出功率为 1024A §r"
    })
    @EN({
            "Output Energy into ME Network from generators",
            "§4The max Output Power is 1024A §r"
    })
    static Lang[] me_energy_out;

    @CN("可配置")
    @EN("Configurable")
    static Lang configurable;

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

    private static void initMEEnergyHatch(){
        ENERGY_INPUT_HATCH_ME = REGISTRATE
                .machine("me_energy_input_hatch", holder -> new MEEnergyPartMachine(holder, IO.IN))
                .cnLangValue("ME能源仓")
                .langValue("ME Energy Hatch")
                .tooltips(
                        me_energy_in[0].translate(),
                        me_energy_in[1].translate(),
                        me_energy_in[2].translate()
                )
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

        ENERGY_OUTPUT_HATCH_ME = REGISTRATE
                .machine("me_energy_output_hatch", holder -> new MEEnergyPartMachine(holder, IO.OUT))
                .cnLangValue("ME动力仓")
                .langValue("ME Dynamo Hatch")
                .tooltips(
                        me_energy_out[0].translate(),
                        me_energy_out[1].translate().append(Component.literal(VNF[MAX])),
                        Component.translatable("gtceu.part_sharing.enabled")
                )
                .tier(UV)
                .abilities(PartAbility.OUTPUT_ENERGY)
                .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                .colorOverlayTieredHullModel("me_energy_out", null, null)
                .register();

        ME_SUBSTATION_HATCH = REGISTRATE
                .machine("me_substation_hatch", MESubstationHatch::new)
                .cnLangValue("ME变电仓")
                .langValue("ME Substation Hatch")
                .tooltips(
                        substation_hatch[0].translate(),
                        substation_hatch[1].translate(),
                        substation_hatch[2].translate(),
                        Component.translatable("gtceu.part_sharing.disabled")
                )
                .tier(IV)
                .rotationState(RotationState.ALL)
                .abilities(PartAbility.SUBSTATION_INPUT_ENERGY, PartAbility.SUBSTATION_OUTPUT_ENERGY)
                .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                .overlayTieredHullModel(GTCEu.id("block/machine/part/energy_output_hatch_64a"))
                .register();
    }

    private static void initDualOutputHatchME() {
        DUAL_OUTPUT_HATCH_ME = REGISTRATE
                .machine("me_dual_output_hatch", MEDualOutputHatchPartMachine::new)
                .cnLangValue("ME输出总成")
                .langValue("ME Dual Output Hatch")
                .tier(IV)
                .rotationState(RotationState.ALL)
                .abilities(DUAL_OUTPUT_HATCH_ABILITIES)
                .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_output_bus"))
                .tooltips(
                        Component.translatable("gtceu.machine.me.export.tooltip"),
                        Component.translatable("gtceu.part_sharing.enabled"))
                .register();
    }

    @CN("可使用物品标签过滤可被自动拉取的物品")
    @EN("Allows items to be filtered for Auto-Pull using Item Tags.")
    static Lang tag_filter;

    public static void init() {
        initAdvancedMEPatternBuffer();
        initUltimateMEPatternBuffer();
        initDualOutputHatchME();
        initMEEnergyHatch();

        STOCKING_IMPORT_BUS_ME = REGISTRATE
                .machine("me_stocking_input_bus", MEStockingBusPartMachine::new)
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
                        Component.translatable("gtceu.machine.me.copy_paste.tooltip"),
                        Component.translatable("gtceu.machine.me.stocking_item.tooltip.1"),
                        Component.translatable("gtceu.part_sharing.enabled"))
                .register();

        TAG_STOCKING_IMPORT_BUS_ME = REGISTRATE
                .machine("me_tag_stocking_input_bus", METagStockingBusPartMachine::new)
                .cnLangValue("ME标签库存输入总线")
                .langValue("ME Tag Stocking Input Bus")
                .tier(LuV)
                .rotationState(RotationState.ALL)
                .abilities(PartAbility.IMPORT_ITEMS)
                .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_input_bus"))
                .tooltips(
                        Component.translatable("gtceu.machine.item_bus.import.tooltip"),
                        Component.translatable("gtceu.machine.me.stocking_item.tooltip.0"),
                        Component.translatable("gtceu.machine.me_import_item_hatch.configs.tooltip"),
                        Component.translatable("gtceu.machine.me.copy_paste.tooltip"),
                        Component.translatable("gtceu.machine.me.stocking_item.tooltip.1"),
                        tag_filter.translate(),
                        Component.translatable("gtceu.part_sharing.enabled"))
                .register();

    }
}