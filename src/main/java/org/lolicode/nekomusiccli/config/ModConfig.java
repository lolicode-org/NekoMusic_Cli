package org.lolicode.nekomusiccli.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.lolicode.nekomusiccli.cache.CacheUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Config(name = "nekomusic")
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.Excluded
    private static final Map<String, String> env = System.getenv();
    public boolean enabled = true;
    public boolean enableHud = true;
    public boolean enableHudInfo = true;
    public boolean enableHudList = true;
    public boolean enableHudLyric = true;
    public boolean enableHudImg = true;
    @ConfigEntry.Gui.Tooltip
    public boolean enableHudImgRotate = true;

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
    public List<String> bannedServers = new ArrayList<>();
    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.Tooltip(count = 2)
    @ConfigEntry.Gui.RequiresRestart
    public boolean allmusicCompatible = false;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.Gui.RequiresRestart
    public long musicResponseSizeLimit = 200;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public long imgResponseSizeLimit = 10;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public int musicCacheSize = 1024;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public int imgCacheSize = 100;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.Gui.RequiresRestart
    public String cachePath = CacheUtils.getDefaultCachePath();

    public void validatePostLoad() throws ValidationException {
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
        if (musicResponseSizeLimit != -1 && musicResponseSizeLimit < 10)
            musicResponseSizeLimit = 10;
        while (bannedServers.remove("")) ;
        try {
            CacheUtils.checkCachePath(getCachePath());
        } catch (Exception e) {
            throw new ValidationException("Invalid cache path: ", e);
        }
    }

    public void save() {
        AutoConfig.getConfigHolder(this.getClass()).save();
    }

    public String getCachePath() {
        StrSubstitutor strSubstitutor = new StrSubstitutor(env);
        return strSubstitutor.replace(cachePath);
    }
}
