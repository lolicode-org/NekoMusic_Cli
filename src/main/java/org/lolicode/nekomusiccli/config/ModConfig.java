package org.lolicode.nekomusiccli.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.client.Minecraft;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.cache.CacheUtils;
import org.lolicode.nekomusiccli.utils.StrEnvSubstitutor;

import java.util.ArrayList;

// TODO: Apply changes immediately after saving the config

@Config(name = NekoMusicClient.MOD_ID)
public class ModConfig implements ConfigData {
    public static class BannedSong {
        @ConfigEntry.Gui.Tooltip
        public long id;
        @ConfigEntry.Gui.Tooltip
        public String name;

        public BannedSong() {
        }

        public BannedSong(long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
    public boolean enabled = true;
    public boolean blockMusic = true;
    public boolean blockRecords = false;

    public boolean enableHud = true;
    public boolean enableHudInfo = true;
    public boolean enableHudList = true;
    public boolean enableHudLyric = true;
    public boolean enableHudImg = true;
    @ConfigEntry.Gui.Tooltip
    public boolean enableHudImgRotate = true;

    public int maxCharPerLineListHud = 25;
    public int maxRowListHud = 5;
    public boolean scrollListHud = false;

    public int infoX = 74;
    public int infoY = 2;
    public int listX = 2;
    public int listY = 74;
    public int lyricX = 74;
    public int lyricY = 53;
    public int imgX = 2;
    public int imgY = 2;
    public int imgSize = 70;
    public int imgRotateSpeed = 50;
    @ConfigEntry.Gui.Tooltip(count = 2)
    @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
    public int textOpacity = 255;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
    public int textColorRed = 255;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
    public int textColorGreen = 255;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
    public int textColorBlue = 255;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public ArrayList<String> bannedServers = new ArrayList<>();

    @ConfigEntry.Gui.Tooltip()
    public ArrayList<BannedSong> bannedSongs = new ArrayList<>();

    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.Gui.RequiresRestart
    public long musicResponseSizeLimit = 0;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public long imgResponseSizeLimit = 25;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.Tooltip
    public long imgWidthLimit = 0;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public int musicCacheSize = 1024;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public int imgCacheSize = 100;
    
    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public boolean singleInstance = true;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.Gui.RequiresRestart
    public String cachePath = "";

    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.Tooltip
    public ArrayList<String> domainWhitelist = new ArrayList<>();

    @ConfigEntry.Category("integration")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public boolean lowerVolumeWhenPlayingVoice = true;

    @ConfigEntry.Category("integration")
    @ConfigEntry.Gui.Tooltip
    public boolean useAbsoluteVolume = false;

    @ConfigEntry.Category("integration")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int volumeWhenPlayingVoice = 25;

    public void validatePostLoad() throws ValidationException {
        if (maxCharPerLineListHud < 10 && maxCharPerLineListHud > 0)
            maxCharPerLineListHud = 10;
        if (infoX < 0)
            infoX = 0;
        if (infoY < 0)
            infoY = 0;
        if (listX < 0)
            listX = 0;
        if (listY < 0)
            listY = 0;
        if (lyricX < 0)
            lyricX = 0;
        if (lyricY < 0)
            lyricY = 0;
        if (imgX < 0)
            imgX = 0;
        if (imgY < 0)
            imgY = 0;
        if (imgSize < 0)
            imgSize = 10;
        if (musicCacheSize < -1)
            musicCacheSize = -1;
        if (imgRotateSpeed <= 0)
            imgRotateSpeed = 50;
        if (textOpacity < 0 || textOpacity > 255)
            textOpacity = 255;
        if (textColorRed < 0 || textColorRed > 255)
            textColorRed = 255;
        if (textColorGreen < 0 || textColorGreen > 255)
            textColorGreen = 255;
        if (textColorBlue < 0 || textColorBlue > 255)
            textColorBlue = 255;
        if (musicResponseSizeLimit > 0 && musicResponseSizeLimit < 10)
            musicResponseSizeLimit = 10;
        if (volumeWhenPlayingVoice < 0 || volumeWhenPlayingVoice > 100)
            volumeWhenPlayingVoice = 25;
        bannedServers.removeIf(String::isBlank);
        bannedSongs.removeIf(bannedSong -> bannedSong.id <= 0);
        domainWhitelist.removeIf(String::isBlank);
        if (!domainWhitelist.isEmpty() && !domainWhitelist.contains("music.126.net"))
            domainWhitelist.add("music.126.net");
        if (Minecraft.getInstance() != null) {
            // During client init, this is null, and npe will be thrown... Idk if it's something wrong with neo or cloth config, but ignore cache check during client init will workaround this
            try {
                CacheUtils.checkCachePath(getCachePath());
            } catch (Exception e) {
                throw new ValidationException("Invalid cache path: ", e);
            }
        }
    }

    public void save() {
        AutoConfig.getConfigHolder(this.getClass()).save();
    }

    public String getCachePath() {
        if (cachePath.isBlank())
            return CacheUtils.getDefaultCachePath();
        return StrEnvSubstitutor.replace(cachePath);
    }
}
