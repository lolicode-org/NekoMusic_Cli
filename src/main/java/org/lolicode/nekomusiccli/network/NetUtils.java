package org.lolicode.nekomusiccli.network;

import okhttp3.*;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.config.ModConfig;
import org.lolicode.nekomusiccli.music.AlbumObj;
import org.lolicode.nekomusiccli.music.MusicObj;

import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NetUtils {

    private final OkHttpClient musicClient;
    private final OkHttpClient imageClient;

    public NetUtils(ModConfig config) {
        musicClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new ResponseInterceptor(config.musicResponseSizeLimit * 1024 * 1024))
                .cache(NekoMusicClient.cacheUtils.getMusicCache())
                .build();
        imageClient = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .addInterceptor(new ResponseInterceptor(config.imgResponseSizeLimit * 1024 * 1024))
                .cache(NekoMusicClient.cacheUtils.getImgCache())
                .build();
    }

    public Response getMusicResponse(MusicObj musicObj) throws InterruptedIOException {
        Response response = null;
        var cachedUrl = NekoMusicClient.cacheUtils.queryMusicCache(musicObj.Hash());
        if (cachedUrl != null && !cachedUrl.isBlank()) {
            response = fetchData(cachedUrl, musicClient, true);
        }
        if (response == null) {
            NekoMusicClient.LOGGER.debug("Cache miss for " + musicObj.Hash());
            response = fetchData(musicObj.url, musicClient, false);
            NekoMusicClient.cacheUtils.updateMusicCache(musicObj.Hash(), musicObj.url);
        }
        return response;
    }

    public Response getImageResponse(AlbumObj albumObj) throws InterruptedIOException {
        Response response = null;
        var cachedUrl = NekoMusicClient.cacheUtils.queryImgCache(albumObj.Hash());
        if (cachedUrl != null && !cachedUrl.isBlank()) {
            response = fetchData(cachedUrl, imageClient, true);
        }
        if (response == null) {
            NekoMusicClient.LOGGER.debug("Cache miss for " + albumObj.Hash());
            response = fetchData(albumObj.picUrl, imageClient, false);
            NekoMusicClient.cacheUtils.updateImgCache(albumObj.Hash(), albumObj.picUrl);
        }
        return response;
    }

    private Response fetchData(String url, OkHttpClient client, boolean forceCache) throws InterruptedIOException {
        if (!forceCache) {  // don't check whitelist for cache
            if (NekoMusicClient.config.domainWhitelist != null && !NekoMusicClient.config.domainWhitelist.isEmpty()) {
                if (!isDomainWhitelisted(url, NekoMusicClient.config.domainWhitelist)) {
                    throw new DomainNotInWhitelistException("Domain not whitelisted: " + url);
                }
            }
        }
        Response resp;
        try {
            resp = client.newCall(NetRequest.getRequest(url, forceCache)).execute();
        } catch (InterruptedIOException e) {
            NekoMusicClient.LOGGER.info("Request interrupted");
            throw e;
        } catch (Exception e) {
            NekoMusicClient.LOGGER.error("Failed to get data: ", e);
            return null;
        }
        if (resp.code() != 200) {
            resp.close();
            return null;
        }
        var body = resp.body();
        if (body == null || body.contentLength() <= 0) {
            resp.close();
            return null;
        }
        return resp;
    }

    private List<String> getParentDomains(String domain) {
        List<String> domains = new ArrayList<>();
        String[] parts = domain.split("\\.");
        for (int i = 0; i < parts.length - 1; i++) {
            domains.add(String.join(".", Arrays.copyOfRange(parts, i, parts.length)));
        }
        return domains;
    }

    private boolean isDomainWhitelisted(String url, Collection<String> whitelist) {
        try {
            String host = new URL(url).getHost();
            List<String> parentDomains = getParentDomains(host);
            for (String domain : parentDomains) {
                if (whitelist.contains(domain)) {
                    return true;
                }
            }
            return whitelist.contains(host);
        } catch (MalformedURLException e) {
            NekoMusicClient.LOGGER.error("Invalid URL: ", e);
            return false;
        }
    }
}
