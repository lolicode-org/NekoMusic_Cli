package org.lolicode.nekomusiccli.events;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.utils.InstanceLock;

public class OnQuitServer {
    public static void onQuitServer() {
        if (NekoMusicClient.musicManager != null) {
            NekoMusicClient.musicManager.stop();
        }
        if (NekoMusicClient.hudUtils != null) {
            NekoMusicClient.hudUtils.stopCurrentMusic();
//            NekoMusicClient.hudUtils.close();
//            NekoMusicClient.hudUtils = null;
        }
        if (NekoMusicClient.cacheUtils != null) {
            NekoMusicClient.cacheUtils.save();
        }
        InstanceLock.release();
    }

    public static void register() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> onQuitServer());
    }
}
