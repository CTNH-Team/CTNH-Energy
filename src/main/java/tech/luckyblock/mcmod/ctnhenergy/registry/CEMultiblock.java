package tech.luckyblock.mcmod.ctnhenergy.registry;

import appeng.core.definitions.AEBlocks;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.client.util.TooltipHelper;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import com.gregtechceu.gtceu.common.registry.GTRegistration;
import com.gregtechceu.gtceu.config.ConfigHolder;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.luckyblock.mcmod.ctnhenergy.api.CEPredicates;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.PowerSubstationMachine;
import tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.machine.QuantumComputerMultiblockMachine;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.CN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.EN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.Prefix;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.gregtechceu.gtceu.api.GTValues.EV;
import static com.gregtechceu.gtceu.api.GTValues.HV;
import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static com.gregtechceu.gtceu.api.pattern.Predicates.autoAbilities;
import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.*;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_PALLADIUM_SUBSTATION;
import static tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy.REGISTRATE;

@SuppressWarnings("removal")
@Prefix("multiblock")
public class CEMultiblock {

    static {
        REGISTRATE.creativeModeTab(() -> CECreativeModeTabs.ITEM);
    }

    public static MultiblockMachineDefinition JIUZHANG_QUANTUM_COMPUTER;
    public static MultiblockMachineDefinition POWER_SUBSTATION;
    @CN({
            "通过顶部的接口接入ME网络，作为合成CPU处理网络的自动合成任务",
            "自动分离出量子计算单元以创建子CPU来§e同时处理多个合成任务§r",
            "所有子CPU具有相同并行数，总内存取决于结构中所有合成存储器容量之和",
            "有比OMNI CPU更大的§b样板自动翻倍§r倍数",
            "可通过主界面设置并行数，每4并行需要消耗1算力",
            "能量消耗：(总内存x32 + 并行数x1920) EU/t",
            "§d§o策定乾坤算因果§r§r"
    })
    @EN({
            "","","","","","",
            "§d§oScheming the cosmos, Computing karma.§r§r"


    })
    static Lang[] jiuzhang_tooltip;

    @EN({
            "Voltage tier is determined by the §7highest-tier Capacitor§f.",
            "§4No Energy Hatch may exceed the Substation's voltage tier.§r"
    })
    @CN({
            "§e电压等级§r由§7电压等级最高的电容§f决定",
            "§4任何能源仓室的电压等级不能超过蓄能变电站§r"
    })
    static Lang[] powerstation_voltage;


