package tech.luckyblock.mcmod.ctnhenergy.client;

import appeng.init.client.InitScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.luckyblock.mcmod.ctnhenergy.common.CommonProxy;
import tech.luckyblock.mcmod.ctnhenergy.registry.AEMenus;
import tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.gui.QuantumComputerScreen;

@Mod.EventBusSubscriber(modid = CTNHEnergy.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {
    public ClientProxy(FMLJavaModLoadingContext context) {
        super(context);
        init(context.getModEventBus());

    }

    public static void init(IEventBus eventBus){
        eventBus.addListener(ClientProxy::initClientAE2);
    }

    private static void initClientAE2(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                InitScreens.register(
                        AEMenus.QUANTUM_COMPUTER.get(), QuantumComputerScreen::new, "/screens/quantum_computer" + ".json");
            } catch (Throwable e) {

                throw new RuntimeException(e);
            }
        });
    }
}
