package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.patternprovider;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ToolboxMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.PatternProviderMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.luckyblock.mcmod.ctnhenergy.api.IPatternProviderLogic;
import tech.luckyblock.mcmod.ctnhenergy.api.IUpgradeableMenu;
import tech.luckyblock.mcmod.ctnhenergy.common.CESettings;

@Mixin(value = PatternProviderMenu.class, remap = true)
public abstract class PatternProviderMenuMixin extends AEBaseMenu implements IPatternProviderLogic, IUpgradeableMenu {

    @Unique
    private IUpgradeableObject CE$upgradeHost;

    @Unique
    private ToolboxMenu CE$toolbox;

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

    // @SuppressWarnings("all")
    @Inject(method = "broadcastChanges",
            at = @At(value = "HEAD"),
            remap = true)
    private void broadcastChanges(CallbackInfo ci) {
        if (isServerSide()) {
            CE$blockingType = logic.getConfigManager().getSetting(CESettings.BLOCKING_TYPE);
        }
        if (CE$toolbox != null) CE$toolbox.tick();
    }

    @Redirect(method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lappeng/helpers/patternprovider/PatternProviderLogicHost;)V",
              at = @At(value = "INVOKE",
                       target = "Lappeng/helpers/patternprovider/PatternProviderLogicHost;getLogic()Lappeng/helpers/patternprovider/PatternProviderLogic;"),
              remap = false)
    private PatternProviderLogic CE$initUpgrades(PatternProviderLogicHost host) {
        CE$upgradeHost = (IUpgradeableObject) host.getLogic();
        CE$toolbox = new ToolboxMenu(this);
        setupUpgrades(CE$upgradeHost.getUpgrades());
        return host.getLogic();
    }

    @Override
    public IUpgradeInventory CE$getUpgrades() {
        return CE$upgradeHost.getUpgrades();
    }

    @Override
    public ToolboxMenu CE$getToolbox() {
        return CE$toolbox;
    }
}
