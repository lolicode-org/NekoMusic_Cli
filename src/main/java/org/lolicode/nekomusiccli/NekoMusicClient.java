package org.lolicode.nekomusiccli;

import com.google.gson.Gson;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.AutoConfigClient;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lolicode.nekomusiccli.cache.CacheUtils;
import org.lolicode.nekomusiccli.config.ModConfig;
import org.lolicode.nekomusiccli.hud.HudUtils;
import org.lolicode.nekomusiccli.integration.plasmovoice.NekoMusicPVAddon;
import org.lolicode.nekomusiccli.integration.plasmovoice.PVAddonLoader;
import org.lolicode.nekomusiccli.music.MusicManager;
import org.lolicode.nekomusiccli.network.NetUtils;
import org.lolicode.nekomusiccli.packet.ClientByeSender;
import org.lolicode.nekomusiccli.packet.ClientHelloSender;
import org.lolicode.nekomusiccli.packet.ServerPacketReceiver;

@Mod(NekoMusicClient.MOD_ID)
public class NekoMusicClient {
    public static final String MOD_ID = "nekomusiccli";
    public static final String MOD_NAME = "NekoMusic Client";
    public static final String MOD_CHANNEL = "nekomusic";
    public static final Identifier MOD_BASE_IDENTIFIER = Identifier.fromNamespaceAndPath(MOD_CHANNEL, MOD_ID);
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON = new Gson();
    public static MusicManager musicManager;
    public static HudUtils hudUtils = null;
    public static CacheUtils cacheUtils = null;
    public static ModConfig config;
    public static NetUtils netUtils;
    public static NekoMusicPVAddon pvAddon = null;

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public NekoMusicClient(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::clientSetup);
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
                () -> (container, parent) -> AutoConfigClient.getConfigScreen(ModConfig.class, parent).get());
    }

    @SubscribeEvent
    private void clientSetup(final FMLClientSetupEvent event) {
        cacheUtils = new CacheUtils(config);
        netUtils = new NetUtils(config);
        musicManager = new MusicManager();
        hudUtils = new HudUtils();

        ServerPacketReceiver.register();
        ClientHelloSender.register();
        ClientByeSender.register();

        if (config.lowerVolumeWhenPlayingVoice && ModList.get().isLoaded("plasmovoice")) {
            ModContainer pvModContainer = ModList.get().getModContainerById("plasmovoice").orElse(null);
            if (pvModContainer != null) {
                String version = pvModContainer.getModInfo().getVersion().toString();
                try {
                    if (version.compareTo("2.1.0") >= 0) {
                        PVAddonLoader.load();
                    } else {
                        LOGGER.warn("PlasmoVoice version is too low, PlasmoVoice integration will not be loaded.");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}