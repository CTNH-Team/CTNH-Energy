package tech.luckyblock.mcmod.ctnhenergy.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tech.luckyblock.mcmod.ctnhenergy.CTNHLib;
import tech.luckyblock.mcmod.ctnhenergy.common.CommonProxy;

@Mod.EventBusSubscriber(modid = CTNHLib.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {
    public ClientProxy(FMLJavaModLoadingContext context) {
        super(context);
        init();
    }
    public static void init() {

    }
}
