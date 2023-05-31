package org.lolicode.nekomusiccli;

import com.google.gson.Gson;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lolicode.nekomusiccli.config.ModConfig;
import org.lolicode.nekomusiccli.hud.HudUtils;
import org.lolicode.nekomusiccli.music.MusicManager;
import org.lolicode.nekomusiccli.network.NetUtils;
import org.lolicode.nekomusiccli.events.Events;
import org.lolicode.nekomusiccli.packet.AllMusicPacketReceiver;
import org.lolicode.nekomusiccli.packet.NekoMusicPacketReceiver;

public class NekoMusicClient implements ClientModInitializer {
    public static final String MOD_ID = "nekomusiccli";
    public static final String MOD_NAME = "NekoMusic Client";
    public static final String MOD_CHANNEL = "nekomusic";
    public static final Identifier MOD_BASE_IDENTIFIER = new Identifier(MOD_CHANNEL, MOD_ID);  // if not provide MODID, the namespace will be "minecraft"
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final Gson GSON = new Gson();
    public static MusicManager musicManager;
    public static HudUtils hudUtils = null;
    public static ModConfig config;
    public static NetUtils netUtils;
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        netUtils = new NetUtils(config);
        musicManager = new MusicManager();

        Events.register();
        NekoMusicPacketReceiver.register();
        AllMusicPacketReceiver.register();
    }
}
