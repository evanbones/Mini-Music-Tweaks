package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.client.jukebox.JukeboxOffsetState;
import com.evandev.music_tweaks.client.jukebox.OffsetSoundInstance;
import com.evandev.music_tweaks.client.jukebox.PendingStreamOffset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
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
                    client.execute(() -> {
                        Level world = client.level;
                        if (world != null) {
                            BlockState state = world.getBlockState(pos);
                            if (state.hasProperty(JukeboxBlock.HAS_RECORD) && state.getValue(JukeboxBlock.HAS_RECORD)) {
                                if (!JukeboxOffsetState.hasActiveSound(pos)) {
                                    Optional<ItemStack> recordItemOpt = ItemStack.parse(world.registryAccess(), recordNbt);

                                    if (recordItemOpt.isPresent() && !recordItemOpt.get().isEmpty()) {
                                        ItemStack recordItem = recordItemOpt.get();
                                        Optional<Holder<JukeboxSong>> songEntry = JukeboxSong.fromStack(world.registryAccess(), recordItem);

                                        if (songEntry.isPresent()) {
                                            SoundEvent soundEvent = songEntry.get().value().soundEvent().value();

                                            TagKey<JukeboxSong> ambientTag = TagKey.create(
                                                    Registries.JUKEBOX_SONG,
                                                    ResourceLocation.parse("quark:ambient")
                                            );

                                            if (songEntry.get().is(ambientTag)) {
                                                SimpleSoundInstance instance = new SimpleSoundInstance(
                                                        soundEvent.getLocation(),
                                                        SoundSource.RECORDS,
                                                        4.0F, 1.0F,
                                                        SoundInstance.createUnseededRandom(),
                                                        true, 0,
                                                        SoundInstance.Attenuation.LINEAR,
                                                        pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                                                        false
                                                );

                                                client.getSoundManager().play(instance);

                                            } else {
                                                float offsetSeconds = (float) ticksSinceStart / 20.0F;

                                                OffsetSoundInstance instance = new OffsetSoundInstance(
                                                        soundEvent, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D
                                                );

                                                JukeboxOffsetState.markOurSound(instance);
                                                PendingStreamOffset.set(offsetSeconds);
                                                client.getSoundManager().play(instance);
                                                JukeboxOffsetState.trackSound(pos, instance);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }
    }
}