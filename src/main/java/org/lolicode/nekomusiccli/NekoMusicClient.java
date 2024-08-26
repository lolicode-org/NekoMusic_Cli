package org.lolicode.nekomusiccli;

import com.google.gson.Gson;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lolicode.nekomusiccli.cache.CacheUtils;
import org.lolicode.nekomusiccli.config.ModConfig;
import org.lolicode.nekomusiccli.events.Events;
import org.lolicode.nekomusiccli.hud.HudUtils;
import org.lolicode.nekomusiccli.integration.plasmovoice.NekoMusicPVAddon;
import org.lolicode.nekomusiccli.integration.plasmovoice.PVAddonLoader;
import org.lolicode.nekomusiccli.music.MusicManager;
import org.lolicode.nekomusiccli.network.NetUtils;
import org.lolicode.nekomusiccli.packet.ClientByeSender;
import org.lolicode.nekomusiccli.packet.ClientHelloSender;
import org.lolicode.nekomusiccli.packet.ServerPacketReceiver;

public class NekoMusicClient implements ClientModInitializer {
    public static final String MOD_ID = "nekomusiccli";
    public static final String MOD_NAME = "NekoMusic Client";
    public static final String MOD_CHANNEL = "nekomusic";
    public static final Identifier MOD_BASE_IDENTIFIER = Identifier.of(MOD_CHANNEL, MOD_ID);  // if not provide MODID, the namespace will be "minecraft"
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final Gson GSON = new Gson();
    public static MusicManager musicManager;
    public static HudUtils hudUtils = null;
    public static CacheUtils cacheUtils = null;
    public static ModConfig config;
    public static NetUtils netUtils;
    public static NekoMusicPVAddon pvAddon = null;
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        cacheUtils = new CacheUtils(config);
        netUtils = new NetUtils(config);
        musicManager = new MusicManager();
        hudUtils = new HudUtils();

        Events.register();
        ServerPacketReceiver.register();
        ClientHelloSender.register();
        ClientByeSender.register();

        if (config.lowerVolumeWhenPlayingVoice && FabricLoader.getInstance().isModLoaded("plasmovoice")) {
            var pvModContainer = FabricLoader.getInstance().getModContainer("plasmovoice");
            if (pvModContainer.isPresent()) {
                var version = pvModContainer.get().getMetadata().getVersion();
                try {
                    if (version.compareTo(Version.parse("2.1.0")) >= 0)
                        PVAddonLoader.load();
                    else
                        LOGGER.warn("PlasmoVoice version is too low, PlasmoVoice integration will not be loaded.");
                } catch (VersionParsingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
