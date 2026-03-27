package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.client.music.MusicClientLogic;
import com.evandev.music_tweaks.config.ModConfig;
import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Mixin(SoundEngine.class)
public abstract class SoundEngineJukeboxMixin {

    @Unique
    private static final int TICKS_TO_FULLY_FADE_OUT = 20;
    @Unique
    private static final int TICKS_TO_FULLY_FADE_IN = 40;
    @Unique
    private static final float MUSIC_VOLUME_PER_TICK_TO_FADE_OUT = 1f / TICKS_TO_FULLY_FADE_OUT;
    @Unique
    private static final float MUSIC_VOLUME_PER_TICK_TO_FADE_IN = 1f / TICKS_TO_FULLY_FADE_IN;
    @Unique
    private static final Map<SoundInstance, Vec3> coordinates = new HashMap<>();
    @Unique
    private final SoundEngineWrapper wrapper = (SoundEngineWrapper) this;

    @Unique
    private float currentMusicVolumeFactor = 1f;
    @Unique
    private float currentRecordVolumeFactor = 1f;
    @Unique
    private boolean wasMusicPaused = false;

    @Inject(method = "play", at = @At("HEAD"))
    private void injectPlay(SoundInstance sound, CallbackInfo ci) {
        if (!ModConfig.get().betterJukeboxes) return;

        if (sound.getSource() == SoundSource.RECORDS && sound instanceof AbstractSoundInstanceWrapper modifiedSound) {
            modifiedSound.setRelative(true);
            modifiedSound.setAttenuationType(SoundInstance.Attenuation.NONE);

            coordinates.put(sound, new Vec3(sound.getX(), sound.getY(), sound.getZ()));
            modifiedSound.setX(0);
            modifiedSound.setY(0);
            modifiedSound.setZ(0);
        }
    }

    @Inject(method = "tick(Z)V", at = @At("HEAD"))
    private void injectTick(boolean isPaused, CallbackInfo ci) {
        boolean inCombat = MusicClientLogic.getInstance().isInCombat();
        boolean betterJukeboxes = ModConfig.get().betterJukeboxes;

        double maxDistance = ModConfig.get().jukeboxDistance;
        double minDistance = maxDistance * 0.25;
        double minDistanceSquared = minDistance * minDistance;
        double maxDistanceSquared = maxDistance * maxDistance;
        double divisor = maxDistanceSquared - minDistanceSquared;

        Collection<SoundInstance> records = wrapper.getInstanceBySource().get(SoundSource.RECORDS);
        Vec3 playerPosition = wrapper.getListener().getListenerPosition();

        long amountRecordsHearable = 0;

        for (SoundInstance sound : records) {
            if (betterJukeboxes && coordinates.containsKey(sound)) {
                double distanceSquared = playerPosition.distanceToSqr(coordinates.get(sound));
                if (distanceSquared < maxDistanceSquared) {
                    amountRecordsHearable++;
                }
            } else if (!betterJukeboxes) {
                amountRecordsHearable++;
            }
        }

        boolean hasHearableRecords = amountRecordsHearable > 0;

        float targetRecordVolume = inCombat ? 0.0f : 1.0f;
        float targetMusicVolume = (inCombat || hasHearableRecords) ? 0.0f : 1.0f;

        // Jukebox Fade
        if (currentRecordVolumeFactor < targetRecordVolume) {
            currentRecordVolumeFactor = Math.min(currentRecordVolumeFactor + MUSIC_VOLUME_PER_TICK_TO_FADE_IN, targetRecordVolume);
        } else if (currentRecordVolumeFactor > targetRecordVolume) {
            currentRecordVolumeFactor = Math.max(currentRecordVolumeFactor - MUSIC_VOLUME_PER_TICK_TO_FADE_OUT, targetRecordVolume);
        }

        // Vanilla Music Fade
        if (currentMusicVolumeFactor < targetMusicVolume) {
            currentMusicVolumeFactor = Math.min(currentMusicVolumeFactor + MUSIC_VOLUME_PER_TICK_TO_FADE_IN, targetMusicVolume);
            musicTweaks$setMusicVolumeAndHandlePausing();
        } else if (currentMusicVolumeFactor > targetMusicVolume) {
            currentMusicVolumeFactor = Math.max(currentMusicVolumeFactor - MUSIC_VOLUME_PER_TICK_TO_FADE_OUT, targetMusicVolume);
            musicTweaks$setMusicVolumeAndHandlePausing();
        } else if (currentMusicVolumeFactor == 1.0f && wasMusicPaused) {
            musicTweaks$setMusicVolumeAndHandlePausing();
        } else if (currentMusicVolumeFactor == 0.0f && !wasMusicPaused) {
            musicTweaks$setMusicVolumeAndHandlePausing();
        }

        for (SoundInstance sound : records) {
            ChannelAccess.ChannelHandle sourceManager = wrapper.getInstanceToChannel().get(sound);

            if (betterJukeboxes && coordinates.containsKey(sound)) {
                double distanceSquared = playerPosition.distanceToSqr(coordinates.get(sound));
                double calculatedVolume = (maxDistanceSquared - distanceSquared) / divisor;
                calculatedVolume = Math.max(0, Math.min(1, calculatedVolume));

                float adjustedVolume = wrapper.calculateAdjustedVolume((float) calculatedVolume, SoundSource.RECORDS);
                float finalVolume = adjustedVolume * currentRecordVolumeFactor;

                sourceManager.execute(source -> source.setVolume(finalVolume));

                if (sound instanceof AbstractSoundInstanceWrapper modifiedSound) {
                    modifiedSound.trackVolumeForReferenceOnly(finalVolume);
                }
            } else {
                float maxVolume = sound.getVolume();
                float adjustedVolume = wrapper.calculateAdjustedVolume(maxVolume, SoundSource.RECORDS);
                sourceManager.execute(source -> source.setVolume(adjustedVolume * currentRecordVolumeFactor));
            }

            if (sourceManager.isStopped()) {
                coordinates.remove(sound);
            }
        }
    }

    @Unique
    private void musicTweaks$setMusicVolumeAndHandlePausing() {
        if (!wrapper.isLoaded()) return;

        Collection<SoundInstance> music = wrapper.getInstanceBySource().get(SoundSource.MUSIC);

        for (SoundInstance sound : music) {
            if (MusicClientLogic.getInstance().isCombatSound(sound)) continue;

            ChannelAccess.ChannelHandle sourceManager = wrapper.getInstanceToChannel().get(sound);
            float maxVolume = sound.getVolume();

            sourceManager.execute(source -> {
                source.setVolume(wrapper.calculateAdjustedVolume(maxVolume * currentMusicVolumeFactor, SoundSource.MUSIC));

                if (currentMusicVolumeFactor <= 0 && !wasMusicPaused) {
                    sourceManager.execute(Channel::pause);
                } else if (currentMusicVolumeFactor > 0 && wasMusicPaused) {
                    sourceManager.execute(Channel::unpause);
                }
            });
        }

        if (currentMusicVolumeFactor <= 0 && !wasMusicPaused) {
            wasMusicPaused = true;
        } else if (currentMusicVolumeFactor > 0 && wasMusicPaused) {
            wasMusicPaused = false;
        }
    }
}