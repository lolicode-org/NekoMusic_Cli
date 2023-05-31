package org.lolicode.nekomusiccli.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class RenderMain {
    private static final MatrixStack stack = new MatrixStack();
    public static void drawText(String text, float x, float y) {
        MinecraftClient.getInstance().textRenderer.draw(stack, text, x, y, 0xffffff);
    }

    public static void drawImg(int textureId, boolean shouldRotate, int angle) {
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
