package org.lolicode.nekomusiccli.mixin;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @Inject(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance sound, CallbackInfo ci) {
        if (NekoMusicClient.musicManager != null && !NekoMusicClient.musicManager.isPlaying()) return;
        switch (sound.getSource()) {
            case RECORDS -> {
                if (NekoMusicClient.config.blockRecords) ci.cancel();
            }
            case MUSIC -> {
                if (NekoMusicClient.config.blockMusic) ci.cancel();
            }
        }
    }

    @Inject(method = "reload", at = @At("RETURN"))
    public void reload(CallbackInfo ci){
        if (NekoMusicClient.musicManager != null) NekoMusicClient.musicManager.stop();
    }
}
