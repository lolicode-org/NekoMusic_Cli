package org.lolicode.nekomusiccli.mixin;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance sound, CallbackInfo ci) {
        switch (sound.getCategory()) {
            case RECORDS, MUSIC -> ci.cancel();
        }
    }

    @Inject(method = "reloadSounds", at = @At("RETURN"))
    public void reload(CallbackInfo ci){
        if (NekoMusicClient.musicManager != null) NekoMusicClient.musicManager.stop();
    }

    @Inject(method = "stopAll", at = @At("RETURN"))
    public void stopAll(CallbackInfo ci){
        if (NekoMusicClient.musicManager != null) NekoMusicClient.musicManager.stop();
    }

    @Inject(method = "updateSoundVolume(Lnet/minecraft/sound/SoundCategory;F)V", at = @At("HEAD"), cancellable = true)
    public void updateSoundVolume(SoundCategory category, float volume, CallbackInfo ci){
        if (category == SoundCategory.RECORDS && NekoMusicClient.musicManager != null) {
            NekoMusicClient.musicManager.setVolume(volume);
            ci.cancel();
        }
    }
}
