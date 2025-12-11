package tech.luckyblock.mcmod.ctnhenergy.client.render;

import appeng.api.client.AEKeyRenderHandler;
import appeng.client.gui.style.Blitter;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import tech.luckyblock.mcmod.ctnhenergy.common.me.EUKey;

public class EUKeyRenderHandler implements AEKeyRenderHandler<EUKey> {

    public static final EUKeyRenderHandler INSTANCE = new EUKeyRenderHandler();

    @Override
    public void drawInGui(Minecraft minecraft, GuiGraphics guiGraphics, int x, int y, EUKey stack) {
        Blitter.sprite(Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.parse("forge:block/milk_flowing")))
                .blending(false)
                .dest(x, y, 16, 16)
                .blit(guiGraphics);
    }

    @Override
    public void drawOnBlockFace(PoseStack poseStack, MultiBufferSource buffers, EUKey what, float scale, int combinedLight, Level level) {

    }

    @Override
    public Component getDisplayName(EUKey stack) {
        return EUKey.EU_NAME;
    }
}
