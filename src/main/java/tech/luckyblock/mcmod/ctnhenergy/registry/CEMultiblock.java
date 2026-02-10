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
import com.mo_guang.ctpp.api.pattern.FactoryStaticBlockPattern;
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
            "自动创建子CPU来§e同时处理多个合成任务§r",
            "所有子CPU具有相同并行数，CPU并行数 = 并行控制仓并行数 * 当前算力消耗",
            "总内存取决于结构中所有合成存储器容量之和",
            "可通过UI设置当前算力消耗和§b样板自动翻倍§r倍数",
            "能量消耗：(总内存x32 + 并行数x512) EU/t",
            "§d§o策定乾坤算因果§r§r"
    })
    @EN({
            "","","","","","","",
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
                .pattern(definition -> FactoryStaticBlockPattern.start()
                        .aisle("AAAAAAA###LLLLLLL", "AAAAAAA###LLLLLLL", "AAAAAAA###LLLLLLL", "AAAAAAA###LLLLLLL", "AAAAAAA###LLLLLLL", "AAAAAAA###LLLLLLL", "AAAAAAA###LLLLLLL", "#################", "#################", "#################", "IIIIIII###OOOOOOO", "IIIIIII###OOOOOOO", "IIIIIII###OOOOOOO", "IIIIIII###OOOOOOO", "IIIIIII###OOOOOOO", "IIIIIII###OOOOOOO", "IIIIIII###OOOOOOO")
                        .aisle("AAAAAAA###LLLLLLL", "ABBBBBC###MBBBBBL", "ABBBBBC###MBBBBBL", "ABBBBBC###MBBBBBL", "ABBBBBC###MBBBBBL", "ABBBBBC###MBBBBBL", "ACCCCCC###MMMMMML", "#################", "#################", "#################", "IJJJJJJ###PPPPPPO", "IBBBBBJ###PBBBBBO", "IBBBBBJ###PBBBBBO", "IBBBBBJ###PBBBBBO", "IBBBBBJ###PBBBBBO", "IBBBBBJ###PBBBBBO", "IIIIIII###OOOOOOO")
                        .aisle("AAAAAAA###LLLLLLL", "ABBBBBC###MBBBBBL", "ABBBBBD###NBBBBBL", "ABBBBBD###NBBBBBL", "ABBBBBD###NBBBBBL", "ABBBBBD###NBBBBBL", "ACDDDDDEEENNNNNML", "######EFFFE######", "######EFGFE######", "######EFFFE######", "IJKKKKKEEEQQQQQPO", "IBBBBBK###QBBBBBO", "IBBBBBK###QBBBBBO", "IBBBBBK###QBBBBBO", "IBBBBBK###QBBBBBO", "IBBBBBJ###PBBBBBO", "IIIIIII###OOOOOOO")
                        .aisle("AAAAAAA###LLLLLLL", "ABBBBBC###MBBBBBL", "ABBBBBD###NBBBBBL", "ABBBBBC###MBBBBBL", "ABBBBCCEFEMMBBBBL", "ABBBCCEEFEEMMBBBL", "ACDCCEEHHHEEMMNML", "####EEHHHHHEE####", "####FFHHHHHFF####", "####EEHHHHHEE####", "IJKJJEEHHHEEPPQPO", "IBBBJJEEFEEPPBBBO", "IBBBBJJEFEPPBBBBO", "IBBBBBJ###PBBBBBO", "IBBBBBK###QBBBBBO", "IBBBBBJ###PBBBBBO", "IIIIIII###OOOOOOO")
                        .aisle("AAAAAAA###LLLLLLL", "ABBBBBC###MBBBBBL", "ABBBBBD###NBBBBBL", "ABBBBCCEFEMMBBBBL", "ABBBCEEHHHEEMBBBL", "ABBCEEHHHHHEEMBBL", "ACDCEHHHHHHHEMNML", "###EHHHHHHHHHE###", "###FHHHHHHHHHF###", "###EHHHHHHHHHE###", "IJKJEHHHHHHHEPQPO", "IBBJEEHHHHHEEPBBO", "IBBBJEEHHHEEPBBBO", "IBBBBJJEFEPPBBBBO", "IBBBBBK###QBBBBBO", "IBBBBBJ###PBBBBBO", "IIIIIII###OOOOOOO")
                        .aisle("AAAAAAA###LLLLLLL", "ABBBBBC###MBBBBBL", "ABBBBBD###NBBBBBL", "ABBBCCEEFEEMMBBBL", "ABBCEEHHHHHEEMBBL", "ABBCEHHHHHHHEMBBL", "ACDEHHHHHHHHHENML", "###EHHHHHHHHHE###", "###FHHHHHHHHHF###", "###EHHHHHHHHHE###", "IJKEHHHHHHHHHEQPO", "IBBJEHHHHHHHEPBBO", "IBBJEEHHHHHEEPBBO", "IBBBJJEEFEEPPBBBO", "IBBBBBK###QBBBBBO", "IBBBBBJ###PBBBBBO", "IIIIIII###OOOOOOO")
                        .aisle("AAAAAAA###LLLLLLL", "ACCCCCC###MMMMMML", "ACDDDDDEEENNNNNML", "ACDCCEEHHHEEMMNML", "ACDCEHHHHHHHEMNML", "ACDEHHHHHHHHHENML", "ACDEHHHHHHHHHENML", "##EHHHHHHHHHHHE##", "##EHHHHHHHHHHHE##", "##EHHHHHHHHHHHE##", "IJKEHHHHHHHHHEQPO", "IJKEHHHHHHHHHEQPO", "IJKJEHHHHHHHEPQPO", "IJKJJEEHHHEEPPQPO", "IJKKKKKEEEQQQQQPO", "IJJJJJJ###PPPPPPO", "IIIIIII###OOOOOOO")
                        .aisle("#################", "#################", "######EFFFE######", "####EEHHHHHEE####", "###EHHHHHHHHHE###", "###EHHHHHHHHHE###", "##EHHHHHHHHHHHE##", "##FHHHHHHHHHHHF##", "##FHHHHHHHHHHHF##", "##FHHHHHHHHHHHF##", "##EHHHHHHHHHHHE##", "###EHHHHHHHHHE###", "###EHHHHHHHHHE###", "####EEHHHHHEE####", "######EFFFE######", "#################", "#################")
                        .aisle("#################", "#################", "######EFGFE######", "####FFHHHHHFF####", "###FHHHHHHHHHF###", "###FHHHHHHHHHF###", "##EHHHHHHHHHHHE##", "##FHHHHHHHHHHHF##", "##GHHHHHHHHHHHG##", "##FHHHHHHHHHHHF##", "##EHHHHHHHHHHHE##", "###FHHHHHHHHHF###", "###FHHHHHHHHHF###", "####FFHHHHHFF####", "######EF4FE######", "#################", "#################")
                        .aisle("#################", "#################", "######EFFFE######", "####EEHHHHHEE####", "###EHHHHHHHHHE###", "###EHHHHHHHHHE###", "##EHHHHHHHHHHHE##", "##FHHHHHHHHHHHF##", "##FHHHHHHHHHHHF##", "##FHHHHHHHHHHHF##", "##EHHHHHHHHHHHE##", "###EHHHHHHHHHE###", "###EHHHHHHHHHE###", "####EEHHHHHEE####", "######EFFFE######", "#################", "#################")
                        .aisle("RRRRRRR###XXXXXXX", "RSSSSSS###YYYYYYX", "RSTTTTTEEEZZZZZYX", "RSTSSEEHHHEEYYZYX", "RSTSEHHHHHHHEYZYX", "RSTEHHHHHHHHHEZYX", "RSTEHHHHHHHHHEZYX", "##EHHHHHHHHHHHE##", "##EHHHHHHHHHHHE##", "##EHHHHHHHHHHHE##", "UVWEHHHHHHHHHE321", "UVWEHHHHHHHHHE321", "UVWVEHHHHHHHE2321", "UVWVVEEHHHEE22321", "UVWWWWWEEE3333321", "UVVVVVV###2222221", "UUUUUUU###1111111")
                        .aisle("RRRRRRR###XXXXXXX", "RBBBBBS###YBBBBBX", "RBBBBBT###ZBBBBBX", "RBBBSSEEFEEYYBBBX", "RBBSEEHHHHHEEYBBX", "RBBSEHHHHHHHEYBBX", "RSTEHHHHHHHHHEZYX", "###EHHHHHHHHHE###", "###FHHHHHHHHHF###", "###EHHHHHHHHHE###", "UVWEHHHHHHHHHE321", "UBBVEHHHHHHHE2BB1", "UBBVEEHHHHHEE2BB1", "UBBBVVEEFEE22BBB1", "UBBBBBW###3BBBBB1", "UBBBBBV###2BBBBB1", "UUUUUUU###1111111")
                        .aisle("RRRRRRR###XXXXXXX", "RBBBBBS###YBBBBBX", "RBBBBBT###ZBBBBBX", "RBBBBSSEFEYYBBBBX", "RBBBSEEHHHEEYBBBX", "RBBSEEHHHHHEEYBBX", "RSTSEHHHHHHHEYZYX", "###EHHHHHHHHHE###", "###FHHHHHHHHHF###", "###EHHHHHHHHHE###", "UVWVEHHHHHHHE2321", "UBBVEEHHHHHEE2BB1", "UBBBVEEHHHEE2BBB1", "UBBBBVVEFE22BBBB1", "UBBBBBW###3BBBBB1", "UBBBBBV###2BBBBB1", "UUUUUUU###1111111")
                        .aisle("RRRRRRR###XXXXXXX", "RBBBBBS###YBBBBBX", "RBBBBBT###ZBBBBBX", "RBBBBBS###YBBBBBX", "RBBBBSSEFEYYBBBBX", "RBBBSSEEFEEYYBBBX", "RSTSSEEHHHEEYYZYX", "####EEHHHHHEE####", "####FFHHHHHFF####", "####EEHHHHHEE####", "UVWVVEEHHHEE22321", "UBBBVVEEFEE22BBB1", "UBBBBVVEFE22BBBB1", "UBBBBBV###2BBBBB1", "UBBBBBW###3BBBBB1", "UBBBBBV###2BBBBB1", "UUUUUUU###1111111")
                        .aisle("RRRRRRR###XXXXXXX", "RBBBBBS###YBBBBBX", "RBBBBBT###ZBBBBBX", "RBBBBBT###ZBBBBBX", "RBBBBBT###ZBBBBBX", "RBBBBBT###ZBBBBBX", "RSTTTTTEEEZZZZZYX", "######EFFFE######", "######EF@FE######", "######EFFFE######", "UVWWWWWEEE3333321", "UBBBBBW###3BBBBB1", "UBBBBBW###3BBBBB1", "UBBBBBW###3BBBBB1", "UBBBBBW###3BBBBB1", "UBBBBBV###2BBBBB1", "UUUUUUU###1111111")
                        .aisle("RRRRRRR###XXXXXXX", "RBBBBBS###YBBBBBX", "RBBBBBS###YBBBBBX", "RBBBBBS###YBBBBBX", "RBBBBBS###YBBBBBX", "RBBBBBS###YBBBBBX", "RSSSSSS###YYYYYYX", "#################", "#################", "#################", "UVVVVVV###2222221", "UBBBBBV###2BBBBB1", "UBBBBBV###2BBBBB1", "UBBBBBV###2BBBBB1", "UBBBBBV###2BBBBB1", "UBBBBBV###2BBBBB1", "UUUUUUU###1111111")
                        .aisle("RRRRRRR###XXXXXXX", "RRRRRRR###XXXXXXX", "RRRRRRR###XXXXXXX", "RRRRRRR###XXXXXXX", "RRRRRRR###XXXXXXX", "RRRRRRR###XXXXXXX", "RRRRRRR###XXXXXXX", "#################", "#################", "#################", "UUUUUUU###1111111", "UUUUUUU###1111111", "UUUUUUU###1111111", "UUUUUUU###1111111", "UUUUUUU###1111111", "UUUUUUU###1111111", "UUUUUUU###1111111")
                        .where("4", Predicates.blocks(CEBlocks.QUANTUM_COMPUTER_ME_NETWORK_PORT.get()))
                        .where("G", Predicates.blocks(AEBlocks.NOT_SO_MYSTERIOUS_CUBE.block())
                                .or(Predicates.abilities(PartAbility.COMPUTATION_DATA_RECEPTION).setExactLimit(1).setPreviewCount(1))
                        )
                        .where("#", Predicates.any())
                        .where("E", Predicates.blocks(CEBlocks.QUANTUM_POINTING_BLOCK.get()))
                        .where("H", CEPredicates.craftingUnitBlock())
                        .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                        .where("B", Predicates.air())
                        .where("A", Predicates.blocks(CEBlocks.QUANTUM_COMPUTER_CASING.get()), false, 0)
                        .where("C", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_WALL.get()), false, 0)
                        .where("D", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_FRAME.get()), false, 0)
                        .where("I", Predicates.blocks(CEBlocks.QUANTUM_COMPUTER_CASING.get()), false, 1)
                        .where("J", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_WALL.get()), false, 1)
                        .where("K", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_FRAME.get()), false, 1)
                        .where("L", Predicates.blocks(CEBlocks.QUANTUM_COMPUTER_CASING.get()), false, 2)
                        .where("M", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_WALL.get()), false, 2)
                        .where("N", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_FRAME.get()), false, 2)
                        .where("O", Predicates.blocks(CEBlocks.QUANTUM_COMPUTER_CASING.get()), false, 3)
                        .where("P", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_WALL.get()), false, 3)
                        .where("Q", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_FRAME.get()), false, 3)
                        .where("R", Predicates.blocks(CEBlocks.QUANTUM_COMPUTER_CASING.get()), false, 4)
                        .where("S", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_WALL.get()), false, 4)
                        .where("T", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_FRAME.get()), false, 4)
                        .where("U", Predicates.blocks(CEBlocks.QUANTUM_COMPUTER_CASING.get()), false, 5)
                        .where("V", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_WALL.get()), false, 5)
                        .where("W", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_FRAME.get()), false, 5)
                        .where("X", Predicates.blocks(CEBlocks.QUANTUM_COMPUTER_CASING.get()), false, 6)
                        .where("Y", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_WALL.get()), false, 6)
                        .where("Z", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_FRAME.get()), false, 6)
                        .where("1", Predicates.blocks(CEBlocks.QUANTUM_COMPUTER_CASING.get()), false, 7)
                        .where("2", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_WALL.get()), false, 7)
                        .where("3", Predicates.blocks(CEBlocks.ASSEMBLER_MATRIX_FRAME.get()), false, 7)
                        .where("F", Predicates.blocks(CEBlocks.STEADY_STATE_COMPUTING_MATRIX_SHELL.get())
                                .or(Predicates.autoAbilities(CERecipeTypes.QUANTUM_COMPUTER))
                                .or(Predicates.autoAbilities(false, false, true))
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
