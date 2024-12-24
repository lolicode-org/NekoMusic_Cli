package org.lolicode.nekomusiccli.events;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.utils.InstanceLock;

@EventBusSubscriber(modid = NekoMusicClient.MOD_ID, value = Dist.CLIENT)
public class OnQuitServer {
    @SubscribeEvent
    public static void onQuitServer(ClientPlayerNetworkEvent.LoggingOut event) {
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
}
