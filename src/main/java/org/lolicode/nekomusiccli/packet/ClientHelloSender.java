package org.lolicode.nekomusiccli.packet;

import lol.bai.badpackets.api.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class ClientHelloSender {
    private static final Identifier CLIENT_HELLO_PACKET_ID = NekoMusicClient.MOD_BASE_IDENTIFIER.withPath("client_hello");

    public static void send(MinecraftClient client) {
        if (client.getCurrentServerEntry() == null || client.getCurrentServerEntry().isLocal()) return;
        PacketSender.c2s().send(CLIENT_HELLO_PACKET_ID, PacketByteBufs.empty());
    }
}
