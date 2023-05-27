package org.lolicode.nekomusiccli.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class ClientHelloSender implements PacketSender{
    private final Identifier CLIENT_HELLO_PACKET_ID = NekoMusicClient.MOD_BASE_IDENTIFIER.withPath("client_hello");
    @Override
    public void send(MinecraftServer server) {
        if (server == null) {
            return;
        }
        lol.bai.badpackets.api.PacketSender.c2s().send(CLIENT_HELLO_PACKET_ID, PacketByteBufs.empty());
    }
}
