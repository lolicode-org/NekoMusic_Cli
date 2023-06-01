package org.lolicode.nekomusiccli.hud;

import net.minecraft.text.Text;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.music.MusicList;
import org.lolicode.nekomusiccli.music.MusicObj;
import org.lolicode.nekomusiccli.utils.Alert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;

public class HudUtils {
    private volatile String info = null;
    private volatile String list = null;
    private volatile LyricRender lyricRender = null;
    private volatile ImgRender imgRender = null;
    private volatile boolean isClosed = false;
    private volatile boolean isStopped = false;

    public synchronized void setMusic(MusicObj music) throws InterruptedIOException {
        if (isClosed) throw new IllegalStateException("Hud is closed");
        stopCurrentMusic();
        info = music.name == null || music.name.isBlank() ? Text.translatable("hud.nekomusic.no_title").getString() : music.name;
        info += "\n";
        info += music.ar == null || music.ar.isEmpty() ? Text.translatable("hud.nekomusic.no_artist").getString() : music.ar.get(0).name;
        info += "\n";
        info += music.album == null || music.album.name == null || music.album.name.isBlank() ? Text.translatable("hud.nekomusic.no_album").getString() : music.album.name;
        if (music.player != null && !music.player.isBlank()) info += "\nby: " + music.player;
        if (music.album != null && music.album.picUrl != null && !music.album.picUrl.isBlank()) {
            try (var imageResponse = NekoMusicClient.netUtils.getImageResponse(music.album)) {
                if (imageResponse == null || imageResponse.body() == null) throw new IOException("Failed to load image");
                var imgStream = new ByteArrayInputStream(imageResponse.body().bytes());
                imgRender = new ImgRender(imgStream, NekoMusicClient.config.enableHudImgRotate);
            } catch (InterruptedIOException e) {
                throw e;
            } catch (IOException e) {
                NekoMusicClient.LOGGER.error("Failed to load image: " + music.album.picUrl, e);
                Alert.warn("hud.nekomusic.failed_to_load_image");
            } catch (Exception e) {
                NekoMusicClient.LOGGER.error("Failed to load image: " + e.getMessage());
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
        this.list = list.toString();
    }

    public void frame() {
        if (isClosed || isStopped) return;
        var cfg = NekoMusicClient.config;
        if (!cfg.enableHud) return;
        if (cfg.enableHudImg && imgRender != null) {
            imgRender.RenderImg();
        }
        if (cfg.enableHudInfo) {
            InfoRender.render(info);
        }
        if (cfg.enableHudList) {
            ListRender.render(list);
        }
        if (cfg.enableHudLyric && lyricRender != null) {
            lyricRender.render();
        }
    }

    public synchronized void startLyric() {
        if (lyricRender != null) lyricRender.start();
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
        list = null;
        isStopped = true;
    }

    public synchronized void close() {
        stopCurrentMusic();
        isClosed = true;
    }
}
