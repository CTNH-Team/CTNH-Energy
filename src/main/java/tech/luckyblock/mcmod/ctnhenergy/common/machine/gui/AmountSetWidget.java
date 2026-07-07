package tech.luckyblock.mcmod.ctnhenergy.common.machine.gui;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

public class AmountSetWidget extends WidgetGroup {

    private static final int SYNC_INDEX = 2000;
    private static final int LABEL_X = 4;
    private static final int LABEL_WIDTH = 60;
    private static final float ROLL_SPEED = 0.7f;

    private int index = -1;
    @Getter
    private final TextFieldWidget minAmountText;
    @Getter
    private final TextFieldWidget maxAmountText;
    private final ConfigWidget parentWidget;

    @CN("最小拉取阈值:")
    @EN("Minimum Pull Threshold:")
    static Lang min;

    @CN("最大拉取数量:")
    @EN("Maximum Pull Amount:")
    static Lang max;

    public AmountSetWidget(int x, int y, ConfigWidget widget) {
        super(x, y, 114, 36);
        parentWidget = widget;
        TextTexture minLabel = createLabelTexture(() -> min.translate().getString());
        TextTexture maxLabel = createLabelTexture(() -> max.translate().getString());
        addWidget(new ImageWidget(LABEL_X, 6, LABEL_WIDTH, 10, minLabel));
        addWidget(new ImageWidget(LABEL_X, 21, LABEL_WIDTH, 10, maxLabel));
        addWidget(minAmountText = new TextFieldWidget( 68,  4, 40, 13, this::getMinAmountStr, this::setNewMinAmount)
                .setNumbersOnly(0, Integer.MAX_VALUE)
                .setMaxStringLength(10));
        addWidget(maxAmountText = new TextFieldWidget( 68,  19, 40, 13, this::getMaxAmountStr, this::setNewMaxAmount)
                .setNumbersOnly(0, Integer.MAX_VALUE)
                .setMaxStringLength(10));
    }

    private static TextTexture createLabelTexture(java.util.function.Supplier<String> labelSupplier) {
        TextTexture texture = new TextTexture(labelSupplier)
                .setColor(0x404040)
                .setDropShadow(false)
                .setType(TextTexture.TextType.ROLL)
                .setWidth(LABEL_WIDTH);
        texture.setRollSpeed(ROLL_SPEED);
        return texture;
    }

    @OnlyIn(Dist.CLIENT)
    public void setSlotIndexClient(int slotIndex) {
        index = slotIndex;
        writeClientAction(SYNC_INDEX, buf -> buf.writeVarInt(index));
    }

    public void setSlotIndex(int slotIndex) {
        index = slotIndex;
    }

    public String getMinAmountStr() {
        return index < 0 ? "0" : String.valueOf(parentWidget.getMinAmount(index));
    }

    public String getMaxAmountStr() {
        return index < 0 ? "0" : String.valueOf(parentWidget.getMaxAmount(index));
    }

    public void setNewMinAmount(String amount) {
        setNewAmount(amount, true);
    }

    public void setNewMaxAmount(String amount) {
        setNewAmount(amount, false);
    }

    private void setNewAmount(String amount, boolean minAmount) {
        try {
            int newAmount = Integer.parseInt(amount);
            if (index < 0 || parentWidget.getKey(index) == null) {
                return;
            }
            if (minAmount) {
                parentWidget.setMinAmount(index, newAmount);
            } else {
                parentWidget.setMaxAmount(index, newAmount);
            }
        } catch (NumberFormatException ignore) {}
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        super.handleClientAction(id, buffer);
        if (id == SYNC_INDEX) {
            index = buffer.readVarInt();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        Position position = getPosition();
        GuiTextures.BACKGROUND.draw(graphics, mouseX, mouseY, position.x, position.y, 114, 36);
        GuiTextures.DISPLAY.draw(graphics, mouseX, mouseY, position.x + 68, position.y + 3, 40, 14);
        GuiTextures.DISPLAY.draw(graphics, mouseX, mouseY, position.x + 68, position.y + 18, 40, 14);
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
    }
}
