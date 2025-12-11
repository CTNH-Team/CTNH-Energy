package tech.luckyblock.mcmod.ctnhenergy.common;

import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;
import appeng.capabilities.Capabilities;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.CEConfig;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.luckyblock.mcmod.ctnhenergy.common.me.EUKeyType;
import tech.luckyblock.mcmod.ctnhenergy.common.me.GenericStackEUStorage;
import tech.luckyblock.mcmod.ctnhenergy.data.CEDatagen;
import tech.luckyblock.mcmod.ctnhenergy.integration.jade.AdMEPatternBufferProvider;
import tech.luckyblock.mcmod.ctnhenergy.integration.jade.AdMEPatternBufferProxyProvider;
import tech.luckyblock.mcmod.ctnhenergy.registry.AEMenus;
import tech.luckyblock.mcmod.ctnhenergy.registry.CECreativeModeTabs;
import tech.luckyblock.mcmod.ctnhenergy.registry.CERecipeTypes;
import tech.luckyblock.mcmod.ctnhenergy.utils.CEUtil;
import tech.vixhentx.mcmod.ctnhlib.jade.JadePriorityManager;

@Mod.EventBusSubscriber(modid = CTNHEnergy.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonProxy {
    public CommonProxy(FMLJavaModLoadingContext context) {
        IEventBus eventBus = context.getModEventBus();
        eventBus.register(this);
        init(eventBus);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void init(IEventBus eventBus) {
        CTNHEnergy.REGISTRATE.registerRegistrate();
        AEMenus.DR.register(eventBus);
        CEConfig.init();
        CEDatagen.init();
        CECreativeModeTabs.init();
        eventBus.addGenericListener(GTRecipeType.class, CommonProxy::registerRecipeTypes);

        eventBus.addListener((RegisterEvent event) -> {
            AEKeyTypes.register(EUKeyType.INSTANCE);
        });

        GenericSlotCapacities.register(EUKeyType.INSTANCE, (long)Integer.MAX_VALUE);
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, (AttachCapabilitiesEvent<BlockEntity> event) -> {
            var blockEntity = event.getObject();
            event.addCapability(CTNHEnergy.id("generic_eu_wrapper"), new ICapabilityProvider() {
                @Override
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
                    if(capability == GTCapability.CAPABILITY_ENERGY_CONTAINER && CEUtil.canHandleEU(blockEntity, direction)){
                        return blockEntity.getCapability(Capabilities.GENERIC_INTERNAL_INV, direction)
                                .lazyMap(GenericStackEUStorage::new)
                                .cast();
                    }
                    return LazyOptional.empty();
                }
            });
        });

        JadePriorityManager.registerBlockData(new AdMEPatternBufferProvider(), BlockEntity.class, 2901, "ad_me_pattern_buffer_data");
        JadePriorityManager.registerBlockData(new AdMEPatternBufferProxyProvider(), BlockEntity.class, 3001, "ad_me_pattern_buffer_proxy_data");
        JadePriorityManager.registerBlockComponent(new AdMEPatternBufferProvider(), Block.class, 2901, "ad_me_pattern_buffer_component");
        JadePriorityManager.registerBlockComponent(new AdMEPatternBufferProxyProvider(), Block.class, 3001, "ad_me_pattern_buffer_proxy_component");

    }

    public static void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        CERecipeTypes.init();
    }

}
