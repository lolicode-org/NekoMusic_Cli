package org.lolicode.nekomusiccli.music.player;

import javazoom.jl.decoder.*;
import org.lolicode.nekomusiccli.libs.flac.decode.DataFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Mp3Decoder extends javazoom.jl.decoder.Decoder implements Decoder {
    private final Bitstream bitstream;
    private volatile boolean closed = false;
    private final int sampleRate;

    public Mp3Decoder(InputStream inputStream) throws DataFormatException, BitstreamException {
        super();
        this.bitstream = new Bitstream(inputStream);
        Header header = null;
        try {
            header = bitstream.readFrame();
            if (header == null) {
                throw new DataFormatException("Failed to read header");
            }
            this.sampleRate = header.frequency();
        } catch (BitstreamException e) {
            bitstream.closeFrame();
            bitstream.close();
            throw e;
        }
        bitstream.closeFrame();
    }

    @Override
    public synchronized void seek(long pos) throws IOException {
        if (closed) {
            return;
        }
        var frames = pos * sampleRate / 1152;
        for (int i = 0; i < frames; i++) {
            try {
                bitstream.readFrame();
            } catch (BitstreamException e) {
                throw new IOException(e);
            }
            bitstream.closeFrame();
        }
    }

    @Override
    public synchronized void close() throws Exception {
        bitstream.close();
        closed = true;
    }

    @Override
    public synchronized ByteBuffer decodeFrame() throws Exception {
        if (closed) {
            return null;
        }
        var frame = bitstream.readFrame();
        if (frame == null) {
            return null;
        }
        SampleBuffer buffer = (SampleBuffer) super.decodeFrame(frame, bitstream);
        ByteBuffer byteBuffer = Decoder.getByteBuffer(buffer.getBuffer());
        bitstream.closeFrame();
        return byteBuffer;
    }
}
