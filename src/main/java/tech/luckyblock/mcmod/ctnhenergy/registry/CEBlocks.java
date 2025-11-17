package tech.luckyblock.mcmod.ctnhenergy.registry;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.registry.registrate.provider.GTBlockstateProvider;
import com.gregtechceu.gtceu.common.data.models.GTModels;
import com.gregtechceu.gtceu.common.registry.GTRegistration;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.luckyblock.mcmod.ctnhenergy.common.block.QuantumComputerCasingBlock;
import tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.port.QuantumComputerMENetworkPortBlock;
import tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.port.QuantumComputerMENetworkPortBlockEntity;

import java.util.function.Supplier;

import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.casingTextures;
import static tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy.REGISTRATE;

public class CEBlocks {

    static {
        REGISTRATE.creativeModeTab(() -> CECreativeModeTabs.ITEM);
    }

    public static BlockEntry<QuantumComputerMENetworkPortBlock> QUANTUM_COMPUTER_ME_NETWORK_PORT;
    public static BlockEntry<QuantumComputerCasingBlock> QUANTUM_COMPUTER_CASING;
    public static BlockEntry<Block> STEADY_STATE_COMPUTING_MATRIX_SHELL = createCasingBlock("steady_state_computing_matrix_shell", "稳态计算矩阵外壳", CTNHEnergy.id("block/casings/steady_state_computing_matrix_shell"));
    public static BlockEntry<Block> QUANTUM_POINTING_BLOCK = createCasingBlock("quantum_pointing_block", "量子指向方块", CTNHEnergy.id("block/casings/quantum_pointing_block"));
    public static BlockEntry<Block> ASSEMBLER_MATRIX_FRAME =
            createCasingBlock("assembler_matrix_frame_no_entity", "装配矩阵框架（无实体）", CTNHEnergy.id("block/casings/assembler_matrix_frame"));
    public static BlockEntry<Block> ASSEMBLER_MATRIX_WALL =
            createCasingBlock("assembler_matrix_wall_no_entity", "装配矩阵墙壁（无实体）", ResourceLocation.parse("expatternprovider:block/assembler_matrix/wall_block"));


    public static void init(){
        QUANTUM_COMPUTER_ME_NETWORK_PORT =
                REGISTRATE.block("quantum_computer_me_network_port", QuantumComputerMENetworkPortBlock::new)
                        .cnlang("量子计算机ME网络接口")
                        .lang("Quantum Computer ME Network Port")
                        .initialProperties(() -> Blocks.IRON_BLOCK)
                        .properties(p ->
                                p.isValidSpawn((state, level, pos, ent) -> false)
                        )

                        .blockstate((ctx, prov) -> {
                            BlockModelBuilder model = prov.models().withExistingParent(ctx.getName(),CTNHEnergy.id("block/overlay/top_all"))
                                    .texture("all", CTNHEnergy.id("block/casings/steady_state_computing_matrix_shell"))
                                    .texture("overlay", CTNHEnergy.id("block/overlay/quantum_computer_port"));

                            prov.simpleBlock(ctx.getEntry(), model);
                        })
                        .addLayer(() -> RenderType::cutoutMipped)
                        .blockEntity(QuantumComputerMENetworkPortBlockEntity::new)
                        .onRegister(CEBlocks::registerBlockEntity)
                        .build()
                        .item(BlockItem::new)

                        .build()
                        .register();

        QUANTUM_COMPUTER_CASING = REGISTRATE.block("quantum_computer_casing", QuantumComputerCasingBlock::new)
                .cnlang("量子计算机外壳")
                .lang("Quantum Computer Casing")
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p ->
                        p.isValidSpawn((state, level, pos, ent) -> false)
                )
                .blockstate((ctx, prov) -> {
                    VariantBlockStateBuilder builder = prov.getVariantBuilder(ctx.getEntry());
                    for(var state: QuantumComputerCasingBlock.State.values()){
                        var stateName = state.getSerializedName();
                        BlockModelBuilder model = prov.models().withExistingParent(
                                ctx.getName() + "_" + stateName,
                                GTCEu.id("block/cube/tinted/all"))
                                .texture("all", CTNHEnergy.id("block/casings/quantum_computer_casing_" + stateName));
                        builder.partialState().with(QuantumComputerCasingBlock.STATE, state)
                                .modelForState().modelFile(model).addModel();
                    }

                })
                .addLayer(() -> RenderType::cutoutMipped)
                .item(BlockItem::new)
                .model((ctx, prov) -> {
                    prov.withExistingParent(ctx.getName(),
                            CTNHEnergy.id("block/quantum_computer_casing_grey"));
                })
                .build()
                .register();
    }
    public static BlockEntry<Block> createCasingBlock(String name, String cnname, ResourceLocation texture) {
        return createCasingBlock(name, cnname, Block::new, texture, () -> Blocks.IRON_BLOCK,
                () -> RenderType::solid);
    }

    public static BlockEntry<Block> createCasingBlock(String name,
                                                      String cnname,
                                                      NonNullFunction<BlockBehaviour.Properties, Block> blockSupplier,
                                                      ResourceLocation texture,
                                                      NonNullSupplier<? extends Block> properties,
                                                      Supplier<Supplier<RenderType>> type) {
        return REGISTRATE.block(name, blockSupplier)
                .cnlang(cnname)
                .initialProperties(properties)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(type)
                .exBlockstate(GTModels.cubeAllModel(texture))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    public static void registerBlockEntity(BlockEntityType<QuantumComputerMENetworkPortBlockEntity> bet){
        QUANTUM_COMPUTER_ME_NETWORK_PORT.get().setBlockEntity(
                QuantumComputerMENetworkPortBlockEntity.class,
                bet,
                null, null);
    }
}
