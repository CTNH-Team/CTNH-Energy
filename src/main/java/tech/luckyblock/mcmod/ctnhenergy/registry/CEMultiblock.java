package tech.luckyblock.mcmod.ctnhenergy.registry;

import appeng.core.definitions.AEBlocks;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.registries.ForgeRegistries;
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
                .recipeType(GTRecipeTypes.DUMMY_RECIPES)
                .appearanceBlock(COMPUTER_CASING)
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
                        .where("G", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("ae2:1k_crafting_storage"))))
                        .where("I", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("ctnhenergy:quantum_computer_me_network_port"))))
                        .where("F", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("ae2:not_so_mysterious_cube"))))
                        .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                        .where("#", Predicates.any())
                        .where("C", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("expatternprovider:assembler_matrix_wall"))))
                        .where("E", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("ctnhenergy:quantum_pointing_block"))))
                        .where("H", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("ae2:dense_energy_cell"))))
                        .where("B", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft:cobblestone"))))
                        .where("A", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("ctnhenergy:quantum_computer_casing"))))
                        .where("D", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("ctnhenergy:light_computer_casing"))))
                        .build())
                .model((ctx, prov, builder) ->{
                    builder.forAllStates(state ->{
                        BlockModelBuilder model = prov.models().withExistingParent(ctx.getName(), GTCEu.id("block/overlay/front_all"))
                                .texture("all", CTNHEnergy.id("block/casings/light_computer_casing"))
                                .texture("overlay", CTNHEnergy.id("block/overlay/quantum_computer"));
                        return ConfiguredModel.builder().modelFile(model).build();
                    });
                })

                .register();


    }
}
