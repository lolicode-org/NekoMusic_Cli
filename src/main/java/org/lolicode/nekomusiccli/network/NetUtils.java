package org.lolicode.nekomusiccli.network;

import okhttp3.OkHttpClient;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.cache.CacheType;
import org.lolicode.nekomusiccli.cache.CacheUtils;
import org.lolicode.nekomusiccli.config.ModConfig;
import org.lolicode.nekomusiccli.music.AlbumObj;
import org.lolicode.nekomusiccli.music.MusicObj;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

public class NetUtils {

    private final OkHttpClient musicClient;
    private final OkHttpClient imageClient;

    private final CacheUtils cacheUtils;

    public NetUtils(ModConfig config) {
        musicClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new ResponseInterceptor(config.responseSizeLimit * 1024 * 1024))
                .build();
        imageClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(new ResponseInterceptor(5 * 1024 * 1024))
                .build();
        cacheUtils = new CacheUtils(config);
    }

    public ByteArrayInputStream getMusicStream(MusicObj musicObj) {
        return fetchDataAutoCache(musicObj.url, musicObj.Hash(), CacheType.MUSIC);
    }

    public ByteArrayInputStream getImageStream(AlbumObj albumObj) {
        return fetchDataAutoCache(albumObj.picUrl, albumObj.Hash(), CacheType.IMG);
    }

    private ByteArrayInputStream fetchData(String url, String hash, OkHttpClient client, CacheType cacheType, boolean cache) {
        BufferedInputStream inputStream = cacheUtils.getFromCache(hash, cacheType);
        if (inputStream != null) {
            try (inputStream) {
                return new ByteArrayInputStream(inputStream.readAllBytes());
            } catch (Exception e) {
                NekoMusicClient.LOGGER.error("Failed to read cache: " + e.getMessage());
            }
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
            return new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            NekoMusicClient.LOGGER.error("Failed to get data: " + e.getMessage());
            return null;
        }
    }

    private ByteArrayInputStream fetchDataAutoCache(String url, String hash, CacheType cacheType) {
        switch (cacheType) {
            case MUSIC -> {
                return fetchData(url, hash, musicClient, cacheType, NekoMusicClient.config.musicCacheSize != 0);
            }
            case IMG -> {
                return fetchData(url, hash, imageClient, cacheType, NekoMusicClient.config.imgCacheSize != 0);
            }
            default -> {
                throw new IllegalArgumentException("Invalid cache type");
            }
        }
    }
}
