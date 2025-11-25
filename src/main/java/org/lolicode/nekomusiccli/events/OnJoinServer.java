package org.lolicode.nekomusiccli.events;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.packet.ClientHelloSender;

public class OnJoinServer {
    public static void OnPlayerJoin(Minecraft client) {
        ServerData serverInfo = client.getCurrentServer();
        if (serverInfo == null || serverInfo.isLan()) return;
        if (!NekoMusicClient.config.enabled || NekoMusicClient.config.bannedServers.contains(serverInfo.ip)) return;
        ClientHelloSender.send(client);
    }

    public static void register() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> OnPlayerJoin(client));
    }
}
