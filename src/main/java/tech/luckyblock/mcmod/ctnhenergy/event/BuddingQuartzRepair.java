package tech.luckyblock.mcmod.ctnhenergy.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import appeng.core.definitions.AEBlocks;
import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftbchunks.api.Protection;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;

@Mod.EventBusSubscriber(modid = CTNHEnergy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BuddingQuartzRepair {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        var player = event.getEntity();
        var held = event.getItemStack();
        if (player.isShiftKeyDown() || player.isSpectator() || !player.getAbilities().mayBuild ||
                event.getUseBlock() == Event.Result.DENY || event.getUseItem() == Event.Result.DENY ||
                !(held.getItem() instanceof BlockItem blockItem)) return;

        var level = event.getLevel();
        var pos = event.getPos();
        var target = level.getBlockState(pos).getBlock();
        if (target != blockItem.getBlock()) return;
        var next = nextTier(target);
        if (next == null || !level.mayInteract(player, pos)) return;

        // Consume the interaction on both sides so the client doesn't predict placement or try the other hand.
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (ModList.get().isLoaded("ftbchunks") && Claims.preventEdit(serverPlayer, event.getHand(), pos)) {
            event.setCancellationResult(InteractionResult.FAIL);
            return;
        }

        boolean upgrade = level.getRandom().nextBoolean();
        if (upgrade && !level.setBlockAndUpdate(pos, next.defaultBlockState())) return;
        if (!player.getAbilities().instabuild) held.shrink(1);
        level.playSound(null, pos, upgrade ? SoundEvents.AMETHYST_BLOCK_CHIME : SoundEvents.AMETHYST_BLOCK_HIT,
                SoundSource.BLOCKS, 0.7F, 1.0F);
    }

    private static Block nextTier(Block block) {
        if (block == AEBlocks.QUARTZ_BLOCK.block()) return AEBlocks.DAMAGED_BUDDING_QUARTZ.block();
        if (block == AEBlocks.DAMAGED_BUDDING_QUARTZ.block()) return AEBlocks.CHIPPED_BUDDING_QUARTZ.block();
        if (block == AEBlocks.CHIPPED_BUDDING_QUARTZ.block()) return AEBlocks.FLAWED_BUDDING_QUARTZ.block();
        if (block == AEBlocks.FLAWED_BUDDING_QUARTZ.block()) return AEBlocks.FLAWLESS_BUDDING_QUARTZ.block();
        return null;
    }

    private static class Claims {

        private static boolean preventEdit(ServerPlayer player, InteractionHand hand, BlockPos pos) {
            var api = FTBChunksAPI.api();
            return api.isManagerLoaded() &&
                    api.getManager().shouldPreventInteraction(player, hand, pos, Protection.EDIT_BLOCK, null);
        }
    }
}
