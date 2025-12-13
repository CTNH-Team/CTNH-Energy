package tech.luckyblock.mcmod.ctnhenergy;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import tech.luckyblock.mcmod.ctnhenergy.client.ClientProxy;
import tech.luckyblock.mcmod.ctnhenergy.common.CommonProxy;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEMachines;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEMultiblock;
import tech.luckyblock.mcmod.ctnhenergy.registry.CERegistrate;
import tech.vixhentx.mcmod.ctnhlib.langprovider.LangProcessor;

@SuppressWarnings("removal")
@Mod(CTNHEnergy.MODID)

public class CTNHEnergy {
    public static final String MODID = "ctnhenergy";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CERegistrate REGISTRATE = CERegistrate.create();

    public CTNHEnergy() {
        LangProcessor langProcessor = new LangProcessor(REGISTRATE);
        langProcessor.processAll();
        final var context = FMLJavaModLoadingContext.get();
        //noinspection InstantiationOfUtilityClass
        DistExecutor.unsafeRunForDist(() -> () -> new ClientProxy(context), () -> () -> new CommonProxy(context));

    }



    public static ResourceLocation id(String string){
        return ResourceLocation.tryBuild(MODID, string);
    }

}
