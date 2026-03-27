package org.lolicode.nekomusiccli.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class InfoRender {
    public static void render(GuiGraphicsExtractor context, String info, int color) {
        RenderMain.drawMultiLineText(context, info, NekoMusicClient.config.infoX, NekoMusicClient.config.infoY, color);
    }
}
