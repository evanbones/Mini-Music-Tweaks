package com.evandev.music_tweaks.client.jukebox;

import net.minecraft.client.sounds.AudioStream;
import org.jspecify.annotations.NonNull;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;

public class OffsetAudioStream implements AudioStream {
    private final AudioStream delegate;
    private final AudioFormat format;
    private final long bytesToSkip;
    private boolean skipped = false;

    public OffsetAudioStream(AudioStream delegate, float skipSeconds) {
        this.delegate = delegate;
        this.format = delegate.getFormat();
        float bytesPerSecond = this.format.getSampleRate() * this.format.getChannels() * (this.format.getSampleSizeInBits() / 8.0F);
        this.bytesToSkip = (long) (skipSeconds * bytesPerSecond);
    }

    @Override
    public @NonNull AudioFormat getFormat() {
        return this.format;
    }

    @Override
    public @NonNull ByteBuffer read(int capacity) throws IOException {
        if (!this.skipped) {
            this.skipped = true;
            ByteBuffer buf;
            for (long remaining = this.bytesToSkip; remaining > 0L; remaining -= buf.remaining()) {
                int chunk = (int) Math.min(remaining, capacity);
                buf = this.delegate.read(chunk);
                if (buf.remaining() == 0) {
                    break;
                }
            }
        }
        return this.delegate.read(capacity);
    }

    @Override
    public void close() throws IOException {
        this.delegate.close();
    }
}