package com.evandev.music_tweaks.client.jukebox;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
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
                        Optional<Holder<JukeboxSong>> songEntry = JukeboxSong.fromStack(world.registryAccess(), recordItem);

                        if (songEntry.isPresent()) {
                            SoundEvent soundEvent = songEntry.get().value().soundEvent().value();
                            TagKey<JukeboxSong> ambientTag = TagKey.create(Registries.JUKEBOX_SONG, ResourceLocation.parse("quark:ambient"));

                            if (songEntry.get().is(ambientTag)) {
                                SimpleSoundInstance instance = new SimpleSoundInstance(
                                        soundEvent.getLocation(), SoundSource.RECORDS, 4.0F, 1.0F,
                                        SoundInstance.createUnseededRandom(), true, 0,
                                        SoundInstance.Attenuation.LINEAR, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, false
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
        });
    }
}