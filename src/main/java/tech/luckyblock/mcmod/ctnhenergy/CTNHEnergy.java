package tech.luckyblock.mcmod.ctnhenergy;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import tech.luckyblock.mcmod.ctnhenergy.client.ClientProxy;
import tech.luckyblock.mcmod.ctnhenergy.common.CommonProxy;
import tech.luckyblock.mcmod.ctnhenergy.registry.CERegistrate;

@SuppressWarnings("removal")
@Mod(CTNHEnergy.MODID)

public class CTNHEnergy {

    public static final String MODID = "ctnhenergy";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CERegistrate REGISTRATE = CERegistrate.create();

    public CTNHEnergy() {
        DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    }

    public static ResourceLocation id(String string) {
        return ResourceLocation.tryBuild(MODID, string);
    }
}
