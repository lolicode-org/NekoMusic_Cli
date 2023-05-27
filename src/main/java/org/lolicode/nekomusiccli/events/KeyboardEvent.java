package org.lolicode.nekomusiccli.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.config.ModConfig;
import org.lolicode.nekomusiccli.packet.ClientByeSender;
import org.lolicode.nekomusiccli.packet.ClientHelloSender;
import org.lwjgl.glfw.GLFW;

public class KeyboardEvent {
    private static final ModConfig config = NekoMusicClient.config;
    public static KeyBinding globalDisableKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nekomusic.disable", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_F7, // The keycode of the key
                "category.nekomusic.general" // The translation key of the keybinding's category.
    ));

    public static KeyBinding serverDisableKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.nekomusic.server_disable", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_F8, // The keycode of the key
            "category.nekomusic.general" // The translation key of the keybinding's category.
    ));

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (globalDisableKeyBinding.wasPressed()) {
                onGlobalDisablePressed(client);
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (serverDisableKeyBinding.wasPressed()) {
                onServerDisablePressed(client);
            }
        });
    }

    public static void onGlobalDisablePressed(MinecraftClient client) {
        if (!config.enabled) {
            config.enabled = true;
            client.player.sendMessage(Text.translatable("nekomusic.enable"), false);
            if (client.getCurrentServerEntry() != null && !config.bannedServers.contains(client.getCurrentServerEntry().address)) {
                ClientHelloSender.send(client);
            }
        } else {
            config.enabled = false;
            if (NekoMusicClient.musicManager != null) NekoMusicClient.musicManager.stop();
            client.player.sendMessage(Text.translatable("nekomusic.disable"), false);
            if (client.getCurrentServerEntry() != null) {
                ClientByeSender.send(client);
            }
        }
        config.save();
    }

    private static void onServerDisablePressed(MinecraftClient client) {
        ServerInfo info = client.getCurrentServerEntry();
        if (info == null) {
            if (client.player != null)
                client.player.sendMessage(Text.translatable("nekomusic.not_multiplayer"), false);
            return;
        }
        if (config.bannedServers.contains(info.address)) {
            config.bannedServers.remove(info.address);
            if (client.player != null)
                client.player.sendMessage(Text.translatable("nekomusic.server_enable"), false);
            ClientHelloSender.send(client);
        } else {
            config.bannedServers.add(info.address);
            if (NekoMusicClient.musicManager != null) NekoMusicClient.musicManager.stop();
            if (client.player != null)
                client.player.sendMessage(Text.translatable("nekomusic.server_disable"), false);
            ClientByeSender.send(client);
        }
        config.save();
    }
}
