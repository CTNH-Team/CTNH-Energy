package tech.luckyblock.mcmod.ctnhenergy.common;

import appeng.api.config.Setting;

public class CESettings {
    public static Setting<BlockingType> BLOCKING_TYPE;

    public enum BlockingType {
        ALL,
        SMART,
        DEFAULT
    }
}
