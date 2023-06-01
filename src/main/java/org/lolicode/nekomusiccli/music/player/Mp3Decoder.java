package org.lolicode.nekomusiccli.music.player;

import javazoom.jl.decoder.*;
import org.lolicode.nekomusiccli.libs.flac.decode.DataFormatException;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class Mp3Decoder extends javazoom.jl.decoder.Decoder implements Decoder {
    private final Bitstream bitstream;
    private volatile boolean closed = false;
    public Mp3Decoder(InputStream inputStream) throws BitstreamException, DataFormatException {
        super();
        this.bitstream = new Bitstream(inputStream);
        var header = bitstream.readFrame();
        if (header == null) {
            throw new DataFormatException("Failed to read header");
        }
        bitstream.closeFrame();
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
