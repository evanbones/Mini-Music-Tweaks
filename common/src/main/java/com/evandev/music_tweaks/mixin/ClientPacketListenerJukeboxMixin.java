package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.client.jukebox.JukeboxOffsetState;
import com.evandev.music_tweaks.client.jukebox.JukeboxSyncHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerJukeboxMixin {

    @Inject(method = "handleTagQueryPacket", at = @At("HEAD"), cancellable = true)
    private void jukeboxSync_onNbtQueryResponse(ClientboundTagQueryPacket packet, CallbackInfo ci) {
        int transactionId = packet.getTransactionId();
        BlockPos pos = JukeboxOffsetState.resolveQuery(transactionId);

        if (pos != null) {
            ci.cancel();
            CompoundTag nbt = packet.getTag();
            if (nbt != null) {
                long ticksSinceStart = nbt.getLong("ticks_since_song_started");
                CompoundTag recordNbt = nbt.getCompound("RecordItem");

                if (!recordNbt.isEmpty()) {
                    Minecraft client = Minecraft.getInstance();
                    Optional<ItemStack> recordItemOpt = ItemStack.parse(client.level.registryAccess(), recordNbt);

                    if (recordItemOpt.isPresent() && !recordItemOpt.get().isEmpty()) {
                        JukeboxSyncHandler.playSyncedSong(pos, ticksSinceStart, recordItemOpt.get());
                    }
                }
            }
        }
    }
}