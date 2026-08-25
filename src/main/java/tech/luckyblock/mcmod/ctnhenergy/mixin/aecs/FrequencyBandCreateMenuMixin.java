package tech.luckyblock.mcmod.ctnhenergy.mixin.aecs;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import io.github.lounode.ae2cs.api.linker.broadcast.BroadcastFrequencyBand;
import io.github.lounode.ae2cs.api.linker.broadcast.FrequencyBandManager;
import io.github.lounode.ae2cs.common.menu.linker.broadcast.FrequencyBandCreateMenu;
import appeng.menu.AEBaseMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.api.IFrequencyBandCreateMenu;

@Mixin(value = FrequencyBandCreateMenu.class, remap = false)
public abstract class FrequencyBandCreateMenuMixin extends AEBaseMenu implements IFrequencyBandCreateMenu {

    @Unique
    private boolean CE$addTeamMembersToWhitelist;

    public FrequencyBandCreateMenuMixin(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void CE$registerTeamWhitelistAction(CallbackInfo ci) {
        registerClientAction(
                "ctnhenergy_set_team_whitelist",
                Boolean.class,
                enabled -> CE$addTeamMembersToWhitelist = enabled);
    }

    @Override
    public void CE$setAddTeamMembersToWhitelist(boolean enabled) {
        sendClientAction("ctnhenergy_set_team_whitelist", enabled);
    }

    @Redirect(
            method = "createBand",
            at = @At(
                    value = "INVOKE",
                    target = "Lio/github/lounode/ae2cs/api/linker/broadcast/FrequencyBandManager;tryCreateBand(Ljava/lang/String;Ljava/lang/String;Ljava/util/UUID;ZZ)Lio/github/lounode/ae2cs/api/linker/broadcast/BroadcastFrequencyBand;"))
    private BroadcastFrequencyBand addTeamMembersToWhitelist(
            String bandName,
            String password,
            UUID ownerId,
            boolean isPublic,
            boolean allowedMemoryCardCopy) {
        boolean bandPresent = FrequencyBandManager.isBandPresent(bandName);
        BroadcastFrequencyBand band = FrequencyBandManager.tryCreateBand(
                bandName, password, ownerId, isPublic, allowedMemoryCardCopy);
        if (!bandPresent
                && band != null
                && CE$addTeamMembersToWhitelist
                && FTBTeamsAPI.api() != null
                && FTBTeamsAPI.api().isManagerLoaded()) {
            FTBTeamsAPI.api().getManager().getTeamForPlayerID(ownerId).ifPresent(team ->
                    team.getMembers().forEach(band::addToWhiteList));
        }
        return band;
    }
}
