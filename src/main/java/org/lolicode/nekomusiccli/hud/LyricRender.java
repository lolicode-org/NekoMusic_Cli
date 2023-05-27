package org.lolicode.nekomusiccli.hud;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.libs.lrcparser.Lyric;
import org.lolicode.nekomusiccli.libs.lrcparser.parser.LyricParser;
import org.lolicode.nekomusiccli.music.LyricObj;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LyricRender {
    private final ScheduledExecutorService lyricExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(NekoMusicClient.MOD_NAME + "-lyric-%d").build());
    private Lyric lyric;
    private volatile String currentSentence;

    public LyricRender(LyricObj lyric) {
        if (lyric == null || lyric.getLyric() == null) {
            this.lyric = null;
            return;
        }
        try {
            var lyricParser = LyricParser.create(new BufferedReader(new StringReader(lyric.getLyric())));
            var translationParser = lyric.getTranslation() == null ? null : LyricParser.create(new BufferedReader(new StringReader(lyric.getTranslation())));
            var translation = translationParser == null ? null : new Lyric(translationParser.getTags(), translationParser.getSentences());

            this.lyric = new Lyric(lyricParser.getTags(), lyricParser.getSentences());
            this.lyric.merge(translation);
        } catch (Exception e) {
            this.lyric = null;
            NekoMusicClient.LOGGER.error("Failed to parse lyric: " + e.getMessage());
        }
    }

    public void render() {
        RenderMain.drawText(currentSentence, NekoMusicClient.config.lyricX, NekoMusicClient.config.lyricY);
    }

    public synchronized boolean hasLyric() {
        return lyric != null;
    }

    public void start() {
        if (lyric == null) return;
        final long startTime = System.currentTimeMillis();
        lyricExecutor.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis() - startTime;
            if (currentTime > lyric.getDuration() || currentTime < 0) {
                lyricExecutor.shutdownNow();
                return;
            }
            var sentence = lyric.findContent(currentTime);
            currentSentence = sentence == null ? "" : sentence;
        }, 0, 20, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        lyricExecutor.shutdownNow();
    }
}
