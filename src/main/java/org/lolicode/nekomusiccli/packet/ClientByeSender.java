package org.lolicode.nekomusiccli.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class ClientByeSender implements PacketSender{
    private final Identifier CLIENT_BYE_PACKET_ID = NekoMusicClient.MOD_BASE_IDENTIFIER.withPath("client_bye");
    @Override
    public void send(MinecraftServer server) {
        if (server == null) {
            return;
        }
        lol.bai.badpackets.api.PacketSender.c2s().send(CLIENT_BYE_PACKET_ID, PacketByteBufs.empty());
    }
}
