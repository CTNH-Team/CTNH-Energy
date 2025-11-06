package tech.luckyblock.mcmod.ctnhenergy.mixin;

import appeng.api.config.Setting;
import appeng.client.gui.Icon;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.localization.ButtonToolTips;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Predicate;

import static tech.luckyblock.mcmod.ctnhenergy.common.CESettings.BLOCKING_TYPE;
import static tech.luckyblock.mcmod.ctnhenergy.common.CESettings.BlockingType.*;

@Mixin(value = SettingToggleButton.class, remap = false)
public class SettingToggleButtonMixin {
    @Shadow
    private static Map<Object, Object> appearances;

    @Shadow
    private static <T extends Enum<T>> void registerApp(Icon icon, Setting<T> setting, T val, ButtonToolTips title,
                                                        Component... tooltipLines) {}

    @Inject(method = "<init>(Lappeng/api/config/Setting;Ljava/lang/Enum;Ljava/util/function/Predicate;Lappeng/client/gui/widgets/SettingToggleButton$IHandler;)V", at = @At("TAIL"))
    private void onInit(Setting setting, Enum val, Predicate isValidValue, SettingToggleButton.IHandler onPress, CallbackInfo ci) {
        // 在构造方法执行完毕后添加新的 registerApp
        if (appearances != null) {
            registerApp(Icon.CLEAR, BLOCKING_TYPE, ALL,
                    ButtonToolTips.InterfaceBlockingMode,
                    Component.literal("全部")
            );

            registerApp(Icon.BLOCKING_MODE_YES, BLOCKING_TYPE, DEFAULT,
                    ButtonToolTips.InterfaceBlockingMode,
                    Component.literal("默认")
            );

            registerApp(Icon.BLOCKING_MODE_NO, BLOCKING_TYPE, SMART,
                    ButtonToolTips.InterfaceBlockingMode,
                    Component.literal("智能")
            );
        }

    }

}
