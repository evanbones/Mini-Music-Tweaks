package com.evandev.music_tweaks.client.jukebox;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class JukeboxSyncHandler {
    public static void playSyncedSong(BlockPos pos, long ticksSinceStart, ItemStack recordItem) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> {
            Level world = client.level;
            if (world != null) {
                BlockState state = world.getBlockState(pos);
                if (state.hasProperty(JukeboxBlock.HAS_RECORD) && state.getValue(JukeboxBlock.HAS_RECORD)) {
                    if (!JukeboxOffsetState.hasActiveSound(pos)) {
                        Optional<Holder<JukeboxSong>> songEntry = JukeboxSong.fromStack(recordItem);

                        if (songEntry.isPresent()) {
                            JukeboxOffsetState.removeCancelledSound(pos);
                            SoundEvent soundEvent = songEntry.get().value().soundEvent().value();
                            float offsetSeconds = (float) ticksSinceStart / 20.0F;

                            OffsetSoundInstance instance = new OffsetSoundInstance(
                                    soundEvent, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D
                            );
                            JukeboxOffsetState.markOurSound(instance);
                            PendingStreamOffset.set(offsetSeconds);
                            client.getSoundManager().play(instance);
                            JukeboxOffsetState.trackSound(pos, instance);
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
