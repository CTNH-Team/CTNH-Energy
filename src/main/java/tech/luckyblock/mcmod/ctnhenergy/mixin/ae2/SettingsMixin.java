package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2;

import appeng.api.config.Setting;
import appeng.api.config.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.common.CESettings;

@Mixin(value = Settings.class, remap = false)
public class SettingsMixin {

    @SafeVarargs
    @Shadow
    private static <T extends Enum<T>> Setting<T> register(String name, T firstOption, T... moreOptions) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
    private static void init(CallbackInfo ci) {
        CESettings.BLOCKING_TYPE = register("blocking_type", CESettings.BlockingType.ALL, CESettings.BlockingType.DEFAULT, CESettings.BlockingType.SMART);
    }
}
