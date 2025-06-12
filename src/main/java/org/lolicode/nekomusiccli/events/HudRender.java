package org.lolicode.nekomusiccli.events;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import org.lolicode.nekomusiccli.NekoMusicClient;

import static org.lolicode.nekomusiccli.NekoMusicClient.hudUtils;

public class HudRender {
    public static void register() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.SUBTITLES, NekoMusicClient.MOD_BASE_IDENTIFIER.withPath("hud_element"),
                (context, tickCounter) -> {
                    if (hudUtils != null) {
                        hudUtils.frame(context);
                    }
                });
    }
}