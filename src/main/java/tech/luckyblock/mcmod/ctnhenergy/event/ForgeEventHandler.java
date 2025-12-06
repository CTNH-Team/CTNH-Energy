package tech.luckyblock.mcmod.ctnhenergy.event;

import appeng.block.crafting.PatternProviderBlock;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;

@Mod.EventBusSubscriber(modid = CTNHEnergy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {
    @SubscribeEvent
    public static void onPatternProviderBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        if(event.getPlacedBlock().getBlock() instanceof PatternProviderBlock)
        {
            BlockPos placedPos = event.getPos();
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = placedPos.relative(dir);
                BlockEntity be = event.getLevel().getBlockEntity(neighborPos);
                if(be instanceof MetaMachineBlockEntity metaMachineBlockEntity
                        && metaMachineBlockEntity.getMetaMachine() instanceof SimpleTieredMachine tieredMachine)
                {
                    tieredMachine.setAutoOutputFluids(true);
                    tieredMachine.setAutoOutputItems(true);
                    tieredMachine.setOutputFacingFluids(dir.getOpposite());
                    tieredMachine.setOutputFacingItems(dir.getOpposite());
                    tieredMachine.setAllowInputFromOutputSideItems(true);
                    tieredMachine.setAllowInputFromOutputSideFluids(true);
                }
            }
        }
    }
}
