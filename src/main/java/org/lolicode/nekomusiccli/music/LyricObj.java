package org.lolicode.nekomusiccli.music;

import com.google.gson.annotations.SerializedName;

public class LyricObj {
    static class Lrc {
        public String lyric;
    }
    @SerializedName("lrc")
    Lrc lrc;

    @SerializedName("tlyric")
    Lrc translation;

    public String getLyric() {
        return lrc == null ? null : lrc.lyric;
    }
    public String getTranslation() {
        return translation == null ? null : translation.lyric;
    }
}
