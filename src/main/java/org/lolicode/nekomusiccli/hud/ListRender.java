package org.lolicode.nekomusiccli.hud;

import net.minecraft.client.gui.DrawContext;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class ListRender {
    public static void render(DrawContext context, String list) {
        if (list == null || list.isBlank()) {
            return;
        }
        int offset = 0;
        for (var line : list.split("\n")) {
            RenderMain.drawText(context, line, NekoMusicClient.config.listX, NekoMusicClient.config.listY + offset);
            offset += 10;
        }
    }
}
