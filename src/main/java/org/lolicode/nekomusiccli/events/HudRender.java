package org.lolicode.nekomusiccli.events;

import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import org.lolicode.nekomusiccli.NekoMusicClient;

import static org.lolicode.nekomusiccli.NekoMusicClient.hudUtils;

public class HudRender {
    public static void register() {
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerAfter(
                IdentifiedLayer.MISC_OVERLAYS, NekoMusicClient.MOD_BASE_IDENTIFIER.withPath("hud_layer"),
                (drawContext, tickCounter) -> {
                    if (hudUtils != null) {
                        hudUtils.frame(drawContext);
                    }
                }));
    }
}