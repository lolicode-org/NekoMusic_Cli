package org.lolicode.nekomusiccli.network;

import okhttp3.CacheControl;
import okhttp3.Request;

import java.util.concurrent.TimeUnit;

public class NetRequest {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36";

    protected static Request getRequest(String url, boolean forceCache) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", USER_AGENT);
        if (forceCache) {
            builder.cacheControl(CacheControl.FORCE_CACHE);
        } else {
            builder.cacheControl(new CacheControl.Builder()
                    .maxAge(365, TimeUnit.DAYS)
                    .build())
                    .build();
        }
        return builder.build();
    }
}
