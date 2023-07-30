package org.lolicode.nekomusiccli.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
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

    public static void drawImg(NativeImageBackedTexture texture, boolean shouldRotate, int angle) {
        if (texture == null) return;
        int textureId = texture.getGlId();
        if (textureId <= 0) return;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, textureId);

        MatrixStack stack = new MatrixStack();
        Matrix4f matrix = stack.peek().getPositionMatrix();

        int offset = NekoMusicClient.config.imgSize / 2;

        if (shouldRotate) {
            matrix = matrix.translationRotate(NekoMusicClient.config.imgX + offset, NekoMusicClient.config.imgY + offset, 0,
                    new Quaternionf().fromAxisAngleDeg(0, 0, 1, angle));
        } else {
            matrix = matrix.translation(NekoMusicClient.config.imgX + offset, NekoMusicClient.config.imgY + offset, 0);
        }

        int z = 0;
        int u0 = 0;
        float u1 = 1;
        float v0 = 0;
        float v1 = 1;

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, (float) -offset, (float) offset, (float) z).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, (float) offset, (float) offset, (float) z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, (float) offset, (float) -offset, (float) z).texture(u1, v0).next();
        bufferBuilder.vertex(matrix, (float) -offset, (float) -offset, (float) z).texture(u0, v0).next();

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }
}
