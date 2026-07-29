package com.evandev.music_tweaks.client.jukebox;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;

public class JukeboxSyncHandler {
    public static void playSyncedSong(BlockPos pos, long ticksSinceStart, ItemStack recordItem) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> {
            Level world = client.level;
            if (world != null) {
                BlockState state = world.getBlockState(pos);
                if (state.hasProperty(JukeboxBlock.HAS_RECORD) && state.getValue(JukeboxBlock.HAS_RECORD)) {
                    if (!JukeboxOffsetState.hasActiveSound(pos)) {
                        if (!recordItem.isEmpty() && recordItem.getItem() instanceof RecordItem record) {
                            JukeboxOffsetState.removeCancelledSound(pos);
                            SoundEvent soundEvent = record.getSound();

                            TagKey<Item> ambientTag = TagKey.create(
                                    Registries.ITEM,
                                    new ResourceLocation("quark", "ambient")
                            );

                            if (recordItem.is(ambientTag)) {
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
                        } else {
                            playFallback(pos);
                        }
                    } else {
                        JukeboxOffsetState.removeCancelledSound(pos);
                    }
                } else {
                    JukeboxOffsetState.removeCancelledSound(pos);
                }
            }
        });
    }

    public static void playFallback(BlockPos pos) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> {
            SoundInstance original = JukeboxOffsetState.getAndRemoveCancelledSound(pos);
            if (original != null) {
                JukeboxOffsetState.markOurSound(original);
                client.getSoundManager().play(original);
                JukeboxOffsetState.trackSound(pos, original);
            } else {
                JukeboxOffsetState.markIdle(pos);
            }
        });
    }
}
