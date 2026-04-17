package com.evandev.music_tweaks.client.jukebox;

public final class PendingStreamOffset {
    private static volatile float pending = 0.0F;

    private PendingStreamOffset() {
    }

    public static void set(float seconds) {
        pending = seconds;
    }

    public static float consume() {
        float val = pending;
        pending = 0.0F;
        return val;
    }
}