package org.lolicode.nekomusiccli.packet;

import lol.bai.badpackets.api.PacketSender;
import lol.bai.badpackets.api.play.PlayPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class ClientByeSender {
    private static final Identifier CLIENT_BYE_PACKET_ID = NekoMusicClient.MOD_BASE_IDENTIFIER.withPath("client_bye");

    public static void register() {
        PlayPackets.registerServerChannel(CLIENT_BYE_PACKET_ID);
    }

    public static void send(Minecraft client) {
        if (client == null) {
            return;
        }
        PacketSender.c2s().send(CLIENT_BYE_PACKET_ID, PacketByteBufs.empty());
    }
}
