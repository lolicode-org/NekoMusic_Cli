package org.lolicode.nekomusiccli.hud;

import kotlin.NotImplementedError;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ImgRender implements AutoCloseable {
    private int textureId;
    private int angle = 0;
    private final long startTime = System.currentTimeMillis();
    private final boolean shouldRotate;
    public ImgRender(BufferedInputStream stream, boolean shouldRotate) throws IOException {
        this.textureId = InitImg(stream);
        this.shouldRotate = shouldRotate;
    }
    // A function that reads a bufferedinputstream, cuts the image into a circle,
    // draws a filled black circle outside the circle, and returns a textureId
    public int InitImg(BufferedInputStream stream) throws IOException {
        // Use ImageIO.read to create a bufferedimage from the bufferedinputstream
        BufferedImage bufferedImage = ImageIO.read(stream);

        // Check if the bufferedimage is null or empty
        if (bufferedImage == null || bufferedImage.getWidth() == 0 || bufferedImage.getHeight() == 0) {
            return -1; // Invalid input
        }

        // Use GL.createCapabilities to create an OpenGL context for the current thread
        GL.createCapabilities();

        // Use GL11.glGenTextures to generate a new textureId
        int textureId = GL11.glGenTextures();

        // Check if the textureId is valid
        if (textureId == 0) {
            return -1; // Failed to generate texture
        }

        // Use GL11.glBindTexture to bind the textureId to the GL_TEXTURE_2D target
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        // Use GL11.glTexParameteri to set the texture parameters for minification and magnification filters
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        // Check if cfg.enableHudImgRotate is true
        if (NekoMusicClient.config.enableHudImgRotate) {
            // Cut the image into a circle and draw a filled black circle outside the circle

            // Get the width and height of the bufferedimage
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            // Use BufferedImage.getSubimage to create a new bufferedimage with the same dimensions and config as the original
            BufferedImage circularBufferedImage = bufferedImage.getSubimage(0, 0, width, height);

            // Create a graphics2d object from the new bufferedimage
            Graphics2D g2d = circularBufferedImage.createGraphics();

            // Create a color object with black color and anti-aliasing enabled
            Color color = new Color(0, 0, 0, 255); // Use 255 for alpha to make it opaque

            // Draw a filled black circle on the graphics2d object covering the entire bufferedimage
            g2d.setColor(color);
            g2d.fill(new Ellipse2D.Float(0, 0, width, height));

            // Create a shape object to define the circular shape of the original image
            Shape shape = new Ellipse2D.Float(width / 2f - Math.min(width, height) / 2f, height / 2f - Math.min(width, height) / 2f, Math.min(width, height), Math.min(width, height));

            // Clip the graphics2d object with the shape
            g2d.setClip(shape);

            // Draw the original bufferedimage on the graphics2d object using the color object with SRC_IN mode
            g2d.setComposite(AlphaComposite.SrcIn);
            g2d.drawImage(bufferedImage, 0, 0, null);

            // Use ImageIO.write to write the circular bufferedimage to a bytearrayoutputstream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(circularBufferedImage, "png", baos);

            // Use ByteBuffer.wrap to create a bytebuffer from the bytearrayoutputstream
            ByteBuffer byteBuffer = ByteBuffer.wrap(baos.toByteArray());

            // Use GL11.glTexImage2D to load the bytebuffer into the texture
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, byteBuffer);
        } else {
            // Don't cut the image, just use GL11.glTexImage2D to load the original bufferedimage into the texture

            // Use ImageIO.write to write the original bufferedimage to a bytearrayoutputstream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);

            // Use ByteBuffer.wrap to create a bytebuffer from the bytearrayoutputstream
            ByteBuffer byteBuffer = ByteBuffer.wrap(baos.toByteArray());

            // Use GL11.glTexImage2D to load the bytebuffer into the texture
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, bufferedImage.getWidth(), bufferedImage.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, byteBuffer);
        }

        // Return the textureId
        return textureId;
    }


    public void RenderImg() {
        RenderMain.drawImg(this.textureId, this.shouldRotate, angle);
        angle = (int) ((System.currentTimeMillis() - startTime) / NekoMusicClient.config.imgRotateSpeed) % 360;
    }

    private synchronized void DisposeImg() {
        var tempTextureId = this.textureId;
        this.textureId = -1;
        GL11.glDeleteTextures(tempTextureId);
    }

    @Override
    public void close() {
        DisposeImg();
    }
}
