package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2ct;

import com.neuvillette.ae2ct.api.CraftingTreeHelper;
import com.neuvillette.ae2ct.gui.CraftingTreeWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.awt.*;

@Mixin(value = CraftingTreeWidget.class, remap = false)
public interface CraftingTreeWidgetAccessor {

    @Accessor(value = "_nodeManager")
    CraftingTreeHelper.NodeManager getNodeManager();

    @Invoker
    Point invokeGetMousePoint(double xCoord, double yCoord);
}
