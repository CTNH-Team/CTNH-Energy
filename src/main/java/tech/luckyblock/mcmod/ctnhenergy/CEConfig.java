package tech.luckyblock.mcmod.ctnhenergy;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;

@Config(id = CTNHEnergy.MODID)
public class CEConfig {
    public static CEConfig INSTANCE;
    private static final Object LOCK = new Object();

    public static void init() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = Configuration.registerConfig(CEConfig.class, ConfigFormats.yaml()).getConfigInstance();
            }
        }
    }

    @Configurable
    @Configurable.Comment("Crafting CPU")
    public CPU cpu = new CPU();
    public static class CPU {
        @Configurable
        @Configurable.Comment({"The maximum multiplier for automatic pattern doubling during automatic crafting.", "Default: 64"})
        @Configurable.Range(min = 1, max = Integer.MAX_VALUE)
        public int maxMultiple = 64;

        @Configurable
        @Configurable.Comment({"The maximum number of pattern providers can be called with 1 tick", "Default: 8192"})
        @Configurable.Range(min = 1, max = Integer.MAX_VALUE)
        public int maxProviders = 8192;
    }
}
