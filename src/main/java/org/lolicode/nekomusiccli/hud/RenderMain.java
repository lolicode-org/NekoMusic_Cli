package org.lolicode.nekomusiccli.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import com.mojang.blaze3d.vertex.*;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class RenderMain {
    private static final int fontHeight = Minecraft.getInstance().font.lineHeight;
    public static void drawText(GuiGraphics context, String text, float x, float y) {
        context.drawString(Minecraft.getInstance().font, text, (int) x, (int) y, 0xffffff, false);
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

    public static void drawImg(DynamicTexture texture, boolean shouldRotate, int angle) {
        if (texture == null) return;
        int textureId = texture.getId();
        if (textureId <= 0) return;
        RenderSystem.setShader(CoreShaders.POSITION_TEX);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, textureId);

        PoseStack stack = new PoseStack();
        Matrix4f matrix = stack.last().pose();

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

        RenderSystem.setShader(CoreShaders.POSITION_TEX);
        BufferBuilder bufferBuilder = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix, (float) -offset, (float) offset, (float) z).setUv(u0, v1);
        bufferBuilder.addVertex(matrix, (float) offset, (float) offset, (float) z).setUv(u1, v1);
        bufferBuilder.addVertex(matrix, (float) offset, (float) -offset, (float) z).setUv(u1, v0);
        bufferBuilder.addVertex(matrix, (float) -offset, (float) -offset, (float) z).setUv(u0, v0);

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }
}
