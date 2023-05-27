package org.lolicode.nekomusiccli.mixin;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
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
        // TODO
    }

    @Inject(method = "stopAll", at = @At("RETURN"))
    public void stopAll(CallbackInfo ci){
        // TODO
    }

    @Inject(method = "Lnet/minecraft/client/sound/SoundSystem;updateSoundVolume(Lnet/minecraft/sound/SoundCategory;F)V", at = @At("HEAD"), cancellable = true)
    public void updateSoundVolume(CallbackInfo ci){
        // TODO
    }
}
