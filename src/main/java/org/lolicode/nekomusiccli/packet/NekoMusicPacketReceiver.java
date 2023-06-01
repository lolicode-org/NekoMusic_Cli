package org.lolicode.nekomusiccli.packet;

import lol.bai.badpackets.api.S2CPacketReceiver;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.music.MusicList;
import org.lolicode.nekomusiccli.music.MusicObj;

public class NekoMusicPacketReceiver {
    private static final Identifier METADATA_PACKET_ID = NekoMusicClient.MOD_BASE_IDENTIFIER.withPath("metadata");
    private static final Identifier PLAYLIST_PACKET_ID = NekoMusicClient.MOD_BASE_IDENTIFIER.withPath("list");

    private static void onReceiveMetadata(PacketByteBuf buf, ClientPlayNetworkHandler handler) {
        AllMusicPacketReceiver.isNekoServer = true;
        if (buf == null || !NekoMusicClient.config.enabled
                || NekoMusicClient.config.bannedServers.contains(handler.getServerInfo() == null ? "" : handler.getServerInfo().address)) {
            return;
        }

        MusicObj musicObj = NekoMusicClient.GSON.fromJson(buf.readString(), MusicObj.class);
        if (musicObj == null || musicObj.url == null || musicObj.url.isEmpty()) {
            return;
        }
        NekoMusicClient.musicManager.play(musicObj);
    }

    private static void onReceivePlaylist(PacketByteBuf buf, ClientPlayNetworkHandler handler) {
        if (buf == null || !NekoMusicClient.config.enabled
                || NekoMusicClient.config.bannedServers.contains(handler.getServerInfo() == null ? "" : handler.getServerInfo().address)
                || NekoMusicClient.hudUtils == null) {
            return;
        }
        MusicList musicList = NekoMusicClient.GSON.fromJson(buf.readString(), MusicList.class);
        // playlist can be empty
        if (musicList == null) {
            musicList = new MusicList();
        }
        NekoMusicClient.hudUtils.setList(musicList);
    }

    public static void register() {
        S2CPacketReceiver.register(METADATA_PACKET_ID, (client, handler, buf, responseSender) -> onReceiveMetadata(buf, handler));
        S2CPacketReceiver.register(PLAYLIST_PACKET_ID, (client, handler, buf, responseSender) -> onReceivePlaylist(buf, handler));
    }
}
