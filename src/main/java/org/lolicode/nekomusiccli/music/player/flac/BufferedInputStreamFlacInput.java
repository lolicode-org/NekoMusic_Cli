package org.lolicode.nekomusiccli.music.player.flac;

import org.lolicode.nekomusiccli.libs.flac.decode.AbstractFlacLowLevelInput;

import java.io.BufferedInputStream;
import java.io.IOException;

public class BufferedInputStreamFlacInput extends AbstractFlacLowLevelInput {
    private BufferedInputStream inputStream;

    public BufferedInputStreamFlacInput(BufferedInputStream inputStream) {
        super();
        this.inputStream = inputStream;
    }

    @Override
    protected int readUnderlying(byte[] buf, int off, int len) throws IOException {
        return inputStream.read(buf, off, len);
    }

    @Override
    public long getLength() {
        try {
            return inputStream.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void seekTo(long pos) throws IOException {
        positionChanged(inputStream.skip(pos));
    }

    @Override
    public void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
            inputStream = null;
            super.close();
        }
    }
}
