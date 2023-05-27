package org.lolicode.nekomusiccli.hud;

import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.config.ModConfig;

public class InfoRender {
    public static void render(String info) {
        if (info == null || info.isBlank()) {
            return;
        }
        int offset = 0;
        for (var line : info.split("\n")) {
            RenderMain.drawText(line, NekoMusicClient.config.infoX, NekoMusicClient.config.infoY + offset);
            offset += 10;
        }
    }
}
