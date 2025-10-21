package tech.luckyblock.mcmod.ctnhenergy;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import tech.luckyblock.mcmod.ctnhenergy.client.ClientProxy;
import tech.luckyblock.mcmod.ctnhenergy.common.CommonProxy;

@SuppressWarnings("removal")
@Mod(CTNHEnergy.MODID)
public class CTNHEnergy {
    public static final String MODID = "ctnhenergy";

    public static final Logger LOGGER = LogUtils.getLogger();

    public CTNHEnergy() {
        final var context = FMLJavaModLoadingContext.get();
        //noinspection InstantiationOfUtilityClass
        DistExecutor.unsafeRunForDist(() -> () -> new ClientProxy(context), () -> () -> new CommonProxy(context));

    }


}
