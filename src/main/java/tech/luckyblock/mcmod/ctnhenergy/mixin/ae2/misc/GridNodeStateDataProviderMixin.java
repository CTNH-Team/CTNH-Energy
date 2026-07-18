package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.misc;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import appeng.integration.modules.igtooltip.GridNodeState;
import appeng.integration.modules.igtooltip.blocks.GridNodeStateDataProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.MEPartMachine;

@Mixin(value = GridNodeStateDataProvider.class, remap = false)
public class GridNodeStateDataProviderMixin {

    @Inject(method = "provideServerData(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/nbt/CompoundTag;)V",
            at = @At("TAIL"))
    void injectGTMachine(Player player, BlockEntity object, CompoundTag serverData, CallbackInfo ci) {
        if (object instanceof MetaMachineBlockEntity mbe &&
                mbe.getMetaMachine() instanceof MEPartMachine mePart) {
            var state = GridNodeState.fromNode(mePart.getActionableNode());
            serverData.putByte("gridNodeState", (byte) state.ordinal());
        }
    }
}
