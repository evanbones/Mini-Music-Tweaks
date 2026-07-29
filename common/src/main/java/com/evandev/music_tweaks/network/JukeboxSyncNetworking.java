package com.evandev.music_tweaks.network;

import com.evandev.music_tweaks.Constants;
import com.evandev.music_tweaks.client.jukebox.JukeboxOffsetState;
import com.evandev.music_tweaks.client.jukebox.JukeboxSyncHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;

import java.util.function.BiConsumer;

public final class JukeboxSyncNetworking {
    public static final ResourceLocation REQUEST_ID = new ResourceLocation(Constants.MOD_ID, "jukebox_sync_request");
    public static final ResourceLocation RESPONSE_ID = new ResourceLocation(Constants.MOD_ID, "jukebox_sync_response");
    public static final ResourceLocation CHANNEL_ID = new ResourceLocation(Constants.MOD_ID, "jukebox_sync");

    private JukeboxSyncNetworking() {
    }

    public static void writeRequest(FriendlyByteBuf buf, BlockPos pos) {
        buf.writeBlockPos(pos);
    }

    public static BlockPos readRequest(FriendlyByteBuf buf) {
        return buf.readBlockPos();
    }

    public static void writeResponse(FriendlyByteBuf buf, BlockPos pos, long ticksSinceStart, ItemStack recordItem) {
        buf.writeBlockPos(pos);
        buf.writeVarLong(ticksSinceStart);
        buf.writeItem(recordItem);
    }

    public static BlockPos readResponsePos(FriendlyByteBuf buf) {
        return buf.readBlockPos();
    }

    public static long readResponseTicks(FriendlyByteBuf buf) {
        return buf.readVarLong();
    }

    public static ItemStack readResponseItem(FriendlyByteBuf buf) {
        return buf.readItem();
    }

    public static void handleRequestOnServer(ServerLevel level, BlockPos pos, BiConsumer<Long, ItemStack> responder) {
        if (level.getBlockEntity(pos) instanceof JukeboxBlockEntity jukebox) {
            CompoundTag nbt = jukebox.saveWithFullMetadata();
            long ticks = nbt.getLong("ticks_since_song_started");
            CompoundTag recordNbt = nbt.getCompound("RecordItem");

            if (!recordNbt.isEmpty()) {
                ItemStack item = ItemStack.of(recordNbt);
                if (!item.isEmpty()) {
                    responder.accept(ticks, item);
                }
            }
        }
    }

    public static void handleResponseOnClient(BlockPos pos, long ticksSinceStart, ItemStack recordItem) {
        JukeboxOffsetState.resolveCustomQuery(pos);
        JukeboxSyncHandler.playSyncedSong(pos, ticksSinceStart, recordItem);
    }
}
