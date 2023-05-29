package org.lolicode.nekomusiccli.music.player;

import org.lolicode.nekomusiccli.libs.flac.decode.ByteArrayFlacInput;
import org.lolicode.nekomusiccli.libs.flac.decode.DataFormatException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

public class FlacDecoder extends org.lolicode.nekomusiccli.libs.flac.decode.FlacDecoder implements Decoder {
    private volatile boolean metadataRead = false;
    /**
     * Constructs a new FLAC decoder from the given input stream.
     * @param in the input stream to read from
     * @throws IOException if an I/O exception occurred
     */
    public FlacDecoder(ByteArrayInputStream in) throws IOException, DataFormatException {
        super();
        Objects.requireNonNull(in);
        super.input = new ByteArrayFlacInput(in.readAllBytes());
        if (input.readUint(32) != 0x664C6143)  // Magic string "fLaC"
            throw new DataFormatException("Invalid magic string");
        super.metadataEndPos = -1;
    }

    private synchronized void handleMetadata() throws IOException {
        if (metadataRead) {
            return;
        }
        while (super.readAndHandleMetadataBlock() != null) {
            // Do nothing
        }
        metadataRead = true;
    }

    @Override
    public int getOutputFrequency() throws IOException {
        if (!metadataRead) handleMetadata();
        return super.streamInfo.sampleRate;
    }

    @Override
    public int getOutputChannels() throws IOException {
        if (!metadataRead) handleMetadata();
        return super.streamInfo.numChannels;
    }

    @Override
    public synchronized ByteBuffer decodeFrame() throws Exception {
        if (!metadataRead) handleMetadata();
        int[][] samples = new int[super.streamInfo.numChannels][];
        byte[] sampleBytes = new byte[super.streamInfo.maxBlockSize * super.streamInfo.numChannels * super.streamInfo.sampleDepth / 8];

        int blockSamples = readAudioBlock(samples, 0);
        int sampleBytesLen = 0;
        for (int i = 0; i < blockSamples; i++) {
            for (int ch = 0; ch < streamInfo.numChannels; ch++) {
                int val = samples[ch][i];
                if (streamInfo.sampleDepth == 24) {
                    float temp = val / 16777216f;
                    val = (int) (temp * 0x7FFF);
                } else if (streamInfo.sampleDepth == 32) {
                    float temp = val / 1099511627776f;
                    val = (int) (temp * 0x7FFF);
                }
                for (int j = 0; j < 2; j++, sampleBytesLen++)
                    sampleBytes[sampleBytesLen] = (byte) (val >>> (j << 3));
            }
        }
        return ByteBuffer.wrap(sampleBytes, 0, sampleBytesLen);
    }
}
