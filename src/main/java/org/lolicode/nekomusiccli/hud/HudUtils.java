package org.lolicode.nekomusiccli.hud;

import net.minecraft.text.Text;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.music.MusicList;
import org.lolicode.nekomusiccli.music.MusicObj;

import java.io.IOException;

public class HudUtils {
    private volatile String info = null;
    private volatile String list = null;
    private volatile LyricRender lyricRender = null;
    private volatile ImgRender imgRender = null;
    private volatile boolean isClosed = false;

    public synchronized void setMusic(MusicObj music) {
        close();
        info = music.name == null || music.name.isBlank() ? Text.translatable("hud.nekomusic.no_title").getString() : music.name;
        info += "\n";
        info += music.ar == null || music.ar.isEmpty() ? Text.translatable("hud.nekomusic.no_artist").getString() : music.ar.get(0).name;
        info += "\n";
        info += music.album == null || music.album.name == null || music.album.name.isBlank() ? Text.translatable("hud.nekomusic.no_album").getString() : music.album.name;
        if (music.player != null && !music.player.isBlank()) info += "\nby: " + music.player;
        if (music.album != null && music.album.picUrl != null && !music.album.picUrl.isBlank()) {
            try (var imageStream = NekoMusicClient.netUtils.getImageStream(music.album)) {
                imgRender = new ImgRender(imageStream, NekoMusicClient.config.enableHudImgRotate);
            } catch (IOException e) {
                NekoMusicClient.LOGGER.error("Failed to load image: " + music.album.picUrl);
            } catch (Exception e) {
                NekoMusicClient.LOGGER.error("Failed to load image: " + e.getMessage());
            }
        }
        if (lyricRender != null) lyricRender.stop();
        if (music.lyric != null) {
            lyricRender = new LyricRender(music.lyric);
            lyricRender.start();
        } else {
            lyricRender = null;
        }
        isClosed = false;
    }

    public synchronized void setList(MusicList list) {
        this.list = list.toString();
    }

    public void frame() {
        if (isClosed) return;
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

    public synchronized void close() {
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
        isClosed = true;
    }
}
