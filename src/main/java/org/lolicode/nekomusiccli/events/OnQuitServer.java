package org.lolicode.nekomusiccli.events;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.packet.AllMusicPacketReceiver;

public class OnQuitServer {
    public static void onQuitServer() {
        if (NekoMusicClient.musicManager != null) {
            NekoMusicClient.musicManager.stop();
        }
        if (NekoMusicClient.hudUtilsRef.get() != null) {
            NekoMusicClient.hudUtilsRef.getAndSet(null).close();
        }
        AllMusicPacketReceiver.isNekoServer = false;
    }

    public static void register() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> onQuitServer());
    }
}
