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

import static com.gregtechceu.gtceu.api.pattern.Predicates.abilities;
import static com.gregtechceu.gtceu.common.data.GTBlocks.ADVANCED_COMPUTER_CASING;
import static com.gregtechceu.gtceu.common.data.GTBlocks.COMPUTER_CASING;
import static tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy.REGISTRATE;

@SuppressWarnings("removal")
public class CEMultiblock {

    static {
        REGISTRATE.creativeModeTab(() -> CECreativeModeTabs.ITEM);
    }

    public static MultiblockMachineDefinition JIUZHANG_QUANTUM_COMPUTER;

    public static void init() {
        JIUZHANG_QUANTUM_COMPUTER = REGISTRATE.multiblock(
                        "jiuzhang_quantum_computer",
                        QuantumComputerMultiblockMachine::new
                )
                .cnLangValue("§6九章§r | §d量子超算§r")
                .langValue("§6JIUZHANG§r | §dQuantum Supercomputing§r")
                .rotationState(RotationState.NON_Y_AXIS)
                .recipeType(CERecipeTypes.QUANTUM_COMPUTER)
                .appearanceBlock(CEBlocks.LIGHT_COMPUTER_CASING)
                .pattern(definition -> FactoryBlockPattern.start()
                        .aisle("AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "###############", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA")
                        .aisle("AAAAAA###AAAAAA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ACCCCC###CCCCCA", "###############", "ACCCCC###CCCCCA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "AAAAAA###AAAAAA")
                        .aisle("AAAAAA###AAAAAA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ACCCCC###CCCCCA", "###############", "ACCCCC###CCCCCA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "AAAAAA###AAAAAA")
                        .aisle("AAAAAA###AAAAAA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ACCCCC###CCCCCA", "###############", "ACCCCC###CCCCCA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "AAAAAA###AAAAAA")
                        .aisle("AAAAAA###AAAAAA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBDDDDDBBBBA", "ACCCCDEEEDCCCCA", "#####DEFED#####", "ACCCCDEEEDCCCCA", "ABBBBDDDDDBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "AAAAAA###AAAAAA")
                        .aisle("AAAAAA###AAAAAA", "ACCCCC###CCCCCA", "ACCCCC###CCCCCA", "ACCCCC###CCCCCA", "ACCCCDEEEDCCCCA", "ACCCCEGGGECCCCA", "#####EGGGE#####", "ACCCCEGGGECCCCA", "ACCCCDEEEDCCCCA", "ACCCCC###CCCCCA", "ACCCCC###CCCCCA", "ACCCCC###CCCCCA", "AAAAAA###AAAAAA")
                        .aisle("###############", "###############", "###############", "###############", "#####DEFED#####", "#####EGGGE#####", "#####FGHGF#####", "#####EGGGE#####", "#####DEIED#####", "###############", "###############", "###############", "###############")
                        .aisle("AAAAAA###AAAAAA", "ACCCCC###CCCCCA", "ACCCCC###CCCCCA", "ACCCCC###CCCCCA", "ACCCCDEEEDCCCCA", "ACCCCEGGGECCCCA", "#####EGGGE#####", "ACCCCEGGGECCCCA", "ACCCCDEEEDCCCCA", "ACCCCC###CCCCCA", "ACCCCC###CCCCCA", "ACCCCC###CCCCCA", "AAAAAA###AAAAAA")
                        .aisle("AAAAAA###AAAAAA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBDDDDDBBBBA", "ACCCCDEEEDCCCCA", "#####DE@ED#####", "ACCCCDEEEDCCCCA", "ABBBBDDDDDBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "AAAAAA###AAAAAA")
                        .aisle("AAAAAA###AAAAAA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ACCCCC###CCCCCA", "###############", "ACCCCC###CCCCCA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "AAAAAA###AAAAAA")
                        .aisle("AAAAAA###AAAAAA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ACCCCC###CCCCCA", "###############", "ACCCCC###CCCCCA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "AAAAAA###AAAAAA")
                        .aisle("AAAAAA###AAAAAA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ACCCCC###CCCCCA", "###############", "ACCCCC###CCCCCA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "ABBBBC###CBBBBA", "AAAAAA###AAAAAA")
                        .aisle("AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "###############", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA", "AAAAAA###AAAAAA")
                        .where("G", CEPredicates.craftingUnitBlock())
                        .where("I", Predicates.blocks(CEBlocks.QUANTUM_COMPUTER_ME_NETWORK_PORT.get()))
                        .where("F", Predicates.blocks(AEBlocks.NOT_SO_MYSTERIOUS_CUBE.block())
                                .or(Predicates.abilities(PartAbility.COMPUTATION_DATA_RECEPTION).setExactLimit(1).setPreviewCount(1))
                        )
                        .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                        .where("#", Predicates.any())
                        .where("C", Predicates.blocks(EPPItemAndBlock.ASSEMBLER_MATRIX_WALL))
                        .where("E", Predicates.blocks(CEBlocks.QUANTUM_POINTING_BLOCK.get()))
                        .where("H", Predicates.blocks(AEBlocks.DENSE_ENERGY_CELL.block()))
                        .where("B", Predicates.air())
                        .where("A", Predicates.blocks(CEBlocks.QUANTUM_COMPUTER_CASING.get()))
                        .where("D", Predicates.blocks(CEBlocks.LIGHT_COMPUTER_CASING.get())
                                .or(Predicates.autoAbilities(CERecipeTypes.QUANTUM_COMPUTER))
                        )
                        .build())
                .workableCasingModel(CTNHEnergy.id("block/casings/light_computer_casing"),
                        CTNHEnergy.id("block/machine/quantum_computer"))
                .register();


    }
}
