package tech.luckyblock.mcmod.ctnhenergy.registry;

import tech.luckyblock.mcmod.ctnhenergy.network.packets.HighlightBlocksPacket;
import tech.luckyblock.mcmod.ctnhenergy.network.packets.LocatePatternProviderPacket;
import tech.luckyblock.mcmod.ctnhenergy.network.packets.QCOpenCPUMenuPacket;

import static com.lowdragmc.lowdraglib.networking.LDLNetworking.NETWORK;

public class CENetWorking {

    public static void init() {
        NETWORK.registerC2S(QCOpenCPUMenuPacket.class);
        NETWORK.registerC2S(LocatePatternProviderPacket.class);
        NETWORK.registerS2C(HighlightBlocksPacket.class);
    }
}
