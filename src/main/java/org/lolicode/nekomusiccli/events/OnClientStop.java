package org.lolicode.nekomusiccli.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.lolicode.nekomusiccli.NekoMusicClient;

public class OnClientStop {
    public static void cleanUp() {
        if (NekoMusicClient.musicManager != null) {
            NekoMusicClient.musicManager.dispose();
        }
        if (NekoMusicClient.hudUtils != null) {
            NekoMusicClient.hudUtils.close();
            NekoMusicClient.hudUtils = null;
        }
    }

    public static void register() {
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> cleanUp());
    }
}
