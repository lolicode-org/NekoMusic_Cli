package org.lolicode.nekomusiccli.network;

import okhttp3.*;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.cache.CacheType;
import org.lolicode.nekomusiccli.cache.CacheUtils;
import org.lolicode.nekomusiccli.config.ModConfig;
import org.lolicode.nekomusiccli.music.AlbumObj;
import org.lolicode.nekomusiccli.music.MusicObj;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;

import static org.lolicode.nekomusiccli.cache.CacheType.IMG;
import static org.lolicode.nekomusiccli.cache.CacheType.MUSIC;

public class NetUtils {

    private final OkHttpClient musicClient;
    private final OkHttpClient imageClient;

    private final CacheUtils cacheUtils;

    public NetUtils(ModConfig config) {
        musicClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new ResponseInterceptor(config.musicResponseSizeLimit * 1024 * 1024))
                .build();
        imageClient = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .addInterceptor(new ResponseInterceptor(config.imgResponseSizeLimit * 1024 * 1024))
                .build();
        cacheUtils = new CacheUtils(config);
    }

    public ByteArrayInputStream getMusicStream(MusicObj musicObj, boolean onlyCache) throws InterruptedIOException {
        return fetchDataAutoCache(musicObj.url, musicObj.Hash(), MUSIC, onlyCache);
    }

    public Response getMusicResponse(MusicObj musicObj) throws InterruptedIOException {
        return fetchData(musicObj.url, musicObj.Hash(), musicClient, MUSIC);
    }

    public ByteArrayInputStream getImageStream(AlbumObj albumObj) throws InterruptedIOException {
        return fetchDataAutoCache(albumObj.picUrl, albumObj.Hash(), IMG, false);
    }

    private ByteArrayInputStream fetchData(String url, String hash, OkHttpClient client, CacheType cacheType, boolean saveToCache, boolean onlyCache) throws InterruptedIOException {
        BufferedInputStream inputStream = cacheUtils.getFromCache(hash, cacheType);
        if (inputStream != null) {
            try (inputStream) {
                return new ByteArrayInputStream(inputStream.readAllBytes());
            } catch (Exception e) {
                NekoMusicClient.LOGGER.error("Failed to read cache: " + e.getMessage());
            }
        }
        if (onlyCache) return null;
        try (var resp = client.newCall(NetRequest.getRequest(url)).execute()) {
            if (resp.code() != 200) {
                return null;
            }
            var body = resp.body();
            if (body == null) {
                return null;
            }
            byte[] bytes = body.bytes();
            if (saveToCache) {
                cacheUtils.saveToCache(hash, bytes, cacheType);
            }
            return new ByteArrayInputStream(bytes);
        } catch (InterruptedIOException e) {
            NekoMusicClient.LOGGER.info("Request interrupted");
            throw e;
        } catch (Exception e) {
            NekoMusicClient.LOGGER.error("Failed to get data: " + e.getMessage());
            return null;
        }
    }

    private Response fetchData(String url, String hash, OkHttpClient client, CacheType cacheType) throws InterruptedIOException {
        Response resp;
        try {
            resp = client.newCall(NetRequest.getRequest(url)).execute();
        } catch (InterruptedIOException e) {
            NekoMusicClient.LOGGER.info("Request interrupted");
            throw e;
        } catch (Exception e) {
            NekoMusicClient.LOGGER.error("Failed to get data: " + e.getMessage());
            return null;
        }
        if (resp.code() != 200) {
            return null;
        }
        var body = resp.body();
        if (body == null || body.contentLength() <= 0) {
            return null;
        }
        return resp;
    }

    private ByteArrayInputStream fetchDataAutoCache(String url, String hash, CacheType cacheType, boolean onlyCache) throws InterruptedIOException {
        switch (cacheType) {
            case MUSIC -> {
                return fetchData(url, hash, musicClient, cacheType, NekoMusicClient.config.musicCacheSize != 0, onlyCache);
            }
            case IMG -> {
                return fetchData(url, hash, imageClient, cacheType, NekoMusicClient.config.imgCacheSize != 0, onlyCache);
            }
            default -> throw new IllegalArgumentException("Invalid cache type");
        }
    }
}
