package org.lolicode.nekomusiccli.network;

import okhttp3.*;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.cache.CacheUtils;
import org.lolicode.nekomusiccli.config.ModConfig;
import org.lolicode.nekomusiccli.music.AlbumObj;
import org.lolicode.nekomusiccli.music.MusicObj;

import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;

public class NetUtils {

    private final OkHttpClient musicClient;
    private final OkHttpClient imageClient;

    public NetUtils(ModConfig config) {
        CacheUtils cacheUtils = new CacheUtils(config);
        RequestInterceptor requestInterceptor = new RequestInterceptor();
        musicClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(requestInterceptor)  // if cache miss, replace url with request's tag. must be the first interceptor
                .addInterceptor(new ResponseInterceptor(config.musicResponseSizeLimit * 1024 * 1024))
                .cache(new Cache(cacheUtils.getMusicCachePath(), (long) config.musicCacheSize * 1024 * 1024))
                .build();
        imageClient = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .addInterceptor(requestInterceptor)
                .addInterceptor(new ResponseInterceptor(config.imgResponseSizeLimit * 1024 * 1024))
                .cache(new Cache(cacheUtils.getImgCachePath(), (long) config.imgCacheSize * 1024 * 1024))
                .build();
    }

    public Response getMusicResponse(MusicObj musicObj) throws InterruptedIOException {
        return fetchData(musicObj.url, musicObj.Hash(), musicClient);
    }

    public Response getImageResponse(AlbumObj albumObj) throws InterruptedIOException {
        return fetchData(albumObj.picUrl, albumObj.Hash(), imageClient);
    }

    private Response fetchData(String url, String hash, OkHttpClient client) throws InterruptedIOException {
        Response resp;
        try {
            resp = client.newCall(NetRequest.getRequest(url, hash)).execute();
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
}
