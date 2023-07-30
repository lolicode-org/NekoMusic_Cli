package org.lolicode.nekomusiccli.hud;

import net.minecraft.client.gui.DrawContext;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class InfoRender {
    public static void render(DrawContext context, String info) {
        RenderMain.drawMultiLineText(context, info, NekoMusicClient.config.infoX, NekoMusicClient.config.infoY);
    }
}
