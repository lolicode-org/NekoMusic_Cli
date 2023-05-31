package org.lolicode.nekomusiccli.music;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.hud.HudUtils;
import org.lolicode.nekomusiccli.music.player.AudioPlayer;
import org.lolicode.nekomusiccli.utils.Alert;

import java.io.ByteArrayInputStream;
import java.io.InterruptedIOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class MusicManager {
    private final AtomicReference<AudioPlayer> playerRef = new AtomicReference<>();
    private volatile boolean isPlaying = false;
    public volatile MusicObj currentMusic = null;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat(NekoMusicClient.MOD_NAME + " Music-Manager").build());
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(NekoMusicClient.MOD_NAME + " Music-Manager-Scheduler").build());
    private final BlockingDeque<Future<?>> futures = new LinkedBlockingDeque<>();
    private volatile boolean isDisposed = false;

    public synchronized void play(MusicObj music) {
        if (this.isDisposed) {
            NekoMusicClient.LOGGER.error("MusicManager is disposed");
            Alert.error("player.nekomusic.manager.disposed");
            return;
        }
        if (this.isPlaying) {
            this.stop();
        }
        if (music == null) {
            NekoMusicClient.LOGGER.error("Music is null");
            Alert.error("player.nekomusic.music.null");
            return;
        }
        stopVanillaMusic();
        this.isPlaying = true;
        this.currentMusic = music;

        this.futures.add(this.executor.submit(() -> {
            try {
                if (NekoMusicClient.hudUtils == null) NekoMusicClient.hudUtils = new HudUtils();
                NekoMusicClient.hudUtils.setMusic(music);
            } catch (InterruptedIOException e) {
                return;
            } catch (Exception e) {
                NekoMusicClient.LOGGER.error("Failed to update hud", e);
                Alert.error("hud.nekomusic.update.failed");
            }
            try (ByteArrayInputStream stream = NekoMusicClient.netUtils.getMusicStream(music)) {
                if (stream == null) {
                    NekoMusicClient.LOGGER.error("Failed to get music stream");
                    Alert.error("player.nekomusic.stream.failed");
                    return;
                }
                AudioPlayer player = new AudioPlayer(stream);
                playerRef.set(player);
                if (NekoMusicClient.hudUtils != null) NekoMusicClient.hudUtils.startLyric();  // sync lyric with music
                player.play();
                player.setGain(getVolume());
            } catch (InterruptedIOException ignored) {
                // it'll be interrupted when stop, dont call stop again
            } catch (Exception e) {
                NekoMusicClient.LOGGER.error("Failed to play music", e);
                Alert.error("player.nekomusic.play.failed");
                stop();
            }
        }));

        this.futures.add(this.scheduler.schedule(() -> {
            if (this.isPlaying) {
                this.stop();
            }
        }, music.dt, TimeUnit.MILLISECONDS));
    }

    public synchronized void stop() {
        AudioPlayer player = this.playerRef.getAndSet(null);
        this.futures.forEach(f -> {
            if (!f.isDone()) {
                f.cancel(true);
            }
        });
        this.futures.clear();
        if (player != null) {
            player.stop();
        }
        this.isPlaying = false;
        if (this.currentMusic != null) {
            this.currentMusic = null;
        }
        if (NekoMusicClient.hudUtils != null) {
            NekoMusicClient.hudUtils.stopCurrentMusic();
        }
    }

    public synchronized void dispose() {
        this.stop();
        this.executor.shutdownNow();
        this.scheduler.shutdownNow();
        this.isDisposed = true;
    }

    public static float getVolume() {
        return MinecraftClient.getInstance().options.getSoundVolume(SoundCategory.RECORDS);
    }

    public void setVolume(float volume) {
        AudioPlayer player = this.playerRef.get();
        if (player != null) {
            try {
                player.setGain(volume);
            } catch (Exception e) {
                NekoMusicClient.LOGGER.error("Failed to set volume", e);
                Alert.error("volume.nekomusic.set.failed");
            }
        }
    }

    public static void stopVanillaMusic() {
        MinecraftClient.getInstance().getSoundManager().stopSounds(null, SoundCategory.MUSIC);
        MinecraftClient.getInstance().getSoundManager().stopSounds(null, SoundCategory.RECORDS);
    }
}
