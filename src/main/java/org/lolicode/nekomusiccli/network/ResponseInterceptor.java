package org.lolicode.nekomusiccli.network;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.GzipSource;
import org.jetbrains.annotations.NotNull;
import org.lolicode.nekomusiccli.NekoMusicClient;

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
            if (contentEncoding != null && contentEncoding.equals(GZIP)) {
                originalBody.close();
                GzipSource gzipSource = new GzipSource(originalBody.source());
                Buffer buffer = new Buffer();
                long totalBytesRead = 0;
                long bytesRead;

                while ((bytesRead = gzipSource.read(buffer, 8192)) != -1) {
                    totalBytesRead += bytesRead;
                    if (totalBytesRead > MAX_SIZE) {
                        gzipSource.close();
                        throw new IOException("Uncompressed response size too large: " + totalBytesRead + " bytes");
                    }
                }

                ResponseBody newBody = ResponseBody.create(buffer, originalBody.contentType(), totalBytesRead);
                originalResponse = originalResponse.newBuilder().body(newBody).build();
            }
        }
        return originalResponse;
    }
}
