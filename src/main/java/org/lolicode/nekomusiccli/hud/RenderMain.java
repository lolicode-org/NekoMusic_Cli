package org.lolicode.nekomusiccli.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class RenderMain {
    private static final int fontHeight = MinecraftClient.getInstance().textRenderer.fontHeight;

    public static void drawText(DrawContext context, String text, float x, float y) {
        context.drawText(MinecraftClient.getInstance().textRenderer, text, (int) x, (int) y, 0xffffff, false);
    }

    public static void drawMultiLineText(DrawContext context, String text, float x, float y) {
        if (text == null || text.isBlank()) {
            return;
        }
        int offset = 0;
        for (var line : text.split("\n")) {
            drawText(context, line, x, y + offset);
            offset += fontHeight + 2;
        }
    }

    public static void drawImg(DrawContext context, Identifier textureId, boolean shouldRotate, int angle) {
        var config = NekoMusicClient.config;
        var imgSize = config.imgSize;
        var offset = imgSize / 2;

        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();

        matrices.translate(config.imgX + offset, config.imgY + offset);
        if (shouldRotate) {
            matrices.rotate(angle);
        }

        context.drawTexture(RenderPipelines.GUI_TEXTURED, textureId, -offset, -offset, imgSize, imgSize, imgSize, imgSize, imgSize, imgSize);

        matrices.popMatrix();
    }
}
