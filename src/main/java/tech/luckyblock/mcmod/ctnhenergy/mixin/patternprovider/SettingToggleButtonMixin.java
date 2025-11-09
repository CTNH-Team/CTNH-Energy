package tech.luckyblock.mcmod.ctnhenergy.mixin.patternprovider;

import appeng.api.config.Setting;
import appeng.client.gui.Icon;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.localization.ButtonToolTips;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;

import static tech.luckyblock.mcmod.ctnhenergy.common.CESettings.BLOCKING_TYPE;
import static tech.luckyblock.mcmod.ctnhenergy.common.CESettings.BlockingType.*;

@Mixin(value = SettingToggleButton.class, remap = false)
public class SettingToggleButtonMixin {
    @Unique
    private static Component CE$title = null;

    @Shadow
    private static Map<Object, Object> appearances;

    @Inject(
            method = "registerApp(Lappeng/client/gui/Icon;Lappeng/api/config/Setting;Ljava/lang/Enum;Lappeng/core/localization/ButtonToolTips;[Lnet/minecraft/network/chat/Component;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER)
    )
    private static <T extends Enum<T>> void registerApp(Icon icon, Setting<T> setting, T val, ButtonToolTips title, Component[] tooltipLines, CallbackInfo ci,
                                                        @Local(name = "lines") ArrayList<Component> lines) {
        if (CE$title == null) return;
        lines.clear();
        lines.add(CE$title.copy());
    }

    @Shadow
    private static <T extends Enum<T>> void registerApp(Icon icon, Setting<T> setting, T val, ButtonToolTips title,
                                                        Component... tooltipLines) {}

    @Inject(method = "<init>(Lappeng/api/config/Setting;Ljava/lang/Enum;Ljava/util/function/Predicate;Lappeng/client/gui/widgets/SettingToggleButton$IHandler;)V", at = @At("TAIL"))
    private void onInit(Setting setting, Enum val, Predicate isValidValue, SettingToggleButton.IHandler onPress, CallbackInfo ci) {
        // 在构造方法执行完毕后添加新的 registerApp
        if (appearances != null) {
            CE$title = Component.translatable("gui.ctnhenergy.blocking_type.title");
            registerApp(Icon.CLEAR, BLOCKING_TYPE, ALL,
                    ButtonToolTips.InterfaceBlockingMode,
                    Component.translatable("gui.ctnhenergy.blocking_type.all"),
                    Component.translatable("gui.ctnhenergy.blocking_type.all.details")
            );

            registerApp(Icon.BLOCKING_MODE_YES, BLOCKING_TYPE, DEFAULT,
                    ButtonToolTips.InterfaceBlockingMode,
                    Component.translatable("gui.ctnhenergy.blocking_type.default"),
                    Component.translatable("gui.ctnhenergy.blocking_type.default.details")
            );

            registerApp(Icon.BLOCKING_MODE_NO, BLOCKING_TYPE, SMART,
                    ButtonToolTips.InterfaceBlockingMode,
                    Component.translatable("gui.ctnhenergy.blocking_type.smart"),
                    Component.translatable("gui.ctnhenergy.blocking_type.smart.details.1"),
                    Component.translatable("gui.ctnhenergy.blocking_type.smart.details.2")
            );
        }

    }

}
