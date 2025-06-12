package org.lolicode.nekomusiccli.mixin;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.config.CustomSoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance sound, CallbackInfoReturnable<SoundSystem.PlayResult> cir) {
        if (NekoMusicClient.musicManager != null && !NekoMusicClient.musicManager.isPlaying()) return;
        switch (sound.getCategory()) {
            case RECORDS -> {
                if (NekoMusicClient.config.blockRecords) cir.setReturnValue(SoundSystem.PlayResult.NOT_STARTED);
            }
            case MUSIC -> {
                if (NekoMusicClient.config.blockMusic) cir.setReturnValue(SoundSystem.PlayResult.NOT_STARTED);
            }
        }
    }

    @Inject(method = "reloadSounds", at = @At("RETURN"))
    public void reload(CallbackInfo ci){
        if (NekoMusicClient.musicManager != null) NekoMusicClient.musicManager.stop();
    }

    @Inject(method = "updateSoundVolume(Lnet/minecraft/sound/SoundCategory;F)V", at = @At("HEAD"), cancellable = true)
    public void updateSoundVolume(SoundCategory category, float volume, CallbackInfo ci){
        if (category == CustomSoundCategory.NEKOMUSIC) {
            if (NekoMusicClient.pvAddon != null) {
                NekoMusicClient.pvAddon.setVolume(volume);
            } else if (NekoMusicClient.musicManager != null) {
                NekoMusicClient.musicManager.setVolume(volume);
            }
            ci.cancel();
        }
    }
}
