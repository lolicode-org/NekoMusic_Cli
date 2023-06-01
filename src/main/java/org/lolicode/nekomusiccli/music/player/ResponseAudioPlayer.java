package org.lolicode.nekomusiccli.music.player;

import javazoom.jl.decoder.BitstreamException;
import okhttp3.Response;
import org.lolicode.nekomusiccli.NekoMusicClient;

import java.io.BufferedInputStream;
import java.io.IOException;

public class ResponseAudioPlayer extends AudioPlayer {
    private final BufferedInputStream stream;
    private final Response response;

    public ResponseAudioPlayer() {
        throw new UnsupportedOperationException("This class can't be directly instantiated");
    }

    private ResponseAudioPlayer(BufferedInputStream stream, Decoder decoder, Response response) {
        super();
        this.stream = stream;
        this.decoder = decoder;
        this.response = response;
    }

    /**
     * Get an audio player from a Response
     * <p></p>
     * This method <strong>WILL</strong> close the stream if an error occurs.
     * @param resp The response
     * @return The audio player
     * @throws Exception If an error occurs
     */
    public static ResponseAudioPlayer getMp3OrFlacAudioPlayer(Response resp, boolean isMp3) throws Exception {
        Decoder decoder = null;
        var stream = new BufferedInputStream(resp.body().byteStream());
        try {
            if (isMp3) {
                decoder = Decoder.getMp3Decoder(stream);
            } else {
                decoder = Decoder.getFlacDecoder(stream);
            }
        } catch (RuntimeException | BitstreamException | IOException e) {
            // noinspection ConstantConditions
            if (decoder != null) decoder.close();
            stream.close();
            return null;
        }
        var player = new ResponseAudioPlayer(stream, decoder, resp);
        player.decoder = decoder;
        return player;
    }

    @Override
    public synchronized void cleanup() {
        if (super.cleanupDone) {
            return;  // Prevent multiple cleanups
        }
        super.cleanup();
        try {
            this.stream.close();  // Close the stream
        } catch (IOException e) {
            NekoMusicClient.LOGGER.error("Failed to close the audio stream", e);
        }
        try {
            this.response.close();
        } catch (Exception e) {
            NekoMusicClient.LOGGER.error("Failed to close the response", e);
        }
        cleanupDone = true;  // Mark the cleanup as done
    }
}
