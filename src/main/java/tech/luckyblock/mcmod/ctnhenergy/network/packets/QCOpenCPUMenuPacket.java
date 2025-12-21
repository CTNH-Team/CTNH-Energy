package tech.luckyblock.mcmod.ctnhenergy.network.packets;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.lowdragmc.lowdraglib.networking.IHandlerContext;
import com.lowdragmc.lowdraglib.networking.PacketIntLocation;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.port.QuantumComputerMENetworkPortBlockEntity;
import tech.luckyblock.mcmod.ctnhenergy.registry.AEMenus;

@NoArgsConstructor
public class QCOpenCPUMenuPacket extends PacketIntLocation {
    public QCOpenCPUMenuPacket(BlockPos blockPos) {
        super(blockPos);
    }

    @Override
    public void execute(IHandlerContext handler) {
        super.execute(handler);
        var level = handler.getLevel();
        var player = handler.getPlayer();
        if(player != null && level.isLoaded(pos)){
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof QuantumComputerMENetworkPortBlockEntity) {
                MenuOpener.open(
                        AEMenus.QUANTUM_COMPUTER.get(),
                        player,
                        MenuLocators.forBlockEntity(be)
                );
            }
        }
    }
}
