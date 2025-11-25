package org.lolicode.nekomusiccli.hud;

import net.minecraft.client.gui.GuiGraphics;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class InfoRender {
    public static void render(GuiGraphics context, String info, int color) {
        RenderMain.drawMultiLineText(context, info, NekoMusicClient.config.infoX, NekoMusicClient.config.infoY, color);
    }
}
