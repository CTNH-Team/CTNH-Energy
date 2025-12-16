package tech.luckyblock.mcmod.ctnhenergy.registry;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.common.data.machines.GTAEMachines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.advancedpatternbuffer.AdvancedMEPatternBufferPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.advancedpatternbuffer.AdvancedMEPatternBufferProxyPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.energyhatch.MEEnergyPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.energyhatch.MESubstationHatch;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch.MEDualOutputHatchPartMachine;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.CN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.EN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.Prefix;

import java.util.List;
import java.util.function.BiConsumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.DUAL_OUTPUT_HATCH_ABILITIES;
import static tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy.REGISTRATE;

@Prefix("machine")
public class CEMachines {

    static {
        REGISTRATE.creativeModeTab(() -> CECreativeModeTabs.ITEM);
    }

    public static MachineDefinition ADVANCED_ME_PATTERN_BUFFER;
    public static MachineDefinition ADVANCED_ME_PATTERN_BUFFER_PROXY;
    public static MachineDefinition DUAL_OUTPUT_HATCH_ME;
    public static MachineDefinition ME_SUBSTATION_HATCH;
    public static MachineDefinition ENERGY_INPUT_HATCH_ME;
    public static MachineDefinition ENERGY_OUTPUT_HATCH_ME;

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
        ADVANCED_ME_PATTERN_BUFFER = REGISTRATE
                .machine("advanced_me_pattern_buffer", AdvancedMEPatternBufferPartMachine::new)
                .cnLangValue("§5高级ME样板总成§r")
                .langValue("§5Advanced ME Pattern Buffer§r")
                .tier(ZPM)
                .rotationState(RotationState.ALL)
                .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                        PartAbility.EXPORT_ITEMS)
                .rotationState(RotationState.ALL)
                .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch"))
                .tooltips(
                        slot_number.translate(54),
                        circuit_ability.translate(),
                        output_ability.translate(),
                        Component.translatable("gtceu.part_sharing.enabled")
                )
                .register();
    }

    private static void initAdvancedMEPatternBufferProxy() {
        ADVANCED_ME_PATTERN_BUFFER_PROXY = REGISTRATE
                .machine("advanced_me_pattern_buffer_proxy", AdvancedMEPatternBufferProxyPartMachine::new)
                .cnLangValue("高级ME样板总成镜像")
                .langValue("Advanced ME Pattern Buffer Proxy")
                .tier(ZPM)
                .rotationState(RotationState.ALL)
                .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                        PartAbility.EXPORT_ITEMS)
                .rotationState(RotationState.ALL)
                .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch_proxy"))
                .tooltips(
                        Component.translatable("block.gtceu.pattern_buffer_proxy.desc.0"),
                        Component.translatable("block.gtceu.pattern_buffer_proxy.desc.1"),
                        Component.translatable("gtceu.part_sharing.enabled"))
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

    public static void init() {
        initAdvancedMEPatternBuffer();
        initAdvancedMEPatternBufferProxy();
        initDualOutputHatchME();

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

        ENERGY_INPUT_HATCH_ME = REGISTRATE
                .machine("me_energy_input_hatch", holder -> new MEEnergyPartMachine(holder, IO.IN))
                .cnLangValue("ME能源仓")
                .langValue("ME Energy Hatch")
                .tooltips(

                )
                .tier(UV)
                .abilities(PartAbility.INPUT_ENERGY)
                .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                .overlayTieredHullModel(GTCEu.id("block/machine/part/energy_input_hatch_16a"))
                .register();

        ENERGY_OUTPUT_HATCH_ME = REGISTRATE
                .machine("me_energy_output_hatch", holder -> new MEEnergyPartMachine(holder, IO.OUT))
                .cnLangValue("ME动力仓")
                .langValue("ME Dynamo Hatch")
                .tier(UV)
                .abilities(PartAbility.OUTPUT_ENERGY)
                .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                .overlayTieredHullModel(GTCEu.id("block/machine/part/energy_output_hatch_16a"))
                .register();

        GTAEMachines.STOCKING_IMPORT_BUS_ME.setTier(IV);
        GTAEMachines.STOCKING_IMPORT_HATCH_ME.setTier(IV);
        BiConsumer<ItemStack, List<Component>> builder = (i, l)-> l.add(slot_number.translate(27));
        GTAEMachines.ME_PATTERN_BUFFER.setTooltipBuilder(builder.andThen(GTAEMachines.ME_PATTERN_BUFFER.getTooltipBuilder()));
    }
}