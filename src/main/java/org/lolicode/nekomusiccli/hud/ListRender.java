package org.lolicode.nekomusiccli.hud;

import net.minecraft.client.gui.DrawContext;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class ListRender {
    public static void render(DrawContext context, String list) {
        RenderMain.drawMultiLineText(context, list, NekoMusicClient.config.listX, NekoMusicClient.config.listY);
    }
}
