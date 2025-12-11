package org.lolicode.nekomusiccli.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.multiplayer.ServerData;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.bus.api.SubscribeEvent;

import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.config.ModConfig;
import org.lolicode.nekomusiccli.packet.ClientByeSender;
import org.lolicode.nekomusiccli.packet.ClientHelloSender;
import org.lolicode.nekomusiccli.utils.Alert;
import org.lolicode.nekomusiccli.utils.InstanceLock;

@EventBusSubscriber(modid = NekoMusicClient.MOD_ID, value = Dist.CLIENT)
public class KeyboardEvent implements IModBusEvent {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(NekoMusicClient.MOD_CHANNEL, "general"));
    // this class is initialized before the config, so we need to get it lazily
    private static ModConfig getConfig() {
        return NekoMusicClient.config;
    }

    // 1) Define your KeyMapping instances.
    public static final Lazy<KeyMapping> GLOBAL_DISABLE_KEY = Lazy.of(() -> new KeyMapping(
            "key.nekomusic.disable",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F7,
            CATEGORY
    ));

    public static final Lazy<KeyMapping> SERVER_DISABLE_KEY = Lazy.of(() -> new KeyMapping(
            "key.nekomusic.server_disable",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F8,
            CATEGORY
    ));

    public static final Lazy<KeyMapping> CLIENT_BAN_KEY = Lazy.of(() -> new KeyMapping(
            "key.nekomusic.ban_song",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F9,
            CATEGORY
    ));

    // 2) Register KeyMappings in RegisterKeyMappingsEvent.
    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(GLOBAL_DISABLE_KEY.get());
        event.register(SERVER_DISABLE_KEY.get());
        event.register(CLIENT_BAN_KEY.get());
    }

    // 3) Check for presses via ClientTickEvent (end phase).
    @EventBusSubscriber(modid = NekoMusicClient.MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            // Check each key once per tick by consumeClick()
            while (GLOBAL_DISABLE_KEY.get().consumeClick()) {
                onGlobalDisablePressed(Minecraft.getInstance());
            }
            while (SERVER_DISABLE_KEY.get().consumeClick()) {
                onServerDisablePressed(Minecraft.getInstance());
            }
            while (CLIENT_BAN_KEY.get().consumeClick()) {
                onClientBanPressed(Minecraft.getInstance());
            }
        }
    }

    // 4) Keep the same logic for your handlers (adapt if needed).
    private static void onGlobalDisablePressed(Minecraft client) {
        if (client.player == null) return;

        if (!getConfig().enabled) {
            getConfig().enabled = true;
            Alert.info("nekomusic.enable");
            ServerData server = client.getCurrentServer();
            if (server != null && !getConfig().bannedServers.contains(server.ip)) {
                ClientHelloSender.send(client);
            }
        } else {
            getConfig().enabled = false;
            if (NekoMusicClient.musicManager != null) {
                NekoMusicClient.musicManager.stop();
            }
            Alert.info("nekomusic.disable");
            ServerData server = client.getCurrentServer();
            if (server != null) {
                ClientByeSender.send(client);
            }
            InstanceLock.release();
        }
        getConfig().save();
    }

    private static void onServerDisablePressed(Minecraft client) {
        if (client.player == null) return;
        ServerData info = client.getCurrentServer();
        if (info == null) {
            Alert.error("nekomusic.not_multiplayer");
            return;
        }

        if (getConfig().bannedServers.contains(info.ip)) {
            getConfig().bannedServers.remove(info.ip);
            Alert.info("nekomusic.server_enable");
            ClientHelloSender.send(client);
        } else {
            getConfig().bannedServers.add(info.ip);
            if (NekoMusicClient.musicManager != null) {
                NekoMusicClient.musicManager.stop();
            }
            Alert.info("nekomusic.server_disable");
            ClientByeSender.send(client);
            InstanceLock.release();
        }
        getConfig().save();
    }

    private static void onClientBanPressed(Minecraft client) {
        if (NekoMusicClient.musicManager.currentMusic == null) {
            Alert.error("player.nekomusic.not_playing");
            return;
        }

        if (getConfig().bannedSongs.stream()
                .anyMatch(banned -> banned.id == NekoMusicClient.musicManager.currentMusic.id)) {
            NekoMusicClient.LOGGER.error("Song is already banned: {} ({}), something went wrong?",
                    NekoMusicClient.musicManager.currentMusic.name,
                    NekoMusicClient.musicManager.currentMusic.id);
            Alert.error("song.nekomusic.already_banned");
        } else {
            getConfig().bannedSongs.add(new ModConfig.BannedSong(
                    NekoMusicClient.musicManager.currentMusic.id,
                    NekoMusicClient.musicManager.currentMusic.name
            ));
            Alert.info("song.nekomusic.banned",
                    NekoMusicClient.musicManager.currentMusic.name,
                    NekoMusicClient.musicManager.currentMusic.id);
        }
        NekoMusicClient.musicManager.stop();
        getConfig().save();
    }
}