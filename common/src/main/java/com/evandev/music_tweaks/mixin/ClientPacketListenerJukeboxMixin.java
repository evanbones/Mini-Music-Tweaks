package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.client.jukebox.JukeboxOffsetState;
import com.evandev.music_tweaks.client.jukebox.JukeboxSyncHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.resources.RegistryOps;
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
                long ticksSinceStart = nbt.getLong("ticks_since_song_started").orElse(0L);
                Optional<CompoundTag> recordNbtOpt = nbt.getCompound("RecordItem");

                if (recordNbtOpt.isPresent() && !recordNbtOpt.get().isEmpty()) {
                    Minecraft client = Minecraft.getInstance();
                    RegistryOps<Tag> registryOps = client.level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
                    Optional<ItemStack> recordItemOpt = ItemStack.OPTIONAL_CODEC.parse(registryOps, recordNbtOpt.get()).result();

                    if (recordItemOpt.isPresent() && !recordItemOpt.get().isEmpty()) {
                        JukeboxSyncHandler.playSyncedSong(pos, ticksSinceStart, recordItemOpt.get());
                        return;
                    }
                }
            }

            JukeboxSyncHandler.playFallback(pos);
        }
    }
}
