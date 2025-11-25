package org.lolicode.nekomusiccli.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.Identifier;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.config.ModConfig;
import org.lolicode.nekomusiccli.packet.ClientByeSender;
import org.lolicode.nekomusiccli.packet.ClientHelloSender;
import org.lolicode.nekomusiccli.utils.Alert;
import org.lolicode.nekomusiccli.utils.InstanceLock;
import org.lwjgl.glfw.GLFW;

public class KeyboardEvent {
    private static final ModConfig config = NekoMusicClient.config;
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(NekoMusicClient.MOD_CHANNEL, "general"));
    public static KeyMapping globalDisableKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.nekomusic.disable", // The translation key of the keybinding's name
                InputConstants.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_F7, // The keycode of the key
                CATEGORY // The translation key of the keybinding's category.
    ));

    public static KeyMapping serverDisableKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.nekomusic.server_disable", // The translation key of the keybinding's name
            InputConstants.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_F8, // The keycode of the key
            CATEGORY // The translation key of the keybinding's category.
    ));

    public static KeyMapping clientBanKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.nekomusic.ban_song", // The translation key of the keybinding's name
            InputConstants.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_F9, // The keycode of the key
            CATEGORY // The translation key of the keybinding's category.
    ));

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (globalDisableKeyBinding.consumeClick()) {
                onGlobalDisablePressed(client);
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (serverDisableKeyBinding.consumeClick()) {
                onServerDisablePressed(client);
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (clientBanKeyBinding.consumeClick()) {
                onClientBanPressed(client);
            }
        });
    }

    public static void onGlobalDisablePressed(Minecraft client) {
        if (!config.enabled) {
            config.enabled = true;
            Alert.info("nekomusic.enable");
            if (client.getCurrentServer() != null && !config.bannedServers.contains(client.getCurrentServer().ip)) {
                ClientHelloSender.send(client);
            }
        } else {
            config.enabled = false;
            if (NekoMusicClient.musicManager != null) NekoMusicClient.musicManager.stop();
            Alert.info("nekomusic.disable");
            if (client.getCurrentServer() != null) {
                ClientByeSender.send(client);
            }
            InstanceLock.release();
        }
        config.save();
    }

    private static void onServerDisablePressed(Minecraft client) {
        ServerData info = client.getCurrentServer();
        if (info == null) {
            if (client.player != null)
                Alert.error("nekomusic.not_multiplayer");
            return;
        }
        if (config.bannedServers.contains(info.ip)) {
            config.bannedServers.remove(info.ip);
            if (client.player != null)
                Alert.info("nekomusic.server_enable");
            ClientHelloSender.send(client);
        } else {
            config.bannedServers.add(info.ip);
            if (NekoMusicClient.musicManager != null) NekoMusicClient.musicManager.stop();
            if (client.player != null)
                Alert.info("nekomusic.server_disable");
            ClientByeSender.send(client);
            InstanceLock.release();
        }
        config.save();
    }

    private static void onClientBanPressed(Minecraft client) {
        if (NekoMusicClient.musicManager.currentMusic == null) {
            Alert.error("player.nekomusic.not_playing");
            return;
        }
        if (config.bannedSongs.stream().anyMatch(bannedSong -> bannedSong.id == NekoMusicClient.musicManager.currentMusic.id)) {
            NekoMusicClient.LOGGER.error("Song is already banned: {} ({}), something went wrong?", NekoMusicClient.musicManager.currentMusic.name, NekoMusicClient.musicManager.currentMusic.id);
            Alert.error("song.nekomusic.already_banned");
        } else {
            config.bannedSongs.add(new ModConfig.BannedSong(NekoMusicClient.musicManager.currentMusic.id, NekoMusicClient.musicManager.currentMusic.name));
            Alert.info("song.nekomusic.banned", NekoMusicClient.musicManager.currentMusic.name, NekoMusicClient.musicManager.currentMusic.id);
        }
        NekoMusicClient.musicManager.stop();
        config.save();
    }
}
