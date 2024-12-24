package org.lolicode.nekomusiccli.events;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.packet.ClientHelloSender;

import java.net.SocketAddress;

@EventBusSubscriber(modid = NekoMusicClient.MOD_ID, value = Dist.CLIENT)
public class OnJoinServer {
    @SubscribeEvent
    public static void OnPlayerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        SocketAddress address = event.getConnection().getRemoteAddress();
        if (!NekoMusicClient.config.enabled || NekoMusicClient.config.bannedServers.contains(address.toString())) {
            return;
        }
        ClientHelloSender.send(Minecraft.getInstance());
    }
}
