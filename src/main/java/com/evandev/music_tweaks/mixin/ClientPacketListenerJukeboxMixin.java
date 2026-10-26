package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.client.jukebox.JukeboxOffsetState;
import com.evandev.music_tweaks.client.jukebox.JukeboxSyncHandler;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=1.21 {
import net.minecraft.client.Minecraft;

import java.util.Optional;
//?} else {
/*import com.evandev.music_tweaks.network.JukeboxSyncServer;
*///?}
//? if >=26.1 {
/*import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
*///?}

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
                //? if >=26.1 {
                /*long ticksSinceStart = nbt.getLong("ticks_since_song_started").orElse(0L);
                Optional<CompoundTag> recordNbt = nbt.getCompound("RecordItem");
                if (recordNbt.isPresent() && !recordNbt.get().isEmpty()) {
                    RegistryOps<Tag> registryOps = Minecraft.getInstance().level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
                    ItemStack recordItem = ItemStack.OPTIONAL_CODEC.parse(registryOps, recordNbt.get()).result().orElse(ItemStack.EMPTY);
                *///?} else if >=1.21 {
                long ticksSinceStart = nbt.getLong("ticks_since_song_started");
                CompoundTag recordNbt = nbt.getCompound("RecordItem");
                if (!recordNbt.isEmpty()) {
                    ItemStack recordItem = ItemStack.parse(Minecraft.getInstance().level.registryAccess(), recordNbt).orElse(ItemStack.EMPTY);
                //?} else {
                /*long ticksSinceStart = JukeboxSyncServer.ticksSinceSongStarted(nbt);
                CompoundTag recordNbt = nbt.getCompound("RecordItem");
                if (!recordNbt.isEmpty()) {
                    ItemStack recordItem = ItemStack.of(recordNbt);
                *///?}
                    if (!recordItem.isEmpty()) {
                        JukeboxSyncHandler.playSyncedSong(pos, ticksSinceStart, recordItem);
                        return;
                    }
                }
            }

            JukeboxSyncHandler.playFallback(pos);
        }
    }
}
