package tech.luckyblock.mcmod.ctnhenergy.common;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.luckyblock.mcmod.ctnhenergy.data.CEDatagen;

@Mod.EventBusSubscriber(modid = CTNHEnergy.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonProxy {
    public CommonProxy(FMLJavaModLoadingContext context) {
        IEventBus eventBus = context.getModEventBus();
        eventBus.register(this);
        init();
    }
    public static void init() {
        CTNHEnergy.REGISTRATE.registerRegistrate();

        CEDatagen.init();
    }
}
