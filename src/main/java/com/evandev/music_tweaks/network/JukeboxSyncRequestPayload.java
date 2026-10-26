package com.evandev.music_tweaks.network;

import com.evandev.music_tweaks.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

//? if >=1.21 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
//?}

//? if >=1.21 {
public record JukeboxSyncRequestPayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<JukeboxSyncRequestPayload> TYPE = new Type<>(Constants.id("jukebox_sync_request"));
    public static final StreamCodec<FriendlyByteBuf, JukeboxSyncRequestPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, JukeboxSyncRequestPayload::pos,
            JukeboxSyncRequestPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
//?} else {
/*public record JukeboxSyncRequestPayload(BlockPos pos) {
*///?}
    public static final ResourceLocation ID = Constants.id("jukebox_sync_request");

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    public static JukeboxSyncRequestPayload read(FriendlyByteBuf buf) {
        return new JukeboxSyncRequestPayload(buf.readBlockPos());
    }
}
