package org.lolicode.nekomusiccli.music.player;

import okhttp3.Response;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.music.MusicManager;
import org.lolicode.nekomusiccli.music.MusicObj;
import org.lolicode.nekomusiccli.utils.Alert;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import java.io.ByteArrayInputStream;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class AudioPlayer implements AutoCloseable {
    protected final int source;
    protected Decoder decoder = null;
    private final BlockingQueue<IntBuffer> queue = new LinkedBlockingQueue<>();
    private Thread decodeThread = null;
    private Thread playbackThread = null;
    private volatile boolean playbackRunning = false;
    protected volatile boolean cleanupDone = false;

    public static AudioPlayer getAudioPlayerStream(MusicObj music) throws InterruptedIOException {
        if (music == null || music.url == null || music.url.isBlank()) throw new IllegalArgumentException("MusicObj is null or url is null or blank");
        String url = music.url.toLowerCase();
        if (!url.endsWith(".mp3") && !url.endsWith(".flac")) return null;
        Response response = null;
        try {
            // if use try-with-resource, the response will be closed when the try block is exited, before it's actually consumed
            response = NekoMusicClient.netUtils.getMusicResponse(music);
            if (response == null) {
                return null;
            }
            assert response.body() != null;
            return ResponseAudioPlayer.getMp3OrFlacAudioPlayer(response, url.endsWith(".mp3"));
        } catch (InterruptedIOException e) {
            if (response != null) response.close();
            throw e;
        } catch (Exception e) {
            if (response != null) response.close();
            NekoMusicClient.LOGGER.error("Failed to get music stream", e);
            Alert.error("player.nekomusic.stream.failed");
        }
        return null;
    }

    public static AudioPlayer getAudioPlayerNoStream(MusicObj music) throws InterruptedIOException {
        if (music == null || music.url == null || music.url.isBlank()) throw new IllegalArgumentException("MusicObj is null or url is null or blank");
        try (Response resp = NekoMusicClient.netUtils.getMusicResponse(music)) {
            // response consumed here, so can use try-with-resource
            if (resp == null || resp.body() == null) {
                return null;
            }
            var stream = new ByteArrayInputStream(resp.body().bytes());
            return new ByteArrayInputStreamAudioPlayer(stream);
        } catch (InterruptedIOException e) {
            throw e;
        } catch (Exception e) {
            NekoMusicClient.LOGGER.error("Failed to get music bytes", e);
            Alert.error("player.nekomusic.stream.failed");
        }
        return null;
    }

    public AudioPlayer() {
        this.source = AL10.alGenSources();
    }

    public synchronized void play() {
        if (playbackRunning) {
            return;  // Prevent multiple playbacks
        }
        playbackRunning = true;
        this.decodeThread = new Thread(this::decodeLoop, NekoMusicClient.MOD_NAME + " Audio-Decode-Loop");
        this.decodeThread.start();
        this.playbackThread = new Thread(this::playBackLoop, NekoMusicClient.MOD_NAME + " Audio-Playback-Loop");
        this.playbackThread.start();
    }

    public synchronized void stop() {
        try {
            if (this.decoder != null)
                this.decoder.close();
        } catch (Exception e) {
            NekoMusicClient.LOGGER.error("Failed to close the decoder", e);
        }
        playbackRunning = false;  // Stop the playback thread
        if (this.decodeThread != null) {
            this.decodeThread.interrupt();
        }
        if (this.playbackThread != null) {
            this.playbackThread.interrupt();
        }
    }

    public synchronized void cleanup() {
        if (cleanupDone) {
            return;  // Prevent multiple cleanups
        }

        this.stop();

        AL10.alSourceStop(this.source);
        AL10.alSourceUnqueueBuffers(this.source);
        AL10.alDeleteSources(this.source);
        queue.forEach(AL10::alDeleteBuffers);

        cleanupDone = true;  // Mark the cleanup as done
    }

    private void decodeLoop() {
        try {
            while (playbackRunning) {
                ByteBuffer buffer = decoder.decodeFrame();
                if (buffer == null) {
                    break;
                }
                IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
                AL10.alGenBuffers(intBuffer);
                AL10.alBufferData(intBuffer.get(0), decoder.getOutputChannels() == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, buffer, decoder.getOutputFrequency());
                AL10.alSourcef(source, AL10.AL_GAIN, MusicManager.getVolume());
                queue.put(intBuffer);
            }
        } catch (InterruptedException ignored) {
        } catch (Exception e) {
            NekoMusicClient.LOGGER.error("Failed to decode the audio stream", e);
            Alert.error("player.nekomusic.decode.failed");
        } finally {
            try {
                decoder.close();
            } catch (Exception e) {
                NekoMusicClient.LOGGER.error("Failed to close the decoder", e);
            }
        }
    }

    private void playBackLoop() {
        try {
            while (playbackRunning) {
                IntBuffer buffer = queue.take();  // This blocks if the queue is empty
                AL10.alSourceQueueBuffers(source, buffer);
                if (AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING) {
                    AL10.alSourcePlay(source);
                }
            }
        } catch (InterruptedException ignored) {
        } finally {
            cleanup();
        }
    }

    public void setGain(float volume) throws IllegalStateException, IllegalArgumentException {
        if (volume < 0.0f || volume > 1.0f) {
            throw new IllegalArgumentException("Volume not valid: " + volume);
        }
        if (this.cleanupDone) {
            throw new IllegalStateException("Cannot set gain after cleanup");
        }
        AL10.alSourcef(this.source, AL10.AL_GAIN, volume);
    }

    @Override
    public void close() throws Exception {
        cleanup();
    }
}
