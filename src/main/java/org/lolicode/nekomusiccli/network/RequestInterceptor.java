package org.lolicode.nekomusiccli.network;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Replace url with request's tag if cache miss
 * <strong>Must be added before any interceptor that processes response</strong>
 */
public class RequestInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        // if cache miss, replace url with request's tag
        Request request = chain.request();
        if (request.url().toString().toLowerCase().startsWith("http://localhost/") && request.tag() != null) {
            // noinspection ConstantConditions
            request = request.newBuilder().url(request.tag().toString()).build();
        }
        return chain.proceed(request);
    }
}
