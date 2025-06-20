package org.lolicode.nekomusiccli.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3x2fStack;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class RenderMain {
    private static final int fontHeight = Minecraft.getInstance().font.lineHeight;
    public static void drawText(GuiGraphics context, String text, float x, float y) {
        context.drawString(Minecraft.getInstance().font, text, (int) x, (int) y, 0xffffffff, false);
    }

    public static void drawMultiLineText(GuiGraphics context, String text, float x, float y) {
        if (text == null || text.isBlank()) {
            return;
        }
        int offset = 0;
        for (var line : text.split("\n")) {
            drawText(context, line, x, y + offset);
            offset += fontHeight + 2;
        }
    }

    public static void drawImg(GuiGraphics context, ResourceLocation textureId, boolean shouldRotate, int angle) {
        var config = NekoMusicClient.config;
        var imgSize = config.imgSize;
        var offset = imgSize / 2;

        Matrix3x2fStack matrices = context.pose();
        matrices.pushMatrix();

        matrices.translate(config.imgX + offset, config.imgY + offset);
        if (shouldRotate) {
            matrices.rotate(angle * ((float)Math.PI / 180F));
        }

        context.blit(RenderPipelines.GUI_TEXTURED, textureId, -offset, -offset, imgSize, imgSize, imgSize, imgSize, imgSize, imgSize);

        matrices.popMatrix();
    }
}
