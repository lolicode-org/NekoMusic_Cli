package org.lolicode.nekomusiccli.packet;

import lol.bai.badpackets.api.play.PlayPackets;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.music.MusicList;
import org.lolicode.nekomusiccli.music.MusicObj;
import org.lolicode.nekomusiccli.utils.Alert;

public class NekoMusicPacketReceiver {
    private static final Identifier METADATA_PACKET_ID = NekoMusicClient.MOD_BASE_IDENTIFIER.withPath("metadata");
    private static final Identifier PLAYLIST_PACKET_ID = NekoMusicClient.MOD_BASE_IDENTIFIER.withPath("list");

    private static void onReceiveMetadata(PacketByteBuf buf, ClientPlayNetworkHandler handler) {
        if (buf == null || !NekoMusicClient.config.enabled
                || NekoMusicClient.config.bannedServers.contains(handler.getServerInfo() == null ? "" : handler.getServerInfo().address)) {
            NekoMusicClient.musicManager.stop();
            return;
        }

        MusicObj musicObj = NekoMusicClient.GSON.fromJson(buf.readString(), MusicObj.class);
        if (musicObj == null || musicObj.url == null || musicObj.url.isEmpty()) {
            NekoMusicClient.musicManager.stop();
            return;
        }
        if (NekoMusicClient.config.bannedSongs.stream().anyMatch(banned -> banned.id == musicObj.id)) {
            NekoMusicClient.LOGGER.info("Banned song: {} ({})", musicObj.name, musicObj.id);
            Alert.info("player.nekomusic.song.banned", musicObj.name, musicObj.id);
            NekoMusicClient.musicManager.stop();
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
        PlayPackets.registerClientChannel(METADATA_PACKET_ID);
        PlayPackets.registerClientReceiver(METADATA_PACKET_ID, (context, buf) -> onReceiveMetadata(buf, context.handler()));

        PlayPackets.registerClientChannel(PLAYLIST_PACKET_ID);
        PlayPackets.registerClientReceiver(PLAYLIST_PACKET_ID, (context, buf) -> onReceivePlaylist(buf, context.handler()));
    }
}
