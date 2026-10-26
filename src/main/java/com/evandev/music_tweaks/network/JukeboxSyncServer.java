package com.evandev.music_tweaks.network;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;

import java.util.Optional;

//? if <1.21 {
/*import net.minecraft.nbt.CompoundTag;
*///?}

public class JukeboxSyncServer {
    public static Optional<JukeboxSyncResponsePayload> createResponse(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof JukeboxBlockEntity jukebox)) {
            return Optional.empty();
        }

        //? if >=1.21 {
        ItemStack record = jukebox.getTheItem();
        long ticks = jukebox.getSongPlayer().isPlaying() ? jukebox.getSongPlayer().getTicksSinceSongStarted() : 0L;
        //?} else {
        /*CompoundTag nbt = jukebox.saveWithFullMetadata();
        ItemStack record = ItemStack.of(nbt.getCompound("RecordItem"));
        long ticks = ticksSinceSongStarted(nbt);
        *///?}

        if (record.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new JukeboxSyncResponsePayload(pos, ticks, record.copy()));
    }

    //? if <1.21 {
    /*public static long ticksSinceSongStarted(CompoundTag jukeboxNbt) {
        if (!jukeboxNbt.getBoolean("IsPlaying")) {
            return 0L;
        }
        return Math.max(0L, jukeboxNbt.getLong("TickCount") - jukeboxNbt.getLong("RecordStartTick"));
    }
    *///?}
}
