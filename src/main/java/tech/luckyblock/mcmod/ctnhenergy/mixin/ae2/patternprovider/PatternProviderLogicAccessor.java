package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.patternprovider;

import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.util.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = PatternProviderLogic.class, remap = false)
public interface PatternProviderLogicAccessor {

    @Accessor("configManager")
    ConfigManager getConfigManager();
}
