package org.lolicode.nekomusiccli.integration.plasmovoice;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import org.lolicode.nekomusiccli.NekoMusicClient;
import su.plo.voice.api.addon.AddonInitializer;
import su.plo.voice.api.addon.AddonLoaderScope;
import su.plo.voice.api.addon.InjectPlasmoVoice;
import su.plo.voice.api.addon.annotation.Addon;
import su.plo.voice.api.client.PlasmoVoiceClient;
import su.plo.voice.api.client.audio.source.LoopbackSource;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Addon(
        id = "nekomusiccli",
        name = "NekoMusic Client Integration",
        version = "1.0.0",
        authors = "KoishiMoe",
        scope = AddonLoaderScope.CLIENT
)
public final class NekoMusicPVAddon implements AddonInitializer {

    @Getter
    @InjectPlasmoVoice
    private PlasmoVoiceClient client;

    private ScheduledExecutorService scheduler = null;

    private volatile float volume = 1.0F;
    private volatile boolean anyCanHear = false;

    @Override
    public void onAddonInitialize() {
        NekoMusicClient.pvAddon = this;
    }

    @Override
    public void onAddonShutdown() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    void onJoinWorld(Minecraft client) {
        if (client.isSingleplayer()) return;
        scheduler = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setNameFormat(NekoMusicClient.MOD_NAME + " PlasmoVoice Integration").build()
        );
        scheduler.scheduleWithFixedDelay(() -> {
            anyCanHear = this.client.getSourceManager().getSources().stream().anyMatch(source -> !(source instanceof LoopbackSource) && source.canHear());
            setMusicVolume();
        }, 200, 200, TimeUnit.MILLISECONDS);
    }

    void onLeaveWorld() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
            anyCanHear = false;
            setMusicVolume();
        }
    }

    public float getVolume() {
        var config = NekoMusicClient.config;
        return anyCanHear ? ((config.useAbsoluteVolume ? 1.0F : volume) * config.volumeWhenPlayingVoice / 100.0F) : volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
        setMusicVolume();
    }

    public void setMusicVolume() {
        NekoMusicClient.musicManager.setVolume(getVolume());
    }
}
