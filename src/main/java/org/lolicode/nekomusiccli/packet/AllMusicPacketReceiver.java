package org.lolicode.nekomusiccli.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.music.MusicObj;

import java.nio.charset.StandardCharsets;

public class AllMusicPacketReceiver {
    public static boolean isNekoServer = false;  // Neko server sends neko packets before allmusic packets;
    private static final Identifier AllMusicPacketId = new Identifier("allmusic", "channel");

    private static void onReceive(PacketByteBuf buf, ClientPlayNetworkHandler handler) {
        if (isNekoServer || buf == null ||
                !NekoMusicClient.config.enabled
                || NekoMusicClient.config.bannedServers.contains(handler.getServerInfo() == null ? "" : handler.getServerInfo().address))
            return;
        try {
            byte[] buff = new byte[buf.readableBytes()];
            buf.readBytes(buff);
            buff[0] = 0;
            String data = new String(buff, StandardCharsets.UTF_8).substring(1);
            if (data.isBlank()) return;
            if (data.startsWith("[Stop]")) {
                if (NekoMusicClient.musicManager != null) NekoMusicClient.musicManager.stop();
            } else if (data.startsWith("[Play]")) {
                if (NekoMusicClient.musicManager != null) {
                    NekoMusicClient.musicManager.play(new MusicObj() {
                        {
                            url = data.substring(6);
                        }
                    });
                }
            }
        } catch (Exception e) {
            NekoMusicClient.LOGGER.error("Error while receiving allmusic packet: " + e.getMessage());
        }
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(AllMusicPacketId, (client, handler, buf, responseSender) -> {
            onReceive(buf, handler);
        });
    }
}
