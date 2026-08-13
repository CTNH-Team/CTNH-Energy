package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.patternprovider;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.api.upgrades.Upgrades;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.ToolboxPanel;
import appeng.client.gui.widgets.UpgradesPanel;
import appeng.core.localization.GuiText;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.PatternProviderMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.api.IPatternProviderLogic;
import tech.luckyblock.mcmod.ctnhenergy.api.IUpgradeableMenu;
import tech.luckyblock.mcmod.ctnhenergy.common.CESettings;

import java.util.ArrayList;

@Mixin(value = PatternProviderScreen.class, remap = false)
public abstract class PatternProviderScreenMixin<C extends PatternProviderMenu> extends AEBaseScreen<C> {

    @Unique
    private ServerSettingToggleButton<CESettings.BlockingType> CE$blockingType;

    public PatternProviderScreenMixin(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void CE$initUpgradePanel(CallbackInfo ci) {
        var providerMenu = (IUpgradeableMenu) menu;
        widgets.add("upgrades", new UpgradesPanel(menu.getSlots(SlotSemantics.UPGRADE), () -> {
            var lines = new ArrayList<Component>();
            lines.add(GuiText.CompatibleUpgrades.text());
            lines.addAll(Upgrades.getTooltipLinesForMachine(providerMenu.CE$getUpgrades().getUpgradableItem()));
            return lines;
        }));
        if (providerMenu.CE$getToolbox().isPresent()) {
            widgets.add("toolbox", new ToolboxPanel(style, providerMenu.CE$getToolbox().getName()));
        }
    }

    @Redirect(
              method = "<init>",
              at = @At(value = "INVOKE",
                       target = "Lappeng/client/gui/implementations/PatternProviderScreen;addToLeftToolbar(Lnet/minecraft/client/gui/components/Button;)Lnet/minecraft/client/gui/components/Button;",
                       ordinal = 0),
              remap = false)
    private Button init(PatternProviderScreen instance, Button button) {
        this.addToLeftToolbar(button);
        CE$blockingType = new ServerSettingToggleButton<>(CESettings.BLOCKING_TYPE, CESettings.BlockingType.DEFAULT);
        return this.addToLeftToolbar(CE$blockingType);
    }

    @Inject(method = "updateBeforeRender", at = @At("TAIL"), remap = false)
    private void updateBlockType(CallbackInfo ci) {
        CE$blockingType.set(
                ((IPatternProviderLogic) menu).CE$getBlockingMode());
    }
}
