package org.lolicode.nekomusiccli.events;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import static org.lolicode.nekomusiccli.NekoMusicClient.hudUtils;

public class HudRender {
    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, delta) -> {
            if (hudUtils != null) {
                hudUtils.frame(drawContext);
            }
        });
    }
}
