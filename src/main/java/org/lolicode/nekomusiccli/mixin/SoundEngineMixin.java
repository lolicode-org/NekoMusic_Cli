package org.lolicode.nekomusiccli.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @Inject(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance sound, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        if (NekoMusicClient.musicManager != null && !NekoMusicClient.musicManager.isPlaying()) return;
        switch (sound.getSource()) {
            case RECORDS -> {
                if (NekoMusicClient.config.blockRecords) {
                    cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
                }
            }
            case MUSIC -> {
                if (NekoMusicClient.config.blockMusic) {
                    cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
                }
            }
        }
    }

    @Inject(method = "reload", at = @At("RETURN"))
    public void reload(CallbackInfo ci){
        if (NekoMusicClient.musicManager != null) NekoMusicClient.musicManager.stop();
    }

    @Inject(method = "updateCategoryVolume(Lnet/minecraft/sounds/SoundSource;)V", at = @At("HEAD"), cancellable = true)
    public void updateCategoryVolume(SoundSource category, CallbackInfo ci) {
        if (category == SoundSource.RECORDS) {
            if (NekoMusicClient.pvAddon != null) {
                NekoMusicClient.pvAddon.setVolume(Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.RECORDS));
            } else if (NekoMusicClient.musicManager != null) {
                NekoMusicClient.musicManager.setVolume(Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.RECORDS));
            }
            ci.cancel();
        }
    }
}
