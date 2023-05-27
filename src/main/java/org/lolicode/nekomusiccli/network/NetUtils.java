package org.lolicode.nekomusiccli.network;

import okhttp3.OkHttpClient;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.cache.CacheType;
import org.lolicode.nekomusiccli.cache.CacheUtils;
import org.lolicode.nekomusiccli.music.AlbumObj;
import org.lolicode.nekomusiccli.music.MusicObj;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

// A class that provides methods to get music and image streams from URLs
public class NetUtils {

    // A single instance of OkHttpClient
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();

    // A cache utility for music and images
    private final CacheUtils cacheUtils = new CacheUtils();

    // A method that returns a buffered input stream for music from a given MusicObj
    // If the music is already cached, returns it from the cache
    // Otherwise, fetches it from the URL and saves it to the cache
    // Returns null if the URL is invalid or an error occurs
    public BufferedInputStream getMusicStream(MusicObj musicObj) {
        return fetchDataAutoCache(musicObj.url, musicObj.Hash(), CacheType.MUSIC);
    }

    // A method that returns a buffered input stream for image from a given AlbumObj
    // If the image is already cached, returns it from the cache
    // Otherwise, fetches it from the URL and saves it to the cache
    // Returns null if the URL is invalid or an error occurs
    public BufferedInputStream getImageStream(AlbumObj albumObj) {
        return fetchDataAutoCache(albumObj.picUrl, albumObj.Hash(), CacheType.IMG);
    }

    // A private helper method that fetches and caches data from a given URL using a given hash
    // Returns a buffered input stream for the data or null if an error occurs
    private BufferedInputStream fetchData(String url, String hash, int type) {
        // Try to get the data from the cache
        BufferedInputStream inputStream = cacheUtils.getFromCache(hash, type);
        // If found, return it
        if (inputStream != null) {
            return inputStream;
        }
        // Otherwise, try to fetch it from the URL
        try (var resp = client.newCall(NetRequest.getRequest(url)).execute()) {
            // Check if the response is successful
            if (resp.code() != 200) {
                return null;
            }
            // Get the response body
            var body = resp.body();
            if (body == null) {
                return null;
            }
            // Get the input stream from the body
            inputStream = new BufferedInputStream(body.byteStream());
        } catch (Exception e) {
            NekoMusicClient.LOGGER.error("Failed to get data: " + e.getMessage());
        }
        // Return the input stream or null if an error occurred
        return inputStream;
    }

    private BufferedInputStream fetchAndCacheData(String url, String hash, int type) {
        BufferedInputStream inputStream = fetchData(url, hash, type);
        if (inputStream == null) {
            return null;
        }
        byte[] bytes;
        try (inputStream) {
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {
            NekoMusicClient.LOGGER.error("Failed to read data: " + e.getMessage());
            return null;
        }
        cacheUtils.saveToCache(hash, bytes, type);
        return new BufferedInputStream(new ByteArrayInputStream(bytes));
    }

    private BufferedInputStream fetchDataAutoCache(String url, String hash, int type) {
        switch (type) {
            case CacheType.MUSIC -> {
                return NekoMusicClient.config.musicCacheSize == 0 ? fetchData(url, hash, type) : fetchAndCacheData(url, hash, type);
            }
            case CacheType.IMG -> {
                return NekoMusicClient.config.imgCacheSize == 0 ? fetchData(url, hash, type) : fetchAndCacheData(url, hash, type);
            }
            default -> {
                throw new IllegalArgumentException("Invalid cache type");
            }
        }
    }
}
