package org.lolicode.nekomusiccli.packet;

import lol.bai.badpackets.api.play.PlayPackets;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.music.MusicList;
import org.lolicode.nekomusiccli.music.MusicObj;
import org.lolicode.nekomusiccli.utils.Alert;

public class ServerPacketReceiver {
    private static final ResourceLocation METADATA_PACKET_ID = NekoMusicClient.MOD_BASE_IDENTIFIER.withPath("metadata");
    private static final ResourceLocation PLAYLIST_PACKET_ID = NekoMusicClient.MOD_BASE_IDENTIFIER.withPath("list");

    private static void onReceiveMetadata(FriendlyByteBuf buf, ClientPacketListener handler) {
        if (buf == null || !NekoMusicClient.config.enabled
                || NekoMusicClient.config.bannedServers.contains(handler.getServerData() == null ? "" : handler.getServerData().ip)) {
            NekoMusicClient.musicManager.stop();
            return;
        }

        MusicObj musicObj = NekoMusicClient.GSON.fromJson(buf.readUtf(), MusicObj.class);
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

    private static void onReceivePlaylist(FriendlyByteBuf buf, ClientPacketListener handler) {
        if (buf == null || !NekoMusicClient.config.enabled
                || NekoMusicClient.config.bannedServers.contains(handler.getServerData() == null ? "" : handler.getServerData().ip)
                || NekoMusicClient.hudUtils == null) {
            return;
        }
        MusicList musicList = NekoMusicClient.GSON.fromJson(buf.readUtf(), MusicList.class);
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
