package org.lolicode.nekomusiccli.music;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerException;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.hud.HudUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class MusicManager {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat(NekoMusicClient.MOD_NAME + "-Worker-%d").build());
    private volatile Future<?> future = null;
    private final AtomicReference<StreamPlayer> playerRef = new AtomicReference<>();
    private volatile boolean isPlaying = false;
    public volatile MusicObj currentMusic = null;

    public void play(MusicObj music) {
        if (this.isPlaying) {
            this.stop();
        }
        if (music == null) {
            NekoMusicClient.LOGGER.error("Music is null");
            return;
        }
        stopVanillaMusic();
        this.isPlaying = true;
        this.currentMusic = music;
        this.future = this.executorService.submit(() -> {
            try (BufferedInputStream musicStream = NekoMusicClient.netUtils.getMusicStream(music)) {
                StreamPlayer player = new StreamPlayer();
                player.setMixerName(MinecraftClient.getInstance().options.getSoundDevice().getValue());
                player.open(musicStream);
                playerRef.set(player);
                player.addStreamPlayerListener(
                        new StreamPlayerListener() {
                            @Override
                            public void opened(Object dataSource, java.util.Map<String, Object> properties) {
                            }

                            @Override
                            public void progress(int nEncodedBytes, long microsecondPosition, byte[] pcmData, java.util.Map<String, Object> properties) {
                            }

                            @Override
                            public void statusUpdated(StreamPlayerEvent event) {
                                switch (event.getPlayerStatus()) {
                                    case PLAYING -> {
                                        player.setGain(getVolume());
                                    }
                                    case STOPPED, EOM -> {
                                        cleanup();
                                    }
                                }
                            }
                        });
                player.play();
            } catch (IOException | StreamPlayerException e) {
                NekoMusicClient.LOGGER.error("Play music failed", e);
                cleanup();
            }
        });
        if (NekoMusicClient.config.enableHud) {
            if (NekoMusicClient.hudUtils == null) {
                NekoMusicClient.hudUtils = new HudUtils();
            }
            NekoMusicClient.hudUtils.setMusic(music);
        }
    }

    private void cleanup() {
        StreamPlayer player = this.playerRef.getAndSet(null);
        if (player != null) {
            player.stop();
        }
        this.isPlaying = false;
        if (this.currentMusic != null) {
            this.currentMusic = null;
        }
    }

    public void stop() {
        StreamPlayer player = this.playerRef.getAndSet(null);
        if (player != null) {
            player.stop();
        }
        if (this.future != null) {
            this.future.cancel(true);
            this.future = null;
        }
        this.isPlaying = false;
        if (this.currentMusic != null) {
            this.currentMusic = null;
        }
        if (NekoMusicClient.hudUtils != null) {
            NekoMusicClient.hudUtils.close();
        }
    }

    public void dispose() {
        this.stop();
        this.executorService.shutdownNow();
    }

    private static float getVolume() {
        return MinecraftClient.getInstance().options.getSoundVolume(CustomSoundCategory.NEKOMUSIC);
    }

    public void SetVolume(float volume) {
        StreamPlayer player = this.playerRef.get();
        if (player != null) {
            player.setGain(volume);
        }
    }

    public static void stopVanillaMusic() {
        MinecraftClient.getInstance().getSoundManager().stopSounds(null, SoundCategory.MUSIC);
        MinecraftClient.getInstance().getSoundManager().stopSounds(null, SoundCategory.RECORDS);
    }
}
