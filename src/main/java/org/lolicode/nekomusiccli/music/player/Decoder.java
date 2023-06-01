package org.lolicode.nekomusiccli.music.player;

import javazoom.jl.decoder.BitstreamException;
import org.jetbrains.annotations.Nullable;
import org.lolicode.nekomusiccli.libs.flac.decode.DataFormatException;
import org.lwjgl.BufferUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public interface Decoder extends AutoCloseable {
    static Decoder getDecoder(ByteArrayInputStream inputStream) throws DataFormatException, IOException {
        if (inputStream == null) {
            throw new NullPointerException("inputStream is null");
        }
        try {
            return getFlacDecoder(inputStream);
        } catch (DataFormatException e) {
            try {
                inputStream.reset();
                return getMp3Decoder(inputStream);
            } catch (DataFormatException | BitstreamException ex) {
                inputStream.reset();
                return getOggDecoder(inputStream);
            }
        }
    }

    static Decoder getMp3Decoder(InputStream inputStream) throws DataFormatException, BitstreamException {
        if (inputStream == null) {
            throw new NullPointerException("inputStream is null");
        }
        return new Mp3Decoder(inputStream);
    }

    static Decoder getFlacDecoder(InputStream inputStream) throws DataFormatException, IOException {
        if (inputStream == null) {
            throw new NullPointerException("inputStream is null");
        }
        return new FlacDecoder(inputStream);
    }

    static Decoder getOggDecoder(ByteArrayInputStream inputStream) throws DataFormatException, IOException {
        if (inputStream == null) {
            throw new NullPointerException("inputStream is null");
        }
        return new OggDecoder(inputStream);
    }

    static ByteBuffer getByteBuffer(byte[] bytes) {
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(bytes.length);
        byteBuffer.put(bytes, 0, bytes.length);
        byteBuffer.flip();
        return byteBuffer;
    }

    static ByteBuffer getByteBuffer(byte[] bytes, int offset, int length) {
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(length);
        byteBuffer.put(bytes, offset, length);
        byteBuffer.flip();
        return byteBuffer;
    }

    static ByteBuffer getByteBuffer(short[] shorts) {
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(shorts.length * 2);
        for (short s : shorts) {
            byteBuffer.putShort(s);
        }
        byteBuffer.flip();
        return byteBuffer;
    }

    int getOutputFrequency() throws IOException;
    int getOutputChannels() throws IOException;
    void close() throws Exception;
    @Nullable ByteBuffer decodeFrame() throws Exception;
}
