package org.lolicode.nekomusiccli.hud;

import net.minecraft.client.gui.DrawContext;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class InfoRender {
    public static void render(DrawContext context, String info) {
        if (info == null || info.isBlank()) {
            return;
        }
        int offset = 0;
        for (var line : info.split("\n")) {
            RenderMain.drawText(context, line, NekoMusicClient.config.infoX, NekoMusicClient.config.infoY + offset);
            offset += 10;
        }
    }
}
