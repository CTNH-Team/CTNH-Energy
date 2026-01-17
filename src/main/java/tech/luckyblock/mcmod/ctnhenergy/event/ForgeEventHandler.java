package tech.luckyblock.mcmod.ctnhenergy.event;


import appeng.block.crafting.PatternProviderBlock;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import com.glodblock.github.extendedae.common.blocks.BlockExPatternProvider;
import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.luckyblock.mcmod.ctnhenergy.utils.CEUtil;

@Mod.EventBusSubscriber(modid = CTNHEnergy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {
    @SubscribeEvent
    public static void onPatternProviderBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getLevel().isClientSide()) return;

        Level level = (Level) event.getLevel();
        if (level.getServer() == null) return;

        BlockPos pos = event.getPos();

        level.getServer().execute(() -> {
            BlockEntity placedBE = level.getBlockEntity(pos);
            if (placedBE == null) return;

            // 情况 1：放下的是 PatternProvider
            if (CEUtil.getUpgradeable(placedBE, Direction.UP) instanceof PatternProviderLogicHost) {
                tryConfigureNeighborMachine(level, pos);
                return;
            }

            // 情况 2：放下的是 Machine
            if (event.getPlacedBlock().getBlock() instanceof IMachineBlock machineBlock) {
                if (machineBlock.getMachine(level, pos) instanceof SimpleTieredMachine machine) {
                    tryConfigureMachineFromNeighbors(level, pos, machine);
                }
            }
        });
    }

    private static void tryConfigureNeighborMachine(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockEntity be = level.getBlockEntity(pos.relative(dir));
            if (be instanceof MetaMachineBlockEntity metaBE
                    && metaBE.getMetaMachine() instanceof SimpleTieredMachine machine) {

                configureMachine(machine, dir.getOpposite());
            }
        }
    }

    private static void tryConfigureMachineFromNeighbors(
            Level level,
            BlockPos pos,
            SimpleTieredMachine machine
    ) {
        for (Direction dir : Direction.values()) {
            BlockEntity be = level.getBlockEntity(pos.relative(dir));
            if (CEUtil.getUpgradeable(be, dir.getOpposite()) instanceof PatternProviderLogicHost) {
                configureMachine(machine, dir);
                break;
            }
        }
    }

    private static void configureMachine(SimpleTieredMachine machine, Direction outputDir) {
        machine.setAutoOutputFluids(true);
        machine.setAutoOutputItems(true);
        machine.setOutputFacingFluids(outputDir);
        machine.setOutputFacingItems(outputDir);
        machine.setAllowInputFromOutputSideItems(true);
        machine.setAllowInputFromOutputSideFluids(true);
    }



}
