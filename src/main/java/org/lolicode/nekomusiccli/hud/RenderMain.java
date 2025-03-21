package org.lolicode.nekomusiccli.hud;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
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

    public static void drawImg(GuiGraphics context, ResourceLocation textureId, boolean shouldRotate, int angle) {
        var config = NekoMusicClient.config;
        var imgSize = config.imgSize;
        var offset = imgSize / 2;

        PoseStack matrices = context.pose();
        matrices.pushPose();
        Matrix4f matrix = matrices.last().pose();

        if (shouldRotate) {
            matrix.translationRotate(config.imgX + offset, config.imgY + offset , 0, Axis.ZP.rotationDegrees(angle));
        } else {
            matrix.translate(config.imgX + offset, config.imgY + offset, 0);
        }

        context.blit(RenderType::guiTextured, textureId, -offset, -offset, imgSize, imgSize, imgSize, imgSize, imgSize, imgSize);

        matrices.popPose();
    }
}
