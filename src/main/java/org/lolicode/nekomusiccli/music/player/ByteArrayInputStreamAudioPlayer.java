package org.lolicode.nekomusiccli.music.player;

import org.lolicode.nekomusiccli.NekoMusicClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ByteArrayInputStreamAudioPlayer extends AudioPlayer {
    private final ByteArrayInputStream stream;

    public ByteArrayInputStreamAudioPlayer(ByteArrayInputStream stream) throws RuntimeException, IOException {
        super();
        this.stream = stream;

        try {
            this.decoder = Decoder.getDecoder(stream);
        } catch (RuntimeException | IOException e) {
            NekoMusicClient.LOGGER.error("Failed to open the audio stream", e);
            cleanup();
            throw e;
        }
    }

    @Override
    public synchronized void cleanup() {
        if (this.cleanupDone) {
            return;  // Prevent multiple cleanups
        }
        super.cleanup();
        try {
            this.stream.close();  // Close the stream
        } catch (IOException e) {
            NekoMusicClient.LOGGER.error("Failed to close the audio stream", e);
        }
        cleanupDone = true;  // Mark the cleanup as done
    }
}
