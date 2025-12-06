package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.patternprovider;

import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.PatternProviderMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.common.CESettings;
import tech.luckyblock.mcmod.ctnhenergy.api.IPatternProviderLogic;

@Mixin(value = PatternProviderMenu.class, remap = true)
public abstract class PatternProviderMenuMixin extends AEBaseMenu implements IPatternProviderLogic {
    @Unique
    @GuiSync(8)
    private CESettings.BlockingType CE$blockingType = CESettings.BlockingType.DEFAULT;

    @Final
    @Shadow(remap = false)
    protected PatternProviderLogic logic;

    public PatternProviderMenuMixin(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    @Override
    public CESettings.BlockingType CE$getBlockingMode() {
        return CE$blockingType;
    }

    //@SuppressWarnings("all")
    @Inject(method = "broadcastChanges",
            at = @At(value = "HEAD"),
            remap = true
    )
    private void broadcastChanges(CallbackInfo ci) {
        if (isServerSide()) {
            CE$blockingType = logic.getConfigManager().getSetting(CESettings.BLOCKING_TYPE);
        }
    }
}
