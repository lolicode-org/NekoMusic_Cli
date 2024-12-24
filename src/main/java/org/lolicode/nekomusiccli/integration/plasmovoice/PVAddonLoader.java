package org.lolicode.nekomusiccli.integration.plasmovoice;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import su.plo.voice.api.client.PlasmoVoiceClient;

public class PVAddonLoader {
    public static void load() {
        var addon = new NekoMusicPVAddon();
        PlasmoVoiceClient.getAddonsLoader().load(addon);
        NeoForge.EVENT_BUS.register(new NekoMusicPVAddonEvents(addon));
    }

    public static class NekoMusicPVAddonEvents {
        private final NekoMusicPVAddon addon;

        public NekoMusicPVAddonEvents(NekoMusicPVAddon addon) {
            this.addon = addon;
        }

        @SubscribeEvent
        public void onClientStart(FMLClientSetupEvent event) {
            addon.setVolume(Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.RECORDS));
        }

        @SubscribeEvent
        public void onClientJoinWorld(ClientPlayerNetworkEvent.LoggingIn event) {
            addon.onJoinWorld(Minecraft.getInstance());
        }

        @SubscribeEvent
        public void onClientLeaveWorld(ClientPlayerNetworkEvent.LoggingOut event) {
            addon.onLeaveWorld();
        }
    }
}
