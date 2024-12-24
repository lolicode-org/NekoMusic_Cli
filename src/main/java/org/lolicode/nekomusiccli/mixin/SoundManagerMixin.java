package org.lolicode.nekomusiccli.mixin;

import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
    @Inject(method = "updateSourceVolume(Lnet/minecraft/sounds/SoundSource;F)V", at = @At("HEAD"), cancellable = true)
    public void updateSoundVolume(SoundSource category, float volume, CallbackInfo ci) {
        if (category == SoundSource.RECORDS) {
            if (NekoMusicClient.pvAddon != null) {
                NekoMusicClient.pvAddon.setVolume(volume);
            } else if (NekoMusicClient.musicManager != null) {
                NekoMusicClient.musicManager.setVolume(volume);
            }
            ci.cancel();
        }
    }
}