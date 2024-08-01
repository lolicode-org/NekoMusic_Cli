package org.lolicode.nekomusiccli.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class AllMusicPacketReceiver {
    private record AllMusicPackPayload() implements CustomPayload {
        private static final Id<AllMusicPackPayload> ID = new CustomPayload.Id<>(AllMusicPacketId);

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
    private static final Identifier AllMusicPacketId = Identifier.of("allmusic", "channel");

    public static void register() {
        if (!FabricLoader.getInstance().isModLoaded("allmusic_client"))
            ClientPlayNetworking.registerGlobalReceiver(AllMusicPackPayload.ID, (pack, handler) -> {});  // throttle allmusic packets to avoid annoying log
    }
}
