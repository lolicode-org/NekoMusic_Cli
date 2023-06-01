package org.lolicode.nekomusiccli.music.player;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class OggDecoder implements Decoder {
    private final long decoder;
    private volatile boolean closed = false;
    private final STBVorbisInfo info;
    public OggDecoder(ByteArrayInputStream inputStream) throws IOException {
        // Have to use buffer created by BufferUtils, or it will kill your JVM...
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(inputStream.available());
        byteBuffer.put(inputStream.readAllBytes());
        byteBuffer.flip();
        IntBuffer errorBuffer = BufferUtils.createIntBuffer(1);
        decoder = STBVorbis.stb_vorbis_open_memory(byteBuffer, errorBuffer, null);
        if (decoder == 0 || errorBuffer.get(0) != 0) {
            throw new IOException("Failed to open Ogg file: " + errorBuffer.get(0));
        }
        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            STBVorbis.stb_vorbis_get_info(decoder, info);
            this.info = info;
        }
    }

    @Override
    public int getOutputFrequency() {
        return info.sample_rate();
    }

    @Override
    public int getOutputChannels() {
        return info.channels();
    }

    @Override
    public synchronized void close() {
        if (closed) return;
        closed = true;
        STBVorbis.stb_vorbis_close(decoder);
    }

    @Override
    public synchronized ByteBuffer decodeFrame() {
        if (closed) return null;
        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            STBVorbis.stb_vorbis_get_info(decoder, info);
            int channels = info.channels();
            int length = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);
            short[] shorts = new short[length * channels];
            int samples = STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, shorts);
            if (samples == 0) {
                return null;
            }
            return Decoder.getByteBuffer(shorts);
        }
    }
}
