package org.lolicode.nekomusiccli.packet;

import kotlin.NotImplementedError;
import lol.bai.badpackets.api.S2CPacketReceiver;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class NekoMusicPacketReceiver {
    private static final Identifier METADATA_PACKET_ID = NekoMusicClient.MOD_BASE_IDENTIFIER.withPath("metadata");
    private static final Identifier PLAYLIST_PACKET_ID = NekoMusicClient.MOD_BASE_IDENTIFIER.withPath("list");

    private static void onReceiveMetadata(PacketByteBuf buf) {
        throw new NotImplementedError("NOT IMPLEMENTED"); // TODO: Implement
    }

    private static void onReceivePlaylist(PacketByteBuf buf) {
        throw new NotImplementedError("NOT IMPLEMENTED"); // TODO: Implement
    }

    public static void register() {
        S2CPacketReceiver.register(METADATA_PACKET_ID, (client, handler, buf, responseSender) -> {
            onReceiveMetadata(buf);
        });
        S2CPacketReceiver.register(PLAYLIST_PACKET_ID, (client, handler, buf, responseSender) -> {
            onReceivePlaylist(buf);
        });
    }
}
