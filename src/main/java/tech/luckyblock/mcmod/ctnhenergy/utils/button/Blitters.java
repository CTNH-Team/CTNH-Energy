package tech.luckyblock.mcmod.ctnhenergy.utils.button;

import appeng.client.gui.style.Blitter;
import appeng.core.AppEng;
import net.minecraft.resources.ResourceLocation;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;

public class Blitters {
    public static final ResourceLocation TEXTURE = CTNHEnergy.id("textures/guis/cetexture.png");
    public static final int TEXTURE_WIDTH = 256;
    public static final int TEXTURE_HEIGHT = 256;

    public static Blitter CIRCUIT_ON = createBlitter(0, 0);
    public static Blitter CIRCUIT_OFF = createBlitter(16, 0);

    public static Blitter createBlitter(int x, int y) {
        return createBlitter(x, y, 16, 16);
    }

    public static Blitter createBlitter(int x, int y, int width, int height) {
        return Blitter.texture(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT)
                .src(x, y, width, height);
    }
}
