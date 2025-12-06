package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.patternencodingpanel;

import appeng.client.gui.WidgetContainer;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.me.items.ProcessingEncodingPanel;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.utils.CircuitConfigManager;
import tech.luckyblock.mcmod.ctnhenergy.utils.button.Blitters;
import tech.luckyblock.mcmod.ctnhenergy.utils.button.ToggleBlitterButton;

import java.util.List;

@Mixin(value = ProcessingEncodingPanel.class, remap = false)
public abstract class ProcessingEncodingPanelMixin {

    @Unique
    private ToggleBlitterButton CE$circuitButton;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(PatternEncodingTermScreen<?> screen, WidgetContainer widgets, CallbackInfo ci) {
        CE$circuitButton = new ToggleBlitterButton(
                Blitters.CIRCUIT_ON,
                Blitters.CIRCUIT_OFF,
                CircuitConfigManager::setEnabled
        );
        CE$circuitButton.setHalfSize(true);
        CE$circuitButton.setTooltipOn(List.of(
                Component.translatable("gui.ctnhenergy.enable_circuit"),
                Component.translatable("gui.ctnhenergy.enable_circuit.tooltip").withStyle(ChatFormatting.GRAY)
        ));
        CE$circuitButton.setTooltipOff(List.of(
                Component.translatable("gui.ctnhenergy.disable_circuit"),
                Component.translatable("gui.ctnhenergy.disable_circuit.tooltip").withStyle(ChatFormatting.GRAY)
        ));
        widgets.add("enableCircuit", CE$circuitButton);
    }

    @Inject(method = "setVisible", at = @At("TAIL"))
    private void setVisible(boolean visible, CallbackInfo ci) {
        CE$circuitButton.setVisibility(visible);
    }

    @Inject(method = "updateBeforeRender", at = @At("TAIL"))
    public void updateBeforeRender(CallbackInfo ci) {
        CE$circuitButton.setState(CircuitConfigManager.enabled);
    }
}
