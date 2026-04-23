package com.evandev.music_tweaks.network;

import com.evandev.music_tweaks.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record JukeboxSyncRequestPayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<JukeboxSyncRequestPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "jukebox_sync_request"));
    public static final StreamCodec<FriendlyByteBuf, JukeboxSyncRequestPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, JukeboxSyncRequestPayload::pos,
            JukeboxSyncRequestPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}