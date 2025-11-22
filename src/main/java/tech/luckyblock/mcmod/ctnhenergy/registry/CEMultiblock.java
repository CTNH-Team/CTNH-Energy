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
            "","","","","","",""
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
                        CTNHEnergy.id("block/machine/quantum_computer")).tooltips(jiuzhang_tooltip)
                .register();


    }
}
