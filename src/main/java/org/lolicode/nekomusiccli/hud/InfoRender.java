package org.lolicode.nekomusiccli.hud;

import org.lolicode.nekomusiccli.NekoMusicClient;

public class InfoRender {
    public static void render(String info) {
        RenderMain.drawMultiLineText(info, NekoMusicClient.config.infoX, NekoMusicClient.config.infoY);
    }
}
