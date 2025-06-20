package org.lolicode.nekomusiccli.hud;

import net.minecraft.client.gui.GuiGraphics;
import org.lolicode.nekomusiccli.NekoMusicClient;

import java.util.ArrayList;

public class ListRender {
    private static final StringBuilder sb = new StringBuilder((NekoMusicClient.config.maxCharPerLineListHud + 1) * NekoMusicClient.config.maxRowListHud);

    public static void render(GuiGraphics context, ArrayList<String> list, long timeDelta, int color) {
        if (list == null || list.isEmpty()) {
            return;
        }
        var currentPos = (int) (timeDelta / 500);
        var maxLen = NekoMusicClient.config.maxCharPerLineListHud;
        sb.setLength(0);
        for (int i = 1; i <= list.size(); i++) {
            var line = list.get(i - 1);
            if (maxLen > 0 && line.length() > maxLen) {
                var start = NekoMusicClient.config.scrollListHud ? ( currentPos % (line.length() - maxLen + 1)) : 0;
                sb.append(i).append(". ").append(line, start, Math.min(start + maxLen, line.length())).append('\n');
            } else {
                sb.append(i).append(". ").append(line).append('\n');
            }
            if (NekoMusicClient.config.maxRowListHud > 0 && i >= NekoMusicClient.config.maxRowListHud) {
                break;
            }
        }
        RenderMain.drawMultiLineText(context, sb.toString(), NekoMusicClient.config.listX, NekoMusicClient.config.listY, color);
    }
}