    public static void init() {
        JIUZHANG_QUANTUM_COMPUTER = REGISTRATE.multiblock(
                        "jiuzhang_quantum_computer",
                        QuantumComputerMultiblockMachine::new
                )
                .cnLangValue("§6九章§r | §d量子超算§r")
                .langValue("§6JIUZHANG§r | §dQuantum Supercomputing§r")
                .rotationState(RotationState.NON_Y_AXIS)
                .recipeType(CERecipeTypes.QUANTUM_COMPUTER)
                .appearanceBlock(CEBlocks.STEADY_STATE_COMPUTING_MATRIX_SHELL)
                .pattern(definition -> FactoryBlockPattern.start()
                        .aisle("AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "#################", "#################", "#################", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA")
                        .aisle("AAAAAAA###AAAAAAA", "ABBBBBC###CBBBBBA", "ABBBBBC###CBBBBBA", "ABBBBBC###CBBBBBA", "ABBBBBC###CBBBBBA", "ABBBBBC###CBBBBBA", "ACCCCCC###CCCCCCA", "#################", "#################", "#################", "ACCCCCC###CCCCCCA", "ABBBBBC###CBBBBBA", "ABBBBBC###CBBBBBA", "ABBBBBC###CBBBBBA", "ABBBBBC###CBBBBBA", "ABBBBBC###CBBBBBA", "AAAAAAA###AAAAAAA")
                        .aisle("AAAAAAA###AAAAAAA", "ABBBBBC###CBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBD###DBBBBBA", "ACDDDDDEEEDDDDDCA", "######EFFFE######", "######EFGFE######", "######EFFFE######", "ACDDDDDEEEDDDDDCA", "ABBBBBD###DBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBC###CBBBBBA", "AAAAAAA###AAAAAAA")
                        .aisle("AAAAAAA###AAAAAAA", "ABBBBBC###CBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBC###CBBBBBA", "ABBBBCCEFECCBBBBA", "ABBBCCEEFEECCBBBA", "ACDCCEEHHHEECCDCA", "####EEHHHHHEE####", "####FFHHHHHFF####", "####EEHHHHHEE####", "ACDCCEEHHHEECCDCA", "ABBBCCEEFEECCBBBA", "ABBBBCCEFECCBBBBA", "ABBBBBC###CBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBC###CBBBBBA", "AAAAAAA###AAAAAAA")
                        .aisle("AAAAAAA###AAAAAAA", "ABBBBBC###CBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBCCEFECCBBBBA", "ABBBCEEHHHEECBBBA", "ABBCEEHHHHHEECBBA", "ACDCEHHHHHHHECDCA", "###EHHHHHHHHHE###", "###FHHHHHHHHHF###", "###EHHHHHHHHHE###", "ACDCEHHHHHHHECDCA", "ABBCEEHHHHHEECBBA", "ABBBCEEHHHEECBBBA", "ABBBBCCEFECCBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBC###CBBBBBA", "AAAAAAA###AAAAAAA")
                        .aisle("AAAAAAA###AAAAAAA", "ABBBBBC###CBBBBBA", "ABBBBBD###DBBBBBA", "ABBBCCEEFEECCBBBA", "ABBCEEHHHHHEECBBA", "ABBCEHHHHHHHECBBA", "ACDEHHHHHHHHHEDCA", "###EHHHHHHHHHE###", "###FHHHHHHHHHF###", "###EHHHHHHHHHE###", "ACDEHHHHHHHHHEDCA", "ABBCEHHHHHHHECBBA", "ABBCEEHHHHHEECBBA", "ABBBCCEEFEECCBBBA", "ABBBBBD###DBBBBBA", "ABBBBBC###CBBBBBA", "AAAAAAA###AAAAAAA")
                        .aisle("AAAAAAA###AAAAAAA", "ACCCCCC###CCCCCCA", "ACDDDDDEEEDDDDDCA", "ACDCCEEHHHEECCDCA", "ACDCEHHHHHHHECDCA", "ACDEHHHHHHHHHEDCA", "ACDEHHHHHHHHHEDCA", "##EHHHHHHHHHHHE##", "##EHHHHHHHHHHHE##", "##EHHHHHHHHHHHE##", "ACDEHHHHHHHHHEDCA", "ACDEHHHHHHHHHEDCA", "ACDCEHHHHHHHECDCA", "ACDCCEEHHHEECCDCA", "ACDDDDDEEEDDDDDCA", "ACCCCCC###CCCCCCA", "AAAAAAA###AAAAAAA")
                        .aisle("#################", "#################", "######EFFFE######", "####EEHHHHHEE####", "###EHHHHHHHHHE###", "###EHHHHHHHHHE###", "##EHHHHHHHHHHHE##", "##FHHHHHHHHHHHF##", "##FHHHHHHHHHHHF##", "##FHHHHHHHHHHHF##", "##EHHHHHHHHHHHE##", "###EHHHHHHHHHE###", "###EHHHHHHHHHE###", "####EEHHHHHEE####", "######EFFFE######", "#################", "#################")
                        .aisle("#################", "#################", "######EFGFE######", "####FFHHHHHFF####", "###FHHHHHHHHHF###", "###FHHHHHHHHHF###", "##EHHHHHHHHHHHE##", "##FHHHHHHHHHHHF##", "##GHHHHHHHHHHHG##", "##FHHHHHHHHHHHF##", "##EHHHHHHHHHHHE##", "###FHHHHHHHHHF###", "###FHHHHHHHHHF###", "####FFHHHHHFF####", "######EFIFE######", "#################", "#################")
                        .aisle("#################", "#################", "######EFFFE######", "####EEHHHHHEE####", "###EHHHHHHHHHE###", "###EHHHHHHHHHE###", "##EHHHHHHHHHHHE##", "##FHHHHHHHHHHHF##", "##FHHHHHHHHHHHF##", "##FHHHHHHHHHHHF##", "##EHHHHHHHHHHHE##", "###EHHHHHHHHHE###", "###EHHHHHHHHHE###", "####EEHHHHHEE####", "######EFFFE######", "#################", "#################")
                        .aisle("AAAAAAA###AAAAAAA", "ACCCCCC###CCCCCCA", "ACDDDDDEEEDDDDDCA", "ACDCCEEHHHEECCDCA", "ACDCEHHHHHHHECDCA", "ACDEHHHHHHHHHEDCA", "ACDEHHHHHHHHHEDCA", "##EHHHHHHHHHHHE##", "##EHHHHHHHHHHHE##", "##EHHHHHHHHHHHE##", "ACDEHHHHHHHHHEDCA", "ACDEHHHHHHHHHEDCA", "ACDCEHHHHHHHECDCA", "ACDCCEEHHHEECCDCA", "ACDDDDDEEEDDDDDCA", "ACCCCCC###CCCCCCA", "AAAAAAA###AAAAAAA")
                        .aisle("AAAAAAA###AAAAAAA", "ABBBBBC###CBBBBBA", "ABBBBBD###DBBBBBA", "ABBBCCEEFEECCBBBA", "ABBCEEHHHHHEECBBA", "ABBCEHHHHHHHECBBA", "ACDEHHHHHHHHHEDCA", "###EHHHHHHHHHE###", "###FHHHHHHHHHF###", "###EHHHHHHHHHE###", "ACDEHHHHHHHHHEDCA", "ABBCEHHHHHHHECBBA", "ABBCEEHHHHHEECBBA", "ABBBCCEEFEECCBBBA", "ABBBBBD###DBBBBBA", "ABBBBBC###CBBBBBA", "AAAAAAA###AAAAAAA")
                        .aisle("AAAAAAA###AAAAAAA", "ABBBBBC###CBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBCCEFECCBBBBA", "ABBBCEEHHHEECBBBA", "ABBCEEHHHHHEECBBA", "ACDCEHHHHHHHECDCA", "###EHHHHHHHHHE###", "###FHHHHHHHHHF###", "###EHHHHHHHHHE###", "ACDCEHHHHHHHECDCA", "ABBCEEHHHHHEECBBA", "ABBBCEEHHHEECBBBA", "ABBBBCCEFECCBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBC###CBBBBBA", "AAAAAAA###AAAAAAA")
                        .aisle("AAAAAAA###AAAAAAA", "ABBBBBC###CBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBC###CBBBBBA", "ABBBBCCEFECCBBBBA", "ABBBCCEEFEECCBBBA", "ACDCCEEHHHEECCDCA", "####EEHHHHHEE####", "####FFHHHHHFF####", "####EEHHHHHEE####", "ACDCCEEHHHEECCDCA", "ABBBCCEEFEECCBBBA", "ABBBBCCEFECCBBBBA", "ABBBBBC###CBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBC###CBBBBBA", "AAAAAAA###AAAAAAA")
                        .aisle("AAAAAAA###AAAAAAA", "ABBBBBC###CBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBD###DBBBBBA", "ACDDDDDEEEDDDDDCA", "######EFFFE######", "######EFJFE######", "######EFFFE######", "ACDDDDDEEEDDDDDCA", "ABBBBBD###DBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBD###DBBBBBA", "ABBBBBC###CBBBBBA", "AAAAAAA###AAAAAAA")
                        .aisle("AAAAAAA###AAAAAAA", "ABBBBBC###CBBBBBA", "ABBBBBC###CBBBBBA", "ABBBBBC###CBBBBBA", "ABBBBBC###CBBBBBA", "ABBBBBC###CBBBBBA", "ACCCCCC###CCCCCCA", "#################", "#################", "#################", "ACCCCCC###CCCCCCA", "ABBBBBC###CBBBBBA", "ABBBBBC###CBBBBBA", "ABBBBBC###CBBBBBA", "ABBBBBC###CBBBBBA", "ABBBBBC###CBBBBBA", "AAAAAAA###AAAAAAA")
                        .aisle("AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "#################", "#################", "#################", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA", "AAAAAAA###AAAAAAA")
                        .where("I", Predicates.blocks(CEBlocks.QUANTUM_COMPUTER_ME_NETWORK_PORT.get()))
                        .where("G", Predicates.blocks(AEBlocks.NOT_SO_MYSTERIOUS_CUBE.block())
                                .or(Predicates.abilities(PartAbility.COMPUTATION_DATA_RECEPTION).setExactLimit(1).setPreviewCount(1))
                        )
                        .where("D", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_FRAME.get()))
                        .where("#", Predicates.any())
                        .where("C", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_WALL.get()))
                        .where("E", Predicates.blocks(CEBlocks.QUANTUM_POINTING_BLOCK.get()))
                        .where("H", CEPredicates.craftingUnitBlock())
                        .where("J", Predicates.controller(Predicates.blocks(definition.get())))
                        .where("B", Predicates.air())
                        .where("A", Predicates.blocks(CEBlocks.QUANTUM_COMPUTER_CASING.get()))
                        .where("F", Predicates.blocks(CEBlocks.STEADY_STATE_COMPUTING_MATRIX_SHELL.get())
                                .or(Predicates.autoAbilities(CERecipeTypes.QUANTUM_COMPUTER))
                        )
                        .build())
                .workableCasingModel(CTNHEnergy.id("block/casings/steady_state_computing_matrix_shell"),
                        CTNHEnergy.id("block/machine/quantum_computer"))
                .tooltips(jiuzhang_tooltip)
                .register();

        POWER_SUBSTATION = REGISTRATE
                .multiblock("power_substation", PowerSubstationMachine::new)
                .cnLangValue("蓄能变电站")
                .rotationState(RotationState.ALL)
                .recipeType(GTRecipeTypes.DUMMY_RECIPES)
                .tooltips(Component.translatable("gtceu.machine.power_substation.tooltip.0"),
                        Component.translatable("gtceu.machine.power_substation.tooltip.1"),
                        Component.translatable("gtceu.machine.power_substation.tooltip.2", PowerSubstationMachine.MAX_BATTERY_LAYERS),
                        Component.translatable("gtceu.machine.power_substation.tooltip.3"),
                        Component.translatable("gtceu.machine.power_substation.tooltip.4", PowerSubstationMachine.PASSIVE_DRAIN_MAX_PER_STORAGE / 1000),
                        powerstation_voltage[0].translate(),
                        powerstation_voltage[1].translate()
                )
                .tooltipBuilder(
                        (stack,
                         components) -> components.add(Component.translatable("gtceu.machine.power_substation.tooltip.5")
                                .append(Component.translatable("gtceu.machine.power_substation.tooltip.6")
                                        .withStyle(TooltipHelper.RAINBOW_HSL_SLOW))))
                .appearanceBlock(CASING_PALLADIUM_SUBSTATION)
                .pattern(definition -> FactoryBlockPattern.start(RIGHT, BACK, UP)
                        .aisle("XXSXX", "XXXXX", "XXXXX", "XXXXX", "XXXXX")
                        .aisle("XXXXX", "XCCCX", "XCCCX", "XCCCX", "XXXXX")
                        .aisle("GGGGG", "GBBBG", "GBBBG", "GBBBG", "GGGGG")
                        .setRepeatable(1, PowerSubstationMachine.MAX_BATTERY_LAYERS)
                        .aisle("GGGGG", "GGGGG", "GGGGG", "GGGGG", "GGGGG")
                        .where('S', controller(blocks(definition.getBlock())))
                        .where('C', blocks(CASING_PALLADIUM_SUBSTATION.get()))
                        .where('X',
                                blocks(CASING_PALLADIUM_SUBSTATION.get())
                                        .setMinGlobalLimited(PowerSubstationMachine.MIN_CASINGS)
                                        .or(autoAbilities(true, false, false))
                                        .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.SUBSTATION_INPUT_ENERGY,
                                                PartAbility.INPUT_LASER).setMinGlobalLimited(1))
                                        .or(abilities(PartAbility.OUTPUT_ENERGY, PartAbility.SUBSTATION_OUTPUT_ENERGY,
                                                PartAbility.OUTPUT_LASER).setMinGlobalLimited(1)))
                        .where('G', blocks(CASING_LAMINATED_GLASS.get()))
                        .where('B', CEPredicates.powerSubstationBatteries())
                        .build())
                .shapeInfos(definition -> {
                    List<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
                    MultiblockShapeInfo.ShapeInfoBuilder builder = MultiblockShapeInfo.builder()
                            .aisle("ICSCO", "NCMCT", "GGGGG", "GGGGG", "GGGGG")
                            .aisle("CCCCC", "CCCCC", "GBBBG", "GBBBG", "GGGGG")
                            .aisle("CCCCC", "CCCCC", "GBBBG", "GBBBG", "GGGGG")
                            .aisle("CCCCC", "CCCCC", "GBBBG", "GBBBG", "GGGGG")
                            .aisle("CCCCC", "CCCCC", "GGGGG", "GGGGG", "GGGGG")
                            .where('S', definition, Direction.NORTH)
                            .where('C', CASING_PALLADIUM_SUBSTATION)
                            .where('G', CASING_LAMINATED_GLASS)
                            .where('I', GTMachines.ENERGY_INPUT_HATCH[HV], Direction.NORTH)
                            .where('N', GTMachines.SUBSTATION_ENERGY_INPUT_HATCH[EV], Direction.NORTH)
                            .where('O', GTMachines.ENERGY_OUTPUT_HATCH[HV], Direction.NORTH)
                            .where('T', GTMachines.SUBSTATION_ENERGY_OUTPUT_HATCH[EV], Direction.NORTH)
                            .where('M',
                                    ConfigHolder.INSTANCE.machines.enableMaintenance ?
                                            GTMachines.MAINTENANCE_HATCH.getBlock().defaultBlockState().setValue(
                                                    GTMachines.MAINTENANCE_HATCH.get().getRotationState().property,
                                                    Direction.NORTH) :
                                            CASING_PALLADIUM_SUBSTATION.get().defaultBlockState());

                    GTCEuAPI.PSS_BATTERIES.entrySet().stream()
                            // filter out empty batteries in example structures, though they are still
                            // allowed in the predicate (so you can see them on right-click)
                            .filter(entry -> entry.getKey().getCapacity() > 0)
                            .sorted(Comparator.comparingInt(entry -> entry.getKey().getTier()))
                            .forEach(entry -> shapeInfo.add(builder.where('B', entry.getValue().get()).build()));

                    return shapeInfo;
                })
                .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_palladium_substation"),
                        GTCEu.id("block/multiblock/power_substation"))
                .register();
    }
}
