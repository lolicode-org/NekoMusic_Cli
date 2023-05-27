package org.lolicode.nekomusiccli;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lolicode.nekomusiccli.config.ModConfig;

public class NekoMusicClient implements ClientModInitializer {
    public static final String MOD_ID = "nekomusiccli";
    public static final String MOD_NAME = "NekoMusic Client";
    public static final String MOD_CHANNEL = "nekomusic";
    public static final Identifier MOD_BASE_IDENTIFIER = new Identifier(MOD_CHANNEL);
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static ModConfig config;
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

    }
}
