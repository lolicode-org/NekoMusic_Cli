package org.lolicode.nekomusiccli.events;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import org.lolicode.nekomusiccli.packet.ClientHelloSender;

public class OnJoinServer {
    public static void OnPlayerJoin(MinecraftClient client) {
        ClientHelloSender.send(client);
    }

    public static void register() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            OnPlayerJoin(client);
        });
    }
}
