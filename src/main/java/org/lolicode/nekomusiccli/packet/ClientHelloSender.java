package org.lolicode.nekomusiccli.packet;

import lol.bai.badpackets.api.PacketSender;
import lol.bai.badpackets.api.play.PlayPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class ClientHelloSender {
    // TODO: register channel
    private static final Identifier CLIENT_HELLO_PACKET_ID = NekoMusicClient.MOD_BASE_IDENTIFIER.withPath("client_hello");

    public static void register() {
        PlayPackets.registerServerChannel(CLIENT_HELLO_PACKET_ID);
    }

    public static void send(Minecraft client) {
        if (client.getCurrentServer() == null || client.getCurrentServer().isLan()) return;
        PacketSender.c2s().send(CLIENT_HELLO_PACKET_ID, PacketByteBufs.empty());
    }
}
