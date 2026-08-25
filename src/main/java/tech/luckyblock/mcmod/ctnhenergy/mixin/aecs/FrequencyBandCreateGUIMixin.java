package tech.luckyblock.mcmod.ctnhenergy.mixin.aecs;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.client.gui.widgets.AECheckbox;
import io.github.lounode.ae2cs.client.gui.linker.broadcast.FrequencyBandCreateGUI;
import io.github.lounode.ae2cs.common.menu.linker.broadcast.FrequencyBandCreateMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.api.IFrequencyBandCreateMenu;
import tech.luckyblock.mcmod.ctnhenergy.client.FrequencyBandCreateLang;

@Mixin(value = FrequencyBandCreateGUI.class, remap = false)
public abstract class FrequencyBandCreateGUIMixin extends AEBaseScreen<FrequencyBandCreateMenu> {

    @Unique
    private AECheckbox CE$addTeamMembersToWhitelist;

    public FrequencyBandCreateGUIMixin(
            FrequencyBandCreateMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void CE$initTeamWhitelistCheckbox(CallbackInfo ci) {
        CE$addTeamMembersToWhitelist = widgets.addCheckbox(
                "ftb_team_whitelist_box",
                FrequencyBandCreateLang.ADD_TEAM_MEMBERS.translate(),
                () -> {
                });
    }

    @Inject(method = "sendConfirm", at = @At("HEAD"), remap = false)
    private void CE$sendTeamWhitelistSetting(CallbackInfo ci) {
        ((IFrequencyBandCreateMenu) getMenu())
                .CE$setAddTeamMembersToWhitelist(CE$addTeamMembersToWhitelist.isSelected());
    }

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/client/gui/style/StyleManager;loadStyleDoc(Ljava/lang/String;)Lappeng/client/gui/style/ScreenStyle;"))
    private static ScreenStyle CE$loadEnergyStyle(String path) {
        return StyleManager.loadStyleDoc("/screens/ctnhenergy/frequency_band_create_menu.json");
    }
}
