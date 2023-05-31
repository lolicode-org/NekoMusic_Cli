package org.lolicode.nekomusiccli.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class Alert {
    public static void error(String msgKey) {
        if (MinecraftClient.getInstance().player == null) return;
        Text msg = Text.literal("§c[" + NekoMusicClient.MOD_NAME + "] ").append(Text.translatable(msgKey));
        MinecraftClient.getInstance().player.sendMessage(msg, false);
    }

    public static void warn(String msgKey) {
        if (MinecraftClient.getInstance().player == null) return;
        Text msg = Text.literal("§e[" + NekoMusicClient.MOD_NAME + "] ").append(Text.translatable(msgKey));
        MinecraftClient.getInstance().player.sendMessage(msg, false);
    }

    public static void info(String msgKey) {
        if (MinecraftClient.getInstance().player == null) return;
        Text msg = Text.literal("§b[" + NekoMusicClient.MOD_NAME + "] ").append(Text.translatable(msgKey));
        MinecraftClient.getInstance().player.sendMessage(msg, false);
    }
}
