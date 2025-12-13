package tech.luckyblock.mcmod.ctnhenergy.utils;

import appeng.api.parts.IPartHost;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.helpers.InterfaceLogicHost;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.parts.AEBasePart;
import appeng.util.inv.AppEngInternalInventory;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.utils.GTUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import tech.luckyblock.mcmod.ctnhenergy.common.me.MEMachineEUHandler;

import java.util.List;

public class CEUtil {
    public static IUpgradeableObject getUpgradeable(BlockEntity be, Direction side){
        if(be instanceof InterfaceBlockEntity || be instanceof PatternProviderBlockEntity)

            return be instanceof IUpgradeableObject upgradeable ? upgradeable : null;

        if(be instanceof IPartHost host
                && (host.getPart(side) instanceof InterfaceLogicHost || host.getPart(side) instanceof PatternProviderLogicHost)){
            return host.getPart(side) instanceof IUpgradeableObject upgradeable ? upgradeable : null;
        }

        return null;
    }

    public static boolean isInSameGrid(IEnergyContainer a, IEnergyContainer b){
        return a instanceof MEMachineEUHandler handlerA
                && b instanceof MEMachineEUHandler handlerB
                && handlerA.getNode().getGrid() == handlerB.getNode().getGrid();
    }

    public static List<Direction> getSides(Object host) {
        if (host instanceof BlockEntity) {
            return List.of(GTUtil.DIRECTIONS);
        } else if (host instanceof AEBasePart part) {
            if (part.getSide() == null) {
                return List.of();
            }
            return List.of(part.getSide());
        } else {
            return List.of();
        }
    }
}
