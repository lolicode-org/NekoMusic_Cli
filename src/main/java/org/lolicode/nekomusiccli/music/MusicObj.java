package org.lolicode.nekomusiccli.music;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MusicObj {
    public static class FreeTrialInfoObj {
        public int start;
        public int end;
    }

    public List<ArtistObj> ar = List.of();
    public String name;
    public long id;
    public String url;
    public long dt;
    public long time; // should be equal to dt, to determine if the song is trial (fuck netease)
    public FreeTrialInfoObj freeTrialInfo;
    public byte fee;
    public byte payed;
    public String player;  // who ordered this song
    public LyricObj lyric;
    public int br;
    @SerializedName("al")
    public AlbumObj album;

    public String Hash() {
        if (this.id != 0)
            return this.id + "_" + this.br;
        else
            return this.url;  // AllMusic and custom packets
    }
}
