package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2.menu;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.parts.IPart;
import appeng.menu.AEBaseMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AEBaseMenu.class, remap = false)
public interface AEBaseMenuAccessor {
    @Accessor("part")
    IPart getPart();

    @Accessor("itemMenuHost")
    ItemMenuHost getItemMenuHost();

}
