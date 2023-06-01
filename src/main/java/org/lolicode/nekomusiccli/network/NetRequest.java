package org.lolicode.nekomusiccli.network;

import okhttp3.CacheControl;
import okhttp3.Request;

import java.util.concurrent.TimeUnit;

public class NetRequest {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36";
    private static final String CONTENT_TYPE = "audio/*";

    protected static Request getRequest(String url, String hash) {
        return new Request.Builder()
                .url("http://localhost/" + hash)  // okhttp will throw exception if not add http://
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Content-Type", CONTENT_TYPE)
                .cacheControl(new CacheControl.Builder()
                        .maxAge(365, TimeUnit.DAYS)
                        .build())
                .tag(url)
                .build();
    }
}
