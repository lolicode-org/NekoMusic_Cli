package org.lolicode.nekomusiccli.music.player;

import javazoom.jl.decoder.*;
import javazoom.jl.decoder.Decoder;
import org.lolicode.nekomusiccli.libs.flac.decode.DataFormatException;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

public class Mp3Decoder extends Decoder implements org.lolicode.nekomusiccli.music.player.Decoder {
    private final Bitstream bitstream;
    public Mp3Decoder(ByteArrayInputStream inputStream) throws BitstreamException, DataFormatException {
        super();
        this.bitstream = new Bitstream(inputStream);
        var header = bitstream.readFrame();
        if (header == null) {
            throw new DataFormatException("Failed to read header");
        }
    }

    @Override
    public synchronized void close() throws Exception {
        bitstream.close();
    }

    @Override
    public synchronized ByteBuffer decodeFrame() throws Exception {
        var frame = bitstream.readFrame();
        if (frame == null) {
            return null;
        }
        SampleBuffer buffer = (SampleBuffer) super.decodeFrame(frame, bitstream);
        ByteBuffer byteBuffer = ByteBuffer.allocate(2 * buffer.getBufferLength());
        for (short s : buffer.getBuffer()) {
            byteBuffer.putShort(s);
        }
        bitstream.closeFrame();
        return byteBuffer;
    }
}
