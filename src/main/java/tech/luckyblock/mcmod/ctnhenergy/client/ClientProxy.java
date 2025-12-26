package tech.luckyblock.mcmod.ctnhenergy.client;

import appeng.api.client.AEKeyRendering;
import appeng.init.client.InitScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.luckyblock.mcmod.ctnhenergy.client.render.EUKeyRenderHandler;
import tech.luckyblock.mcmod.ctnhenergy.common.CommonProxy;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKey;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKeyType;
import tech.luckyblock.mcmod.ctnhenergy.registry.AEMenus;
import tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.gui.QuantumComputerScreen;

import static com.glodblock.github.extendedae.common.EPPItemAndBlock.INFINITY_CELL;
import static tech.luckyblock.mcmod.ctnhenergy.registry.CEItems.DYNAMO_CARD;

@Mod.EventBusSubscriber(modid = CTNHEnergy.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {
    @SuppressWarnings("removal")
    public ClientProxy() {
        super();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        init(modEventBus);
    }

    public static void init(IEventBus eventBus) {
        eventBus.addListener(ClientProxy::initClientAE2);
        eventBus.addListener(ClientProxy::onClientSetup);
    }

    private static void initClientAE2(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                InitScreens.register(
                        AEMenus.QUANTUM_COMPUTER.get(), QuantumComputerScreen::new, "/screens/quantum_computer" + ".json");
                AEKeyRendering.register(EUKeyType.INSTANCE, EUKey.class, EUKeyRenderHandler.INSTANCE);
            } catch (Throwable e) {

                throw new RuntimeException(e);
            }
        });
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // 注册内容类型谓词
            ItemProperties.register(
                    INFINITY_CELL.asItem(),
                    CTNHEnergy.id("cell_content"),
                    (stack, level, entity, seed) -> {
                        if (stack.hasTag()) {
                            var tag = stack.getTag();
                            if (tag != null && tag.contains("record")) {
                                var record = tag.getCompound("record");
                                if (record.contains("id")) {
                                    String contentType = record.getString("id");
                                    if ("minecraft:water".equals(contentType)) {
                                        return 0.1F;
                                    } else if ("minecraft:cobblestone".equals(contentType)) {
                                        return 0.2F;
                                    }
                                }
                            }
                        }
                        return 0.0F; // 默认值
                    }
            );

            ItemProperties.register(
                    DYNAMO_CARD.get(),
                    CTNHEnergy.id("voltage"),
                    (stack, level, entity, seed) -> {
                        var tag = stack.getTag();
                        return tag != null ? tag.getInt("voltage") : 0;
                    }
            );
        });
    }
}
