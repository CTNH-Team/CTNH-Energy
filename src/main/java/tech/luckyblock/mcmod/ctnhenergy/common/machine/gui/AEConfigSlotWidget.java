package tech.luckyblock.mcmod.ctnhenergy.common.machine.gui;

import appeng.api.client.AEKeyRendering;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.integration.modules.emi.EmiStackHelper;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.lowdragmc.lowdraglib.gui.ingredient.IIngredientSlot;
import com.lowdragmc.lowdraglib.gui.util.TextFormattingUtil;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import tech.luckyblock.mcmod.ctnhenergy.api.IGhostKeyTarget;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.utils.GenericStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.lowdragmc.lowdraglib.gui.util.DrawerHelper.drawGradientRect;
import static com.lowdragmc.lowdraglib.gui.util.DrawerHelper.drawStringFixedCorner;
import static tech.luckyblock.mcmod.ctnhenergy.utils.CEDrawHelper.drawStringRightBorder;

public class AEConfigSlotWidget extends Widget implements IGhostKeyTarget, IIngredientSlot {

    protected final ConfigWidget parentWidget;
    protected final int index;
    protected final static int REMOVE_ID = 1000;
    protected final static int UPDATE_ID = 1001;
    protected final static int MIN_AMOUNT_CHANGE_ID = 1002;
    protected final static int MAX_AMOUNT_CHANGE_ID = 1003;

    @Setter
    protected boolean select = false;

    public AEConfigSlotWidget(Position pos, Size size, ConfigWidget widget, int index) {
        super(pos, size);
        this.parentWidget = widget;
        this.index = index;
    }

