package org.lolicode.nekomusiccli.events;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import org.lolicode.nekomusiccli.NekoMusicClient;

@EventBusSubscriber(modid = NekoMusicClient.MOD_ID, value = Dist.CLIENT)
public class OnClientStop {
    @SubscribeEvent
    public static void cleanUp(GameShuttingDownEvent event) {
        if (NekoMusicClient.musicManager != null) {
            NekoMusicClient.musicManager.dispose();
        }
        if (NekoMusicClient.hudUtils != null) {
            NekoMusicClient.hudUtils.close();
            NekoMusicClient.hudUtils = null;
        }
        if (NekoMusicClient.cacheUtils != null) {
            NekoMusicClient.cacheUtils.close();
            NekoMusicClient.cacheUtils = null;
        }
    }
}
