package org.lolicode.nekomusiccli.network;

import okhttp3.OkHttpClient;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.cache.CacheType;
import org.lolicode.nekomusiccli.cache.CacheUtils;
import org.lolicode.nekomusiccli.music.AlbumObj;
import org.lolicode.nekomusiccli.music.MusicObj;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

public class NetUtils {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();

    private final CacheUtils cacheUtils = new CacheUtils();

    public BufferedInputStream getMusicStream(MusicObj musicObj) {
        return fetchDataAutoCache(musicObj.url, musicObj.Hash(), CacheType.MUSIC);
    }

    public BufferedInputStream getImageStream(AlbumObj albumObj) {
        return fetchDataAutoCache(albumObj.picUrl, albumObj.Hash(), CacheType.IMG);
    }

    private BufferedInputStream fetchData(String url, String hash, CacheType cacheType, boolean cache) {
        BufferedInputStream inputStream = cacheUtils.getFromCache(hash, cacheType);
        if (inputStream != null) {
            return inputStream;
        }
        try (var resp = client.newCall(NetRequest.getRequest(url)).execute()) {
            if (resp.code() != 200) {
                return null;
            }
            var body = resp.body();
            if (body == null) {
                return null;
            }
            byte[] bytes = body.bytes();
            if (cache) {
                cacheUtils.saveToCache(hash, bytes, cacheType);
            }
            return new BufferedInputStream(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            NekoMusicClient.LOGGER.error("Failed to get data: " + e.getMessage());
            return null;
        }
    }

    private BufferedInputStream fetchDataAutoCache(String url, String hash, CacheType cacheType) {
        switch (cacheType) {
            case MUSIC -> {
                return fetchData(url, hash, cacheType, NekoMusicClient.config.musicCacheSize != 0);
            }
            case IMG -> {
                return fetchData(url, hash, cacheType, NekoMusicClient.config.imgCacheSize != 0);
            }
            default -> {
                throw new IllegalArgumentException("Invalid cache type");
            }
        }
    }
}
