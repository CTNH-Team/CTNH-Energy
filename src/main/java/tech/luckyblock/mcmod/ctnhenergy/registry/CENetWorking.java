package tech.luckyblock.mcmod.ctnhenergy.registry;

import tech.luckyblock.mcmod.ctnhenergy.network.packets.QCOpenCPUMenuPacket;

import static com.lowdragmc.lowdraglib.networking.LDLNetworking.NETWORK;

public class CENetWorking {
    public static void init(){
        NETWORK.registerC2S(QCOpenCPUMenuPacket.class);
    }
}
