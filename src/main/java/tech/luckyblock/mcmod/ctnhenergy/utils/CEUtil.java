package tech.luckyblock.mcmod.ctnhenergy.utils;

import appeng.api.parts.IPartHost;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.helpers.InterfaceLogicHost;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CEUtil {
    public static boolean canHandleEU(BlockEntity be, Direction side){
        if(be instanceof InterfaceBlockEntity || be instanceof PatternProviderBlockEntity)
            return true;

        if(be instanceof IPartHost host){
            return host.getPart(side) instanceof InterfaceLogicHost
                    || host.getPart(side) instanceof PatternProviderLogicHost;
        }

        return false;
    }
}