    public AEConfigSlotWidget(int x, int y, ConfigWidget widget, int index) {
        super(new Position(x, y), new Size(18, 18 * 2));
        this.parentWidget = widget;
        this.index = index;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
        Position position = getPosition();
        drawSlots(graphics, mouseX, mouseY, position.x, position.y, parentWidget.isAutoPull());
        if (select) {
            GuiTextures.SELECT_BOX.draw(graphics, mouseX, mouseY, position.x, position.y, 18, 18);
        }
        int stackX = position.x + 1;
        int stackY = position.y + 1;
        GenericStackHandler.Entry entry = parentWidget.getEntry(index);
        if(entry != null) {
            AEKey key = entry.key();
            int minAmount = entry.minAmount();
            int maxAmount = entry.maxAmount();
            GenericStack stack = entry.stack();
            AEKeyRendering.drawInGui(
                    Minecraft.getInstance(),
                    graphics,
                    stackX, stackY, key);
            if(minAmount != 0) {
                String minAmountStr = ">=" + TextFormattingUtil.formatLongToCompactString(minAmount, 4);
                drawStringRightBorder(graphics, minAmountStr, stackX + 16, stackY + 1, 16777215, true, 0.5f);
            }
            if(maxAmount != 0) {
                String maxAmountStr = TextFormattingUtil.formatLongToCompactString(maxAmount, 4);
                drawStringFixedCorner(graphics, maxAmountStr, stackX + 16, stackY + 16, 16777215, true, 0.5f);
            }
            if(stack != null) {
                AEKeyRendering.drawInGui(
                        Minecraft.getInstance(),
                        graphics,
                        stackX, stackY + 18, stack.what());
                String amountStr = TextFormattingUtil.formatLongToCompactString(stack.amount(), 4);
                drawStringFixedCorner(graphics, amountStr, stackX + 16, stackY + 18 + 16, 16777215, true, 0.5f);
            }
        }
        if (mouseOverConfig(mouseX, mouseY)) {
            drawSelectionOverlay(graphics, stackX, stackY, 16, 16);
        } else if (mouseOverStack(mouseX, mouseY)) {
            drawSelectionOverlay(graphics, stackX, stackY + 18, 16, 16);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void drawSlots(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, boolean autoPull) {
        if (autoPull) {
            GuiTextures.SLOT_DARK.draw(graphics, mouseX, mouseY, x, y, 18, 18);
            GuiTextures.CONFIG_ARROW.draw(graphics, mouseX, mouseY, x, y, 18, 18);
        } else {
            GuiTextures.SLOT.draw(graphics, mouseX, mouseY, x, y, 18, 18);
            GuiTextures.CONFIG_ARROW_DARK.draw(graphics, mouseX, mouseY, x, y, 18, 18);
        }
        GuiTextures.SLOT_DARK.draw(graphics, mouseX, mouseY, x, y + 18, 18, 18);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawInForeground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInForeground(graphics, mouseX, mouseY, partialTicks);
        AEKey key = this.parentWidget.getKey(this.index);
        GenericStack stack = this.parentWidget.getStack(this.index);
        if (key == null) {
            if (mouseOverConfig(mouseX, mouseY)) {
                List<Component> hoverStringList = new ArrayList<>();
                hoverStringList.add(Component.translatable("gtceu.gui.config_slot"));
                if (parentWidget.isAutoPull()) {
                    hoverStringList.add(Component.translatable("gtceu.gui.config_slot.auto_pull_managed"));
                } else {
                    hoverStringList.add(Component.translatable("gtceu.gui.config_slot.set"));
                    hoverStringList.add(Component.translatable("gtceu.gui.config_slot.scroll"));
                }
                graphics.renderTooltip(Minecraft.getInstance().font, hoverStringList, Optional.empty(), mouseX, mouseY);
            }
            return;
        }

        GenericStack hovered = null;
        if (mouseOverConfig(mouseX, mouseY)) {
            hovered = new GenericStack(key, Math.max(1, this.parentWidget.getMaxAmount(this.index)));
        } else if (mouseOverStack(mouseX, mouseY)) {
            hovered = stack;
        }
        if (hovered != null) {
            graphics.renderTooltip(Minecraft.getInstance().font, GenericStack.wrapInItemStack(hovered), mouseX, mouseY);
        }
    }

    protected boolean mouseOverConfig(double mouseX, double mouseY) {
        Position position = getPosition();
        return isMouseOver(position.x, position.y, 18, 18, mouseX, mouseY);
    }

    protected boolean mouseOverStack(double mouseX, double mouseY) {
        Position position = getPosition();
        return isMouseOver(position.x, position.y + 18, 18, 18, mouseX, mouseY);
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawSelectionOverlay(GuiGraphics graphics, int x, int y, int width, int height) {
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        drawGradientRect(graphics, x, y, width, height, -2130706433, -2130706433);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseOverConfig(mouseX, mouseY)) {
            if (parentWidget.isAutoPull()) {
                return false;
            }
            if (button == 1) {
                // Right click to clear
                writeClientAction(REMOVE_ID, buf -> {});
                parentWidget.disableAmountClient();
            } else if (button == 0) {
                ItemStack item = gui.getModularUIContainer().getCarried();
                if(!item.isEmpty()) {
                    AEKey key = getKeyFromItem(item);
                    writeClientAction(UPDATE_ID, buf -> AEKey.writeKey(buf, key));
                }
                else if(parentWidget.getKey(index) != null) {
                    parentWidget.enableAmountClient(this.index);
                    select = true;
                }
            }
        }
        return false;
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        super.handleClientAction(id, buffer);
        if(parentWidget.isAutoPull()) return;
        switch (id) {
            case REMOVE_ID -> {
                parentWidget.setKey(index, null);
                parentWidget.disableAmount();
                writeUpdateInfo(REMOVE_ID, buf -> {});
            }
            case UPDATE_ID -> {
                AEKey key = AEKey.readKey(buffer);
                if(key != null) {
                    if(!isKeyValidForSlot(key)) return;
                    parentWidget.setKey(index, key);
                    parentWidget.enableAmount(index);
                    writeUpdateInfo(UPDATE_ID, buf -> AEKey.writeKey(buf, key));
                }
            }
            case MIN_AMOUNT_CHANGE_ID -> {
                if(parentWidget.getKey(index) != null) {
                    int minAmount = buffer.readVarInt();
                    parentWidget.setMinAmount(index, minAmount);
                    writeUpdateInfo(MIN_AMOUNT_CHANGE_ID, buf -> buf.writeVarInt(minAmount));
                }
            }
            case MAX_AMOUNT_CHANGE_ID -> {
                if(parentWidget.getKey(index) != null) {
                    int maxAmount = buffer.readVarInt();
                    parentWidget.setMaxAmount(index, maxAmount);
                    writeUpdateInfo(MAX_AMOUNT_CHANGE_ID, buf -> buf.writeVarInt(maxAmount));
                }
            }
        }
    }

    @Override
    public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
        super.readUpdateInfo(id, buffer);
//        switch (id) {
//            case REMOVE_ID -> {
//                parentWidget.setKey(index, null);
//            }
//            case UPDATE_ID -> {
//                AEKey key = AEKey.readKey(buffer);
//                if(key != null) {
//                    parentWidget.setKey(index, key);
//                }
//            }
//            case MIN_AMOUNT_CHANGE_ID -> {
//                parentWidget.setMinAmount(index, buffer.readVarInt());
//            }
//            case MAX_AMOUNT_CHANGE_ID -> {
//                parentWidget.setMaxAmount(index, buffer.readVarInt());
//            }
//        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Rect2i getRectangleBox() {
        Rect2i rectangle = toRectangleBox();
        rectangle.setHeight(rectangle.getHeight() / 2);
        return rectangle;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void acceptKey(AEKey aeKey) {
        if(aeKey != null) {
            writeClientAction(UPDATE_ID, buf -> AEKey.writeKey(buf, aeKey));
        }
    }

    @Override
    public boolean mouseWheelMove(double mouseX, double mouseY, double wheelDelta) {
        var box = getRectangleBox();
        if(parentWidget.getKey(index) == null || wheelDelta == 0 || !box.contains((int) mouseX, (int) mouseY)) {
            return false;
        }
        int amount;
        amount = isCtrlDown() ? parentWidget.getMinAmount(index) : parentWidget.getMaxAmount(index);

        if(isShiftDown()) {
            amount = wheelDelta > 0 ? amount * 2 : amount / 2;
        } else {
            amount = wheelDelta > 0 ? amount + 1 : amount - 1;
        }
        if(amount >= 0) {
            int id = isCtrlDown() ? MIN_AMOUNT_CHANGE_ID : MAX_AMOUNT_CHANGE_ID;
            int finalAmount = amount;
            writeClientAction(id, buf -> buf.writeVarInt(finalAmount));
            return true;
        }
        return false;
    }

    protected boolean isKeyValidForSlot(AEKey key) {
        if (key == null ) return true;
        return parentWidget.isKeyValid(key);
    }

    protected AEKey getKeyFromItem(ItemStack stack) {
        return AEItemKey.of(stack);
    }

    @Override
    public Object getXEIIngredientOverMouse(double mouseX, double mouseY) {
        GenericStack stack = null;
        if(mouseOverConfig(mouseX, mouseY)) {
            var key = parentWidget.getKey(index);
            stack = key == null ? null : new GenericStack(key, 1);
        } else if (mouseOverStack(mouseX, mouseY)) {
            stack = parentWidget.getStack(index);
        }
        if(stack != null) {
            return EmiStackHelper.toEmiStack(stack);
        }
        return null;
    }
}
