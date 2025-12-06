package tech.luckyblock.mcmod.ctnhenergy.common;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tech.luckyblock.mcmod.ctnhenergy.CEConfig;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.luckyblock.mcmod.ctnhenergy.data.CEDatagen;
import tech.luckyblock.mcmod.ctnhenergy.integration.jade.AdMEPatternBufferProvider;
import tech.luckyblock.mcmod.ctnhenergy.integration.jade.AdMEPatternBufferProxyProvider;
import tech.luckyblock.mcmod.ctnhenergy.registry.AEMenus;
import tech.luckyblock.mcmod.ctnhenergy.registry.CECreativeModeTabs;
import tech.luckyblock.mcmod.ctnhenergy.registry.CERecipeTypes;
import tech.vixhentx.mcmod.ctnhlib.jade.JadePriorityManager;

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
        eventBus.addGenericListener(GTRecipeType.class, CommonProxy::registerRecipeTypes);

        JadePriorityManager.registerBlockData(new AdMEPatternBufferProvider(), BlockEntity.class, 2901, "ad_me_pattern_buffer_data");
        JadePriorityManager.registerBlockData(new AdMEPatternBufferProxyProvider(), BlockEntity.class, 3001, "ad_me_pattern_buffer_proxy_data");
        JadePriorityManager.registerBlockComponent(new AdMEPatternBufferProvider(), Block.class, 2901, "ad_me_pattern_buffer_component");
        JadePriorityManager.registerBlockComponent(new AdMEPatternBufferProxyProvider(), Block.class, 3001, "ad_me_pattern_buffer_proxy_component");

    }

    public static void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        CERecipeTypes.init();
    }
}
