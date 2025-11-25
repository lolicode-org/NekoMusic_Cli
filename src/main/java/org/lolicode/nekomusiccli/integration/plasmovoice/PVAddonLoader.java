package org.lolicode.nekomusiccli.integration.plasmovoice;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import org.lolicode.nekomusiccli.config.CustomSoundCategory;
import su.plo.voice.api.client.PlasmoVoiceClient;

public class PVAddonLoader {
    public static void load() {
        var addon = new NekoMusicPVAddon();
        PlasmoVoiceClient.getAddonsLoader().load(addon);
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> addon.setVolume(Minecraft.getInstance().options.getFinalSoundSourceVolume(CustomSoundCategory.NEKOMUSIC)));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> addon.onJoinWorld(client));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> addon.onLeaveWorld());
    }
}
