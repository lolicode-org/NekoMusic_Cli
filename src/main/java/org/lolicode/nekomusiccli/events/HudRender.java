package org.lolicode.nekomusiccli.events;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import org.lolicode.nekomusiccli.NekoMusicClient;

import static org.lolicode.nekomusiccli.NekoMusicClient.hudUtils;

@EventBusSubscriber(modid = NekoMusicClient.MOD_ID, value = Dist.CLIENT)
public class HudRender {
    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGuiLayerEvent.Post event) {
        if (hudUtils != null) {
            hudUtils.frame(event.getGuiGraphics());
        }
    }
}
