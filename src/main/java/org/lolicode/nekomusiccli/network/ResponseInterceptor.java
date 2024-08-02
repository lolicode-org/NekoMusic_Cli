package org.lolicode.nekomusiccli.network;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ResponseInterceptor implements Interceptor {
    private final long MAX_SIZE;
    private static final String GZIP = "gzip";

    public ResponseInterceptor(long maxSize) {
        MAX_SIZE = maxSize;
    }
    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        ResponseBody originalBody = originalResponse.body();

        if (originalBody != null) {
            long contentLength = originalBody.contentLength();
            String contentEncoding = originalResponse.header("Content-Encoding");
            if (contentLength > MAX_SIZE) {
                originalBody.close();
                throw new IOException("Response size too large: " + contentLength + " bytes");
            }
        }
        return originalResponse;
    }
}
