package org.lolicode.nekomusiccli.packet;

import kotlin.NotImplementedError;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AllMusicPacketReceiver {
    private static final Identifier AllMusicPacketId = new Identifier("allmusic", "channel");

    private static void onReceive(PacketByteBuf buf) {
        throw new NotImplementedError("NOT IMPLEMENTED"); // TODO: Implement
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(AllMusicPacketId, (client, handler, buf, responseSender) -> {
            onReceive(buf);
        });
    }
}
