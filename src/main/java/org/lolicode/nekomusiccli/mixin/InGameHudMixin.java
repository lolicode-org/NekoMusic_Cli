package org.lolicode.nekomusiccli.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusEffectOverlay(Lnet/minecraft/client/util/math/MatrixStack;)V")})
    public void Gui(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (NekoMusicClient.hudUtilsRef.get() != null) NekoMusicClient.hudUtilsRef.get().frame();
    }
}
