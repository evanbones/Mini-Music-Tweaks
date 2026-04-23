package com.evandev.music_tweaks;

import com.evandev.music_tweaks.network.JukeboxSyncRequestPayload;
import com.evandev.music_tweaks.network.JukeboxSyncResponsePayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;

import java.util.Optional;

public class MiniMusicTweaks implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();

        PayloadTypeRegistry.playC2S().register(JukeboxSyncRequestPayload.ID, JukeboxSyncRequestPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(JukeboxSyncResponsePayload.ID, JukeboxSyncResponsePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(JukeboxSyncRequestPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                var level = context.player().serverLevel();
                if (level.getBlockEntity(payload.pos()) instanceof JukeboxBlockEntity jukebox) {
                    CompoundTag nbt = jukebox.saveWithFullMetadata(level.registryAccess());
                    long ticks = nbt.getLong("ticks_since_song_started");
                    CompoundTag recordNbt = nbt.getCompound("RecordItem");

                    Optional<ItemStack> recordItemOpt = ItemStack.parse(level.registryAccess(), recordNbt);
                    if (recordItemOpt.isPresent() && !recordItemOpt.get().isEmpty()) {
                        ServerPlayNetworking.send(context.player(), new JukeboxSyncResponsePayload(payload.pos(), ticks, recordItemOpt.get()));
                    }
                }
            });
        });
    }
}