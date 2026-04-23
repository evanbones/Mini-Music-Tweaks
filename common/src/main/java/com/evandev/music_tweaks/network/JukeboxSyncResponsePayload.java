package com.evandev.music_tweaks.network;

import com.evandev.music_tweaks.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record JukeboxSyncResponsePayload(BlockPos pos, long ticksSinceStart,
                                         ItemStack recordItem) implements CustomPacketPayload {
    public static final Type<JukeboxSyncResponsePayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "jukebox_sync_response"));

    public static final StreamCodec<RegistryFriendlyByteBuf, JukeboxSyncResponsePayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, JukeboxSyncResponsePayload::pos,
            ByteBufCodecs.VAR_LONG, JukeboxSyncResponsePayload::ticksSinceStart,
            ItemStack.STREAM_CODEC, JukeboxSyncResponsePayload::recordItem,
            JukeboxSyncResponsePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}