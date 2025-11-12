package tech.luckyblock.mcmod.ctnhenergy.common;

import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tech.luckyblock.mcmod.ctnhenergy.CEConfig;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.luckyblock.mcmod.ctnhenergy.data.CEDatagen;
import tech.luckyblock.mcmod.ctnhenergy.event.EventHandler;
import tech.luckyblock.mcmod.ctnhenergy.registry.AEMenus;
import tech.luckyblock.mcmod.ctnhenergy.registry.CECreativeModeTabs;

@Mod.EventBusSubscriber(modid = CTNHEnergy.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonProxy {
    public CommonProxy(FMLJavaModLoadingContext context) {
        IEventBus eventBus = context.getModEventBus();
        eventBus.register(this);
        init(eventBus);
    }
    public static void init(IEventBus eventBus) {
        CTNHEnergy.REGISTRATE.registerRegistrate();
        AEMenus.DR.register(eventBus);
        CEConfig.init();
        CEDatagen.init();
        CECreativeModeTabs.init();
        eventBus.addGenericListener(GTRecipeType.class, EventHandler::registerRecipeTypes);
    }
}
