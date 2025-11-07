package tech.luckyblock.mcmod.ctnhenergy.utils;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.client.TooltipsHandler;
import com.gregtechceu.gtceu.integration.ae2.gui.widget.list.AEListGridWidget;
import com.gregtechceu.gtceu.utils.GTMath;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.side.fluid.forge.FluidHelperImpl;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.fluids.FluidStack;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gregtechceu.gtceu.integration.ae2.gui.widget.slot.AEConfigSlotWidget.drawSelectionOverlay;
import static com.lowdragmc.lowdraglib.gui.util.DrawerHelper.drawItemStack;
import static com.lowdragmc.lowdraglib.gui.util.DrawerHelper.drawText;

/**
 * Display a certain {@link GenericStack} element (automatically handles both items and fluids).
 */
public class AEGenericDisplayWidget extends Widget {

    private final AEListGridWidget gridWidget;
    private final int index;

    public AEGenericDisplayWidget(int x, int y, AEListGridWidget gridWidget, int index) {
        super(new Position(x, y), new Size(18, 18));
        this.gridWidget = gridWidget;
        this.index = index;
    }

    @Override
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
        Position position = getPosition();
        GenericStack stack = this.gridWidget.getAt(this.index);

        if (stack != null) {
            // Determine display type based on AEKey type
            boolean isFluid = isFluidStack(stack);

            // Draw appropriate slot background
            if (isFluid) {
                GuiTextures.FLUID_SLOT.draw(graphics, mouseX, mouseY, position.x, position.y, 18, 18);
            } else {
                GuiTextures.SLOT.draw(graphics, mouseX, mouseY, position.x, position.y, 18, 18);
            }

            GuiTextures.NUMBER_BACKGROUND.draw(graphics, mouseX, mouseY, position.x + 18, position.y, 140, 18);

            int stackX = position.x + 1;
            int stackY = position.y + 1;

            String amountStr = String.format("x%,d", stack.amount());
            drawText(graphics, amountStr, stackX + 20, stackY + 5, 1, 0xFFFFFFFF);

            // Draw content based on type
            if (isFluid) {
                drawFluidContent(graphics, stack, stackX, stackY);
            } else {
                drawItemContent(graphics, stack, stackX, stackY);
            }

            if (isMouseOverElement(mouseX, mouseY)) {
                drawSelectionOverlay(graphics, stackX, stackY, 16, 16);
            }
        }
    }

    private boolean isFluidStack(GenericStack stack) {
        return stack.what() instanceof AEFluidKey;
    }

    private void drawItemContent(@NotNull GuiGraphics graphics, GenericStack item, int stackX, int stackY) {
        ItemStack realStack = item.what() instanceof AEItemKey key ? new ItemStack(key.getItem()) : ItemStack.EMPTY;
        drawItemStack(graphics, realStack, stackX, stackY, -1, null);
        String amountStr = String.format("x%,d", item.amount());
        drawText(graphics, amountStr, stackX + 20, stackY + 5, 1, 0xFFFFFFFF);
    }

    private void drawFluidContent(@NotNull GuiGraphics graphics, GenericStack fluid, int stackX, int stackY) {
        FluidStack fluidStack = fluid.what() instanceof AEFluidKey key ?
                new FluidStack(key.getFluid(), GTMath.saturatedCast(fluid.amount()), key.getTag()) :
                FluidStack.EMPTY;
        DrawerHelper.drawFluidForGui(graphics, FluidHelperImpl.toFluidStack(fluidStack), fluid.amount(), stackX,
                stackY, 16, 16);
        String amountStr = String.format("x%,d", fluid.amount());
        drawText(graphics, amountStr, stackX + 20, stackY + 5, 1, 0xFFFFFFFF);
    }

    @Override
    public void drawInForeground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (isMouseOverElement(mouseX, mouseY)) {
            GenericStack stack = this.gridWidget.getAt(this.index);
            if (stack != null) {
                if (isFluidStack(stack)) {
                    showFluidTooltip(graphics, stack, mouseX, mouseY);
                } else {
                    showItemTooltip(graphics, stack, mouseX, mouseY);
                }
            }
        }
    }

    private void showItemTooltip(@NotNull GuiGraphics graphics, GenericStack stack, int mouseX, int mouseY) {
        graphics.renderTooltip(Minecraft.getInstance().font, GenericStack.wrapInItemStack(stack), mouseX, mouseY);
    }

    private void showFluidTooltip(@NotNull GuiGraphics graphics, GenericStack stack, int mouseX, int mouseY) {
        if (stack.what() instanceof AEFluidKey key) {
            FluidStack fluidStack = new FluidStack(key.getFluid(), GTMath.saturatedCast(stack.amount()), key.getTag());
            List<Component> tooltips = new ArrayList<>();
            tooltips.add(fluidStack.getDisplayName());
            tooltips.add(Component.literal(String.format("%,d mB", stack.amount())));
            TooltipsHandler.appendFluidTooltips(fluidStack, tooltips::add, TooltipFlag.NORMAL);
            graphics.renderTooltip(Minecraft.getInstance().font, tooltips, Optional.empty(), mouseX, mouseY);
        }
    }
}