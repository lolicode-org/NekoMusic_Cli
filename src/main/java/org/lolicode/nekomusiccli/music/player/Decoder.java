package org.lolicode.nekomusiccli.music.player;

import javazoom.jl.decoder.BitstreamException;
import org.lolicode.nekomusiccli.libs.flac.decode.DataFormatException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface Decoder extends AutoCloseable {
    static Decoder getDecoder(ByteArrayInputStream inputStream) throws DataFormatException, IOException {
        if (inputStream == null) {
            throw new NullPointerException("inputStream is null");
        }
        try {
            return new FlacDecoder(inputStream);
        } catch (DataFormatException e) {
            try {
                inputStream.reset();
                return new Mp3Decoder(inputStream);
            } catch (DataFormatException | BitstreamException ex) {
                inputStream.reset();
                return new OggDecoder(inputStream);
            }
        }
    }

    int getOutputFrequency() throws IOException;
    int getOutputChannels() throws IOException;
    void close() throws Exception;
    ByteBuffer decodeFrame() throws Exception;
}
