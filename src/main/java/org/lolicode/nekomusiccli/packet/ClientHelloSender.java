package org.lolicode.nekomusiccli.packet;

import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.PacketSender;
import lol.bai.badpackets.api.play.PlayPackets;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class ClientHelloSender {
    // TODO: register channel
    private static final ResourceLocation CLIENT_HELLO_PACKET_ID = NekoMusicClient.MOD_BASE_IDENTIFIER.withPath("client_hello");

    public static void register() {
        PlayPackets.registerServerChannel(CLIENT_HELLO_PACKET_ID);
    }

    public static void send(Minecraft client) {
        if (client.getCurrentServer() == null || client.getCurrentServer().isLan()) return;
        PacketSender.c2s().send(CLIENT_HELLO_PACKET_ID, new FriendlyByteBuf(Unpooled.buffer()));
    }
}
