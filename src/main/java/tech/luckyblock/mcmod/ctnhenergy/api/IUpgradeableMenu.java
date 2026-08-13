package tech.luckyblock.mcmod.ctnhenergy.api;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.ToolboxMenu;

public interface IUpgradeableMenu {

    IUpgradeInventory CE$getUpgrades();

    ToolboxMenu CE$getToolbox();
}
