package org.lolicode.nekomusiccli.music;

import java.util.Arrays;
import java.util.List;

public class MusicList {
    public static class Music {
        public String name;
        public String artist;
        public String album;
    }

    public Music[] musics;

    public String toString() {
        List<Music> musics = Arrays.asList(this.musics);
        StringBuilder sb = new StringBuilder();
        musics.forEach(music -> {
            sb.append(musics.indexOf(music) + 1).append(". ").append(music.name);
            if (music.artist != null) {
                sb.append(" - ").append(music.artist);
            }
            if (music.album != null) {
                sb.append(" (").append(music.album).append(")");
            }
            sb.append("\n");
        });
        return sb.toString();
    }

    public Boolean isEmpty() {
        return musics == null || musics.length == 0;
    }
}
