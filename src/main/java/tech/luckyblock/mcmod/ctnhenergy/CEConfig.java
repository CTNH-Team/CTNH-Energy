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
        @Configurable.Comment({"The maximum multiplier for automatic pattern doubling of OMNI cpu during automatic crafting.", "Default: 64"})
        @Configurable.Range(min = 1, max = Integer.MAX_VALUE)
        public int maxMultipleOMNI = 64;

        @Configurable
        @Configurable.Comment({"The maximum multiplier for automatic pattern doubling of Quantum cpu during automatic crafting.", "Default: 1024"})
        @Configurable.Range(min = 1, max = Integer.MAX_VALUE)
        public int maxMultipleQuantum = 1024;

        @Configurable
        @Configurable.Comment({"The maximum number of pattern providers can be called with 1 tick, and 0 means no restriction", "Default: 0"})
        @Configurable.Range(min = 0, max = Integer.MAX_VALUE)
        public int maxProviders = 0;
    }

    @Configurable
    @Configurable.Comment("Applied EU")
    public AppEU appeu = new AppEU();
    public static class AppEU {
        @Configurable
        @Configurable.Comment({"Amount of EU per byte can storage", "Default: 0"})
        @Configurable.Range(min = 1, max = Integer.MAX_VALUE)
        public int amountPerByte = 1024;
    }
}
