package org.lolicode.nekomusiccli.hud;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

public class ImgRender {
    private static final TextureManager textureManager = Minecraft.getInstance().getTextureManager();
    private volatile DynamicTexture texture = null;
    private ResourceLocation textureId = null;
    private int angle = 0;
    private final long startTime = System.currentTimeMillis();
    private final boolean shouldRotate;
    public ImgRender(ByteArrayInputStream stream, boolean shouldRotate) throws IOException {
        InitImg(stream);
        this.shouldRotate = shouldRotate;
    }
    public ImgRender(ByteArrayInputStream stream, boolean shouldRotate, boolean noWidthCheck) throws IOException {
        InitImg(stream, noWidthCheck);
        this.shouldRotate = shouldRotate;
    }
    // A function that reads a ByteArrayInputStream, cuts the image into a circle,
    // draws a filled black circle outside the circle, and returns a textureId
    public synchronized void InitImg(ByteArrayInputStream stream) throws IOException {
        InitImg(stream, false);
    }
    public synchronized void InitImg(ByteArrayInputStream stream, boolean noWidthCheck) throws IOException {
        try (stream) {
            if (!noWidthCheck) {
                try (ImageInputStream iis = ImageIO.createImageInputStream(stream)) {
                    Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
                    if (!readers.hasNext()) {
                        throw new ImgFormatException("No image reader found");
//                    return; // Invalid input
                    }
                    ImageReader reader = readers.next();
                    reader.setInput(iis, true, true);
                    if (NekoMusicClient.config.imgWidthLimit > 0 &&
                            (reader.getWidth(0) > NekoMusicClient.config.imgWidthLimit
                                    || reader.getHeight(0) > NekoMusicClient.config.imgWidthLimit)) {
//                    return; // Image too large
                        throw new ImgSizeException("Image too large");
                    }
                }
                stream.reset();
            }

            // Use ImageIO.read to create a bufferedimage from the bufferedinputstream
            BufferedImage bufferedImage = ImageIO.read(stream);

            // Check if the bufferedimage is null or empty
            if (bufferedImage == null || bufferedImage.getWidth() == 0 || bufferedImage.getHeight() == 0) {
                throw new ImgFormatException("Failed to load image");
//                return; // Invalid input
            }

            // Get the width and height of the bufferedimage
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            // if width != height, center cut the image
            if (width != height) {
                int min = Math.min(width, height);
                bufferedImage = bufferedImage.getSubimage((width - min) / 2, (height - min) / 2, min, min);
                width = height = min;
            }

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
                    byteBuffer.put((byte) (pixel & 0xFF)); // Blue component
                    byteBuffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
                    byteBuffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
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
            textureManager.release(textureId);
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
        Minecraft.getInstance().execute(() -> {
            try (var img = new NativeImage(NativeImage.Format.RGBA, width, height, true)) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int color = byteBuffer.getInt((y * width + x) * 4);
                        img.setPixel(x, y, color);
                    }
                }
                texture = new DynamicTexture(img);
                texture.setFilter(true, true);
                textureId = ResourceLocation.withDefaultNamespace("nekomusic/hud_img");
                textureManager.register(textureId, texture);
            }
        });
    }
}
