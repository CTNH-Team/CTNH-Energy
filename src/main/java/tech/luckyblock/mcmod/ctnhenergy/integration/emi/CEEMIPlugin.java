package tech.luckyblock.mcmod.ctnhenergy.integration.emi;

import com.mojang.logging.LogUtils;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import org.slf4j.Logger;

public class CEEMIPlugin implements EmiPlugin {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void register(EmiRegistry registry) {
        //registry.addRecipeHandler();
    }
}
