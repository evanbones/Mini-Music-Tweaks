package com.evandev.music_tweaks.client.jukebox;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class JukeboxOffsetState {
    private static final Set<SoundInstance> OUR_SOUNDS = ConcurrentHashMap.newKeySet();
    private static final Map<BlockPos, SoundInstance> ACTIVE_SOUNDS = new ConcurrentHashMap<>();
    private static final Map<Integer, BlockPos> PENDING_QUERIES = new ConcurrentHashMap<>();
    private static final AtomicInteger TRANSACTION_COUNTER = new AtomicInteger(0);
    private static final Set<BlockPos> QUERIED_POSITIONS = ConcurrentHashMap.newKeySet();
    private static final Set<BlockPos> UNSUPPORTED_POSITIONS = ConcurrentHashMap.newKeySet();
    private static final Map<BlockPos, SoundInstance> CANCELLED_SOUNDS = new ConcurrentHashMap<>();
    private static final Set<BlockPos> IDLE_JUKEBOXES = ConcurrentHashMap.newKeySet();

    private JukeboxOffsetState() {
    }

    public static void markOurSound(SoundInstance sound) {
        OUR_SOUNDS.add(sound);
    }

    public static boolean isOurSound(SoundInstance sound) {
        return OUR_SOUNDS.remove(sound);
    }

    public static void trackSound(BlockPos pos, SoundInstance sound) {
        ACTIVE_SOUNDS.put(pos, sound);
    }

    public static void removeSound(BlockPos pos) {
        ACTIVE_SOUNDS.remove(pos);
    }

    public static boolean hasActiveSound(BlockPos pos) {
        return ACTIVE_SOUNDS.containsKey(pos);
    }

    public static Map<BlockPos, SoundInstance> getAllActiveSounds() {
        return ACTIVE_SOUNDS;
    }

    public static int registerQuery(BlockPos pos) {
        int id = TRANSACTION_COUNTER.incrementAndGet();
        PENDING_QUERIES.put(id, pos);
        QUERIED_POSITIONS.add(pos);
        return id;
    }

    public static BlockPos resolveQuery(int transactionId) {
        BlockPos pos = PENDING_QUERIES.remove(transactionId);
        if (pos != null) {
            QUERIED_POSITIONS.remove(pos);
        }
        return pos;
    }

    public static boolean hasPendingQuery(BlockPos pos) {
        return QUERIED_POSITIONS.contains(pos);
    }

    public static void cancelQuery(BlockPos pos) {
        QUERIED_POSITIONS.remove(pos);
        UNSUPPORTED_POSITIONS.remove(pos);
        PENDING_QUERIES.entrySet().removeIf(e -> e.getValue().equals(pos));
        CANCELLED_SOUNDS.remove(pos);
        IDLE_JUKEBOXES.remove(pos);
    }

    public static void markUnsupported(BlockPos pos) {
        UNSUPPORTED_POSITIONS.add(pos);
    }

    public static boolean isUnsupported(BlockPos pos) {
        return UNSUPPORTED_POSITIONS.contains(pos);
    }

    public static void addCancelledSound(BlockPos pos, SoundInstance sound) {
        CANCELLED_SOUNDS.put(pos, sound);
    }

    public static SoundInstance getAndRemoveCancelledSound(BlockPos pos) {
        return CANCELLED_SOUNDS.remove(pos);
    }

    public static void removeCancelledSound(BlockPos pos) {
        CANCELLED_SOUNDS.remove(pos);
    }

    public static void markIdle(BlockPos pos) {
        IDLE_JUKEBOXES.add(pos);
    }

    public static void clearIdle(BlockPos pos) {
        IDLE_JUKEBOXES.remove(pos);
    }

    public static boolean isIdle(BlockPos pos) {
        return IDLE_JUKEBOXES.contains(pos);
    }
}
