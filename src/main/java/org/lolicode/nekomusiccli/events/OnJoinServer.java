package org.lolicode.nekomusiccli.events;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.packet.ClientHelloSender;

public class OnJoinServer {
    public static void OnPlayerJoin(MinecraftClient client) {
        ServerInfo serverInfo = client.getCurrentServerEntry();
        if (serverInfo == null || serverInfo.isLocal()) return;
        if (!NekoMusicClient.config.enabled || NekoMusicClient.config.bannedServers.contains(serverInfo.address)) return;
        ClientHelloSender.send(client);
    }

    public static void register() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            OnPlayerJoin(client);
        });
    }
}
