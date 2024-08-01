package org.lolicode.nekomusiccli.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class AllMusicPacketReceiver {
    private record AllMusicPackPayload() implements CustomPayload {
        private static final Id<AllMusicPackPayload> ID = new CustomPayload.Id<>(AllMusicPacketId);
        private static final PacketCodec<PacketByteBuf, AllMusicPackPayload> CODEC = PacketCodec.of(
                (value, buf) -> {},
                (buf) -> new AllMusicPackPayload()
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
    private static final Identifier AllMusicPacketId = Identifier.of("allmusic", "channel");

    public static void register() {
        if (!FabricLoader.getInstance().isModLoaded("allmusic_client")) {
            PayloadTypeRegistry.playS2C().register(AllMusicPackPayload.ID, AllMusicPackPayload.CODEC);
            ClientPlayNetworking.registerGlobalReceiver(AllMusicPackPayload.ID, (pack, handler) -> {}); // throttle allmusic packets to avoid annoying log
        }
    }
}
