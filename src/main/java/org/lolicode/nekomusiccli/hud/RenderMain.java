package org.lolicode.nekomusiccli.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
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

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        if (shouldRotate) {
            matrix.translationRotate(config.imgX + offset, config.imgY + offset , 0, RotationAxis.POSITIVE_Z.rotationDegrees(angle));
        } else {
            matrix.translate(config.imgX + offset, config.imgY + offset, 0);
        }

        context.drawTexture(RenderLayer::getGuiTextured, textureId, -offset, -offset, imgSize, imgSize, imgSize, imgSize, imgSize, imgSize);

        matrices.pop();
    }
}
