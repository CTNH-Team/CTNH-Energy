package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.misc;

import appeng.api.upgrades.MachineUpgradesChanged;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "appeng.api.upgrades.MachineUpgradeInventory", remap = false)
public interface MachineUpgradeInventoryAccessor {
    @Accessor("changeCallback")
    MachineUpgradesChanged getChangeCallback();

    @Accessor("changeCallback")
    @Mutable
    void setChangeCallback(MachineUpgradesChanged upgradesChanged);
}
