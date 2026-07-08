package tech.luckyblock.mcmod.ctnhenergy.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Objects;

import javax.annotation.Nonnull;

import static com.lowdragmc.lowdraglib.gui.util.DrawerHelper.drawStringSized;

public class CEDrawHelper {

    public static void drawStringRightBorder(@Nonnull GuiGraphics graphics, String text, float x, float y, int color,
                                             boolean dropShadow, float scale) {
        Font fontRenderer = Minecraft.getInstance().font;
        float scaledWidth = (float) fontRenderer.width(text) * scale;
        Objects.requireNonNull(fontRenderer);
        drawStringSized(graphics, text, x - scaledWidth, y, color, dropShadow, scale, false);
    }
}
