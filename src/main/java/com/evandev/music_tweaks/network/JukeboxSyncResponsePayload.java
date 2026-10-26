package com.evandev.music_tweaks.network;

import com.evandev.music_tweaks.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

//? if >=1.21 {
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
//?} else {
/*import net.minecraft.network.FriendlyByteBuf;
*///?}

//? if >=1.21 {
public record JukeboxSyncResponsePayload(BlockPos pos, long ticksSinceStart,
                                         ItemStack recordItem) implements CustomPacketPayload {
    public static final Type<JukeboxSyncResponsePayload> TYPE = new Type<>(Constants.id("jukebox_sync_response"));
    public static final StreamCodec<RegistryFriendlyByteBuf, JukeboxSyncResponsePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, JukeboxSyncResponsePayload::pos,
            ByteBufCodecs.VAR_LONG, JukeboxSyncResponsePayload::ticksSinceStart,
            ItemStack.STREAM_CODEC, JukeboxSyncResponsePayload::recordItem,
            JukeboxSyncResponsePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
//?} else {
/*public record JukeboxSyncResponsePayload(BlockPos pos, long ticksSinceStart, ItemStack recordItem) {
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeVarLong(this.ticksSinceStart);
        buf.writeItem(this.recordItem);
    }

    public static JukeboxSyncResponsePayload read(FriendlyByteBuf buf) {
        return new JukeboxSyncResponsePayload(buf.readBlockPos(), buf.readVarLong(), buf.readItem());
    }
*///?}
    public static final ResourceLocation ID = Constants.id("jukebox_sync_response");
}
