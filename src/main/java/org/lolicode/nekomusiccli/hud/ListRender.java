package org.lolicode.nekomusiccli.hud;

import org.lolicode.nekomusiccli.NekoMusicClient;

public class ListRender {
    public static void render(String list) {
        int offset = 0;
        for (var line : list.split("\n")) {
            RenderMain.drawText(line, NekoMusicClient.config.listX, NekoMusicClient.config.listY + offset);
            offset += 10;
        }
    }
}
