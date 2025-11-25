package org.lolicode.nekomusiccli.hud;

import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.music.MusicList;
import org.lolicode.nekomusiccli.music.MusicObj;
import org.lolicode.nekomusiccli.utils.Alert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class HudUtils {
    private final Minecraft client = Minecraft.getInstance();
    private volatile String info = null;
    private final ArrayList<String> list = new ArrayList<>();
    private volatile LyricRender lyricRender = null;
    private volatile ImgRender imgRender = null;
    private volatile boolean isClosed = false;
    private volatile boolean isStopped = false;
    private volatile long startTime = System.currentTimeMillis();

    public synchronized void setMusic(MusicObj music) throws InterruptedIOException {
        if (isClosed) throw new IllegalStateException("Hud is closed");
        stopCurrentMusic();
        info = music.name == null || music.name.isBlank() ? Component.translatable("hud.nekomusic.no_title").getString() : music.name;
        info += "\n";
        info += music.ar == null || music.ar.isEmpty() ? Component.translatable("hud.nekomusic.no_artist").getString() : music.ar.getFirst().name;
        info += "\n";
        info += music.album == null || music.album.name == null || music.album.name.isBlank() ? Component.translatable("hud.nekomusic.no_album").getString() : music.album.name;
        if (music.player != null && !music.player.isBlank()) info += "\nby: " + music.player;
        if (music.album != null && music.album.picUrl != null && !music.album.picUrl.isBlank()) {
            try (var imageResponse = NekoMusicClient.netUtils.getImageResponse(music.album)) {
                try {
                    if (imageResponse == null || imageResponse.body() == null)
                        throw new IOException("Failed to load image");
                    var imgStream = new ByteArrayInputStream(imageResponse.body().bytes());
                    imgRender = new ImgRender(imgStream, NekoMusicClient.config.enableHudImgRotate);
                } catch (Exception e) {
                    final var defaultCover = Minecraft.getInstance().getResourceManager()
                            .getResource(Identifier.fromNamespaceAndPath(NekoMusicClient.MOD_ID, "texture/default_cover.png"));
                    if (defaultCover.isPresent()) {
                        imgRender = new ImgRender(
                                new ByteArrayInputStream(defaultCover.get().open().readAllBytes()),
                                NekoMusicClient.config.enableHudImgRotate, true);
                    }
                    throw e;
                }
            } catch (InterruptedIOException e) {
                throw e;
            } catch (ImgSizeException e) {
                NekoMusicClient.LOGGER.error("Failed to load image: " + music.album.picUrl, e);
                Alert.warn("hud.nekomusic.image_too_large");
            } catch (ImgFormatException e) {
                NekoMusicClient.LOGGER.error("Failed to load image: " + music.album.picUrl, e);
                Alert.warn("hud.nekomusic.invalid_image_format");
            } catch (IOException e) {
                NekoMusicClient.LOGGER.error("Failed to load image: " + music.album.picUrl, e);
                Alert.warn("hud.nekomusic.failed_to_load_image");
            } catch (Exception e) {
                NekoMusicClient.LOGGER.error("Failed to load image: ", e);
                Alert.warn("hud.nekomusic.failed_to_load_image");
            }
        }
        if (lyricRender != null) lyricRender.stop();
        if (music.lyric != null) {
            lyricRender = new LyricRender(music.lyric);
        } else {
            lyricRender = null;
        }
        isStopped = false;
    }

    public synchronized void setList(MusicList list) {
        if (isClosed) throw new IllegalStateException("Hud is closed");
        this.startTime = System.currentTimeMillis();
        this.list.clear();
        this.list.addAll(list.toArrayList());
    }

    public void frame(GuiGraphics context) {
        if (isClosed || isStopped || client.debugEntries.isOverlayVisible()) return;
        var cfg = NekoMusicClient.config;
        if (!cfg.enableHud) return;
        if (cfg.enableHudImg && imgRender != null) {
            imgRender.RenderImg(context);
        }
        int textColor = ARGB.color(
                cfg.textOpacity,
                cfg.textColorRed, cfg.textColorGreen, cfg.textColorBlue);
        if (cfg.enableHudInfo) {
            InfoRender.render(context, info, textColor);
        }
        if (cfg.enableHudList) {
            ListRender.render(context, list, System.currentTimeMillis() - startTime, textColor);
        }
        if (cfg.enableHudLyric && lyricRender != null) {
            lyricRender.render(context, textColor);
        }
    }

    public synchronized void startLyric(long pos) {
        if (lyricRender != null) lyricRender.start(pos);
    }

    public synchronized void stopCurrentMusic() {
        if (lyricRender != null) {
            lyricRender.stop();
            lyricRender = null;
        }
        if (imgRender != null) {
            imgRender.close();
            imgRender = null;
        }
        info = null;
//        list = null;
        isStopped = true;
    }

    public synchronized void close() {
        stopCurrentMusic();
        isClosed = true;
    }
}
