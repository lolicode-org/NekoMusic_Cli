package org.lolicode.nekomusiccli.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ImgRender {
    private static final TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
    private volatile NativeImageBackedTexture texture = null;
    private Identifier textureId = null;
    private int angle = 0;
    private final long startTime = System.currentTimeMillis();
    private final boolean shouldRotate;
    public ImgRender(ByteArrayInputStream stream, boolean shouldRotate) throws IOException {
        InitImg(stream);
        this.shouldRotate = shouldRotate;
    }
    // A function that reads a ByteArrayInputStream, cuts the image into a circle,
    // draws a filled black circle outside the circle, and returns a textureId
    public synchronized void InitImg(ByteArrayInputStream stream) throws IOException {
        try (stream) {
            // Use ImageIO.read to create a bufferedimage from the bufferedinputstream
            BufferedImage bufferedImage = ImageIO.read(stream);

            // Check if the bufferedimage is null or empty
            if (bufferedImage == null || bufferedImage.getWidth() == 0 || bufferedImage.getHeight() == 0) {
                return; // Invalid input
            }

            // Get the width and height of the bufferedimage
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            int[] pixels = new int[width * height];

            ByteBuffer byteBuffer = BufferUtils.createByteBuffer(width * height * 4);

            // Check if cfg.enableHudImgRotate is true
            if (NekoMusicClient.config.enableHudImgRotate) {
                // Cut the image into a circle and draw a filled black circle outside the circle

                // Use BufferedImage.getSubimage to create a new bufferedimage with the same dimensions and config as the original
                BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

                Graphics2D g2d = img.createGraphics();

                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.drawImage(bufferedImage, (int) (width * 0.08), (int) (height * 0.08), width - (int) (width * 0.08) * 2, height - (int) (height * 0.08) * 2, null);

                Area outer = new Area(new Rectangle(0, 0, img.getWidth(), img.getHeight()));
                int x = (int) (width * 0.08);
                int y = (int) (height * 0.08);
                Ellipse2D.Double inner = new Ellipse2D.Double(x, y, width - x * 2, height - y * 2);
                outer.subtract(new Area(inner)); // remove the ellipse from the original area
                g2d.setColor(Color.BLACK);
                g2d.fill(outer);

                g2d.dispose();

                BufferedImage newImg = createCenterCut(img, new Dimension(width, height));

                newImg.getRGB(0, 0, width, height, pixels, 0, width);

            } else {
                // Don't cut the image, just use GL11.glTexImage2D to load the original bufferedimage into the texture

                // Use ImageIO.write to write the original bufferedimage to a bytearrayoutputstream
                bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);
            }

            // create a bytebuffer from the bytearrayoutputstream
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = pixels[y * width + x];
                    byteBuffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
                    byteBuffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
                    byteBuffer.put((byte) (pixel & 0xFF)); // Blue component
                    byteBuffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component. Only for RGBA
                }
            }
            byteBuffer.flip();

            createTexture(width, height, byteBuffer);
        }
    }


    public synchronized void RenderImg() {
        if (texture == null || textureId == null) return;
        RenderMain.drawImg(this.texture, this.shouldRotate, angle);
        angle = (int) ((System.currentTimeMillis() - startTime) / NekoMusicClient.config.imgRotateSpeed) % 360;
    }

    private synchronized void DisposeImg() {
        if (textureId != null) {
            textureManager.destroyTexture(textureId);
            textureId = null;
        }
        texture = null;
    }

    public void close() {
        DisposeImg();
    }

    // https://stackoverflow.com/a/70391836
    private BufferedImage createCenterCut(BufferedImage inputImage, Dimension d) {
        BufferedImage image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = (inputImage.getWidth() - d.width) / 2;
        int y = (inputImage.getHeight() - d.height) / 2;
        Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, d.width, d.height);
        g2d.setClip(shape);
        g2d.drawImage(inputImage, 0, 0, d.width, d.height, x, y, x + d.width, y + d.height, null);
        g2d.dispose();

        return image;
    }

    private synchronized void createTexture(int width, int height, ByteBuffer byteBuffer) {
        MinecraftClient.getInstance().execute(() -> {
            try (var img = new NativeImage(NativeImage.Format.RGBA, width, height, true)) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int color = byteBuffer.getInt((y * width + x) * 4);
                        img.setColor(x, y, color);
                    }
                }
                texture = new NativeImageBackedTexture(img);
                texture.setFilter(true, true);
                textureId = textureManager.registerDynamicTexture("hud_img", texture);
            }
        });
    }
}
