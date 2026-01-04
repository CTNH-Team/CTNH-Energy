package tech.luckyblock.mcmod.ctnhenergy.registry;

import appeng.core.definitions.AEBlocks;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.mo_guang.ctpp.api.pattern.FactoryStaticBlockPattern;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.luckyblock.mcmod.ctnhenergy.api.CEPredicates;
import tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.machine.QuantumComputerMultiblockMachine;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.CN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.EN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.Prefix;

import static com.gregtechceu.gtceu.api.pattern.Predicates.abilities;
import static com.gregtechceu.gtceu.common.data.GTBlocks.ADVANCED_COMPUTER_CASING;
import static com.gregtechceu.gtceu.common.data.GTBlocks.COMPUTER_CASING;
import static tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy.REGISTRATE;

@SuppressWarnings("removal")
@Prefix("multiblock")
public class CEMultiblock {

    static {
        REGISTRATE.creativeModeTab(() -> CECreativeModeTabs.ITEM);
    }

    public static MultiblockMachineDefinition JIUZHANG_QUANTUM_COMPUTER;

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
                        .aisle("RRRRRRR###XXXXXXX", "RBBBBBS###YBBBBBX", "RBBBBBT###ZBBBBBX", "RBBBBSSEFEYYBBBBX", "RBBBSEEHHHEEYBBBX", "RBBSEEHHHHHEEYBBX", "RSTSEHHHHHHHECZYX", "###EHHHHHHHHHE###", "###FHHHHHHHHHF###", "###EHHHHHHHHHE###", "UVWVEHHHHHHHE2321", "UBBVEEHHHHHEE2BB1", "UBBBVEEHHHEE2BBB1", "UBBBBVVEFE22BBBB1", "UBBBBBW###3BBBBB1", "UBBBBBV###2BBBBB1", "UUUUUUU###1111111")
                        .aisle("RRRRRRR###XXXXXXX", "RBBBBBS###YBBBBBX", "RBBBBBT###ZBBBBBX", "RBBBBBS###YBBBBBX", "RBBBBSSEFEYYBBBBX", "RBBBSSEEFEEYYBBBX", "RSTSSEEHHHEECCZYX", "####EEHHHHHEE####", "####FFHHHHHFF####", "####EEHHHHHEE####", "UVWVVEEHHHEE22321", "UBBBVVEEFEE22BBB1", "UBBBBVVEFE22BBBB1", "UBBBBBV###2BBBBB1", "UBBBBBW###3BBBBB1", "UBBBBBV###2BBBBB1", "UUUUUUU###1111111")
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
                        )
                        .build())
                .workableCasingModel(CTNHEnergy.id("block/casings/steady_state_computing_matrix_shell"),
                        CTNHEnergy.id("block/machine/quantum_computer"))
                .tooltips(jiuzhang_tooltip)
                .register();


    }
}
