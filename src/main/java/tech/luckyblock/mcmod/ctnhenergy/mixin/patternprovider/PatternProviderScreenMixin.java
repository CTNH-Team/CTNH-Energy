package tech.luckyblock.mcmod.ctnhenergy.mixin.patternprovider;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.menu.implementations.PatternProviderMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.common.CESettings;
import tech.luckyblock.mcmod.ctnhenergy.utils.IPatternProviderLogic;

@Mixin(value = PatternProviderScreen.class, remap = false)
public abstract class PatternProviderScreenMixin <C extends PatternProviderMenu> extends AEBaseScreen<C> {

    @Unique
    private ServerSettingToggleButton<CESettings.BlockingType> CE$blockingType;

    public PatternProviderScreenMixin(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL"),
            remap = false
    )
    private void init(PatternProviderMenu menu, Inventory playerInventory, Component title, ScreenStyle style, CallbackInfo ci) {
        CE$blockingType =  new ServerSettingToggleButton<>(CESettings.BLOCKING_TYPE, CESettings.BlockingType.DEFAULT);
        this.addToLeftToolbar(CE$blockingType);
    }

    @Inject(method = "updateBeforeRender", at = @At("TAIL"), remap = false)
    private void updateBlockType(CallbackInfo ci) {
        CE$blockingType.set(
                ((IPatternProviderLogic)menu).CE$getBlockingMode()
        );
    }
}