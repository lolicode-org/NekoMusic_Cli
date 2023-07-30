package org.lolicode.nekomusiccli.hud;

import org.lolicode.nekomusiccli.NekoMusicClient;

public class ListRender {
    public static void render(String list) {
        RenderMain.drawMultiLineText(list, NekoMusicClient.config.listX, NekoMusicClient.config.listY);
    }
}
