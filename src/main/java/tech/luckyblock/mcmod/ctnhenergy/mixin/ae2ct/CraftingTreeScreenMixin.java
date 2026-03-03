package tech.luckyblock.mcmod.ctnhenergy.mixin.ae2ct;

import appeng.client.gui.AESubScreen;
import appeng.client.gui.StackWithBounds;
import appeng.client.gui.me.crafting.CraftConfirmScreen;
import appeng.menu.me.crafting.CraftConfirmMenu;
import com.neuvillette.ae2ct.gui.CraftingTreeScreen;
import com.neuvillette.ae2ct.gui.CraftingTreeWidget;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;

@Mixin(value = CraftingTreeScreen.class, remap = false)
public class CraftingTreeScreenMixin extends AESubScreen<CraftConfirmMenu, CraftConfirmScreen> {
    @Shadow
    private CraftingTreeWidget craftingTreeWidget;

    public CraftingTreeScreenMixin(CraftConfirmScreen parent, String stylePath) {
        super(parent, stylePath);
    }

    @Override
    public @Nullable StackWithBounds getStackUnderMouse(double mouseX, double mouseY) {
        var widget = (CraftingTreeWidgetAccessor)craftingTreeWidget;
        var nodeManager = widget.getNodeManager();
        Point p = widget.invokeGetMousePoint(mouseX, mouseY);
        if(nodeManager != null && nodeManager.map.containsKey(p) && nodeManager.map.get(p) != null){
            var node = nodeManager.map.get(p);
            return new StackWithBounds(node.stack, new Rect2i((int) mouseX, (int) mouseY, 1, 1));
        }
        return super.getStackUnderMouse(mouseX, mouseY);
    }
}
