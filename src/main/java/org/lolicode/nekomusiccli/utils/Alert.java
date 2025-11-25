package org.lolicode.nekomusiccli.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class Alert {
    public static void error(String msgKey) {
        if (Minecraft.getInstance().player == null) return;
        Component msg = Component.literal("§c[" + NekoMusicClient.MOD_NAME + "] ").append(Component.translatable(msgKey));
        Minecraft.getInstance().player.displayClientMessage(msg, false);
    }

    public static void warn(String msgKey) {
        if (Minecraft.getInstance().player == null) return;
        Component msg = Component.literal("§e[" + NekoMusicClient.MOD_NAME + "] ").append(Component.translatable(msgKey));
        Minecraft.getInstance().player.displayClientMessage(msg, false);
    }

    public static void info(String msgKey) {
        if (Minecraft.getInstance().player == null) return;
        Component msg = Component.literal("§b[" + NekoMusicClient.MOD_NAME + "] ").append(Component.translatable(msgKey));
        Minecraft.getInstance().player.displayClientMessage(msg, false);
    }

    public static void info(String msgKey, Object... args) {
        if (Minecraft.getInstance().player == null) return;
        Component msg = Component.literal("§b[" + NekoMusicClient.MOD_NAME + "] ").append(Component.translatable(msgKey, args));
        Minecraft.getInstance().player.displayClientMessage(msg, false);
    }
}
