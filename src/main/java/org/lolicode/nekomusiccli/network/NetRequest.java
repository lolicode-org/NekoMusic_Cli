package org.lolicode.nekomusiccli.network;

import okhttp3.Request;

public class NetRequest {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36";

    protected static Request getRequest(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("User-Agent", USER_AGENT)
                .build();
    }
}
