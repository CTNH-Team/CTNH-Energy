package tech.luckyblock.mcmod.ctnhenergy.registry;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import net.minecraft.network.chat.Component;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.advancedpatternbuffer.AdvancedMEPatternBufferPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.advancedpatternbuffer.AdvancedMEPatternBufferProxyPartMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.mehatch.MEDualOutputHatchPartMachine;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.DUAL_OUTPUT_HATCH_ABILITIES;
import static tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy.REGISTRATE;

public class CEMachines {

    static {
        REGISTRATE.creativeModeTab(() -> CECreativeModeTabs.ITEM);
    }

    public static final MachineDefinition ADVANCED_ME_PATTERN_BUFFER = REGISTRATE
            .machine("advanced_me_pattern_buffer", AdvancedMEPatternBufferPartMachine::new)
            .cnLangValue("高级ME样板总成")
            .langValue("Advanced ME Pattern Buffer")
            .tier(ZPM)
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                    PartAbility.EXPORT_ITEMS)
            .rotationState(RotationState.ALL)
            .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch"))
            .register();

    public static final MachineDefinition ADVANCED_ME_PATTERN_BUFFER_PROXY = REGISTRATE
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

    public final static MachineDefinition DUAL_OUTPUT_HATCH_ME = REGISTRATE
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

    public static void init(){};
}
