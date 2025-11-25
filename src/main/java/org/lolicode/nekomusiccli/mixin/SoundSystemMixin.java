package org.lolicode.nekomusiccli.mixin;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.config.CustomSoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public class SoundSystemMixin {
    @Inject(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance sound, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        if (NekoMusicClient.musicManager != null && !NekoMusicClient.musicManager.isPlaying()) return;
        switch (sound.getSource()) {
            case RECORDS -> {
                if (NekoMusicClient.config.blockRecords) cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
            }
            case MUSIC -> {
                if (NekoMusicClient.config.blockMusic) cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
            }
        }
    }

    @Inject(method = "reload", at = @At("RETURN"))
    public void reload(CallbackInfo ci){
        if (NekoMusicClient.musicManager != null) NekoMusicClient.musicManager.stop();
    }

    @Inject(method = "updateCategoryVolume(Lnet/minecraft/sounds/SoundSource;F)V", at = @At("HEAD"), cancellable = true)
    public void updateSoundVolume(SoundSource category, float volume, CallbackInfo ci){
        volume = Mth.clamp(volume, 0.0F, 1.0F);
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
