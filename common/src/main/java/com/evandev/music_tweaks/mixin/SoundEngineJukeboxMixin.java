package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.client.jukebox.JukeboxOffsetState;
import com.evandev.music_tweaks.client.music.MusicClientLogic;
import com.evandev.music_tweaks.config.ModConfig;
import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQueryPacket;
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
    private static final Map<SoundInstance, Vec3> musicTweaks$coordinates = new HashMap<>();
    @Unique
    private final SoundEngineWrapper musicTweaks$wrapper = (SoundEngineWrapper) this;

    @Unique
    private float musicTweaks$currentMusicVolumeFactor = 1f;
    @Unique
    private float musicTweaks$currentRecordVolumeFactor = 1f;
    @Unique
    private boolean musicTweaks$wasMusicPaused = false;

    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    private void injectPlay(SoundInstance p_sound, CallbackInfo ci) {
        if (p_sound.getSource() == SoundSource.RECORDS) {

            if (p_sound.isLooping()) {
                BlockPos blockPos = BlockPos.containing(p_sound.getX(), p_sound.getY(), p_sound.getZ());
                JukeboxOffsetState.trackSound(blockPos, p_sound);
            } else if (!JukeboxOffsetState.isOurSound(p_sound)) {
                ci.cancel();
                BlockPos blockPos = BlockPos.containing(p_sound.getX(), p_sound.getY(), p_sound.getZ());
                Minecraft client = Minecraft.getInstance();
                if (client.level != null && client.getConnection() != null) {
                    if (!JukeboxOffsetState.hasActiveSound(blockPos) &&
                            !JukeboxOffsetState.hasPendingQuery(blockPos)) {
                        int transactionId = JukeboxOffsetState.registerQuery(blockPos);
                        client.getConnection().send(new ServerboundBlockEntityTagQueryPacket(transactionId, blockPos));
                    }
                }
                return;
            }
        }

        if (!ModConfig.get().betterJukeboxes) return;

        if (p_sound.getSource() == SoundSource.RECORDS &&
                p_sound instanceof AbstractSoundInstanceWrapper modifiedSound) {

            modifiedSound.setRelative(true);
            modifiedSound.setAttenuationType(SoundInstance.Attenuation.NONE);

            musicTweaks$coordinates.put(p_sound, new Vec3(p_sound.getX(), p_sound.getY(), p_sound.getZ()));
            modifiedSound.setX(0);
            modifiedSound.setY(0);
            modifiedSound.setZ(0);
        }
    }

    @Inject(method = "tick(Z)V", at = @At("HEAD"))
    private void injectTick(boolean paused, CallbackInfo ci) {
        boolean inCombat = MusicClientLogic.getInstance().isInCombat();
        boolean betterJukeboxes = ModConfig.get().betterJukeboxes;

        double maxDistance = ModConfig.get().jukeboxDistance;
        double minDistance = maxDistance * 0.25;
        double minDistanceSquared = minDistance * minDistance;
        double maxDistanceSquared = maxDistance * maxDistance;
        double divisor = maxDistanceSquared - minDistanceSquared;

        Collection<SoundInstance> records = musicTweaks$wrapper.getInstanceBySource().get(SoundSource.RECORDS);
        Vec3 playerPosition = musicTweaks$wrapper.getListener().getTransform().position();

        long amountRecordsHearable = 0;

        for (SoundInstance sound : records) {
            if (betterJukeboxes && musicTweaks$coordinates.containsKey(sound)) {
                double distanceSquared = playerPosition.distanceToSqr(musicTweaks$coordinates.get(sound));
                if (distanceSquared < maxDistanceSquared) {
                    amountRecordsHearable++;
                }
            } else {
                amountRecordsHearable++;
            }
        }

        boolean hasHearableRecords = amountRecordsHearable > 0;

        float targetRecordVolume = inCombat ? 0.0f : 1.0f;
        float targetMusicVolume = (inCombat || hasHearableRecords) ? 0.0f : 1.0f;

        // Jukebox Fade
        if (musicTweaks$currentRecordVolumeFactor < targetRecordVolume) {
            musicTweaks$currentRecordVolumeFactor = Math.min(musicTweaks$currentRecordVolumeFactor + MUSIC_VOLUME_PER_TICK_TO_FADE_IN, targetRecordVolume);
        } else if (musicTweaks$currentRecordVolumeFactor > targetRecordVolume) {
            musicTweaks$currentRecordVolumeFactor = Math.max(musicTweaks$currentRecordVolumeFactor - MUSIC_VOLUME_PER_TICK_TO_FADE_OUT, targetRecordVolume);
        }

        // Vanilla Music Fade
        if (musicTweaks$currentMusicVolumeFactor < targetMusicVolume) {
            musicTweaks$currentMusicVolumeFactor = Math.min(musicTweaks$currentMusicVolumeFactor + MUSIC_VOLUME_PER_TICK_TO_FADE_IN, targetMusicVolume);
            musicTweaks$setMusicVolumeAndHandlePausing();
        } else if (musicTweaks$currentMusicVolumeFactor > targetMusicVolume) {
            musicTweaks$currentMusicVolumeFactor = Math.max(musicTweaks$currentMusicVolumeFactor - MUSIC_VOLUME_PER_TICK_TO_FADE_OUT, targetMusicVolume);
            musicTweaks$setMusicVolumeAndHandlePausing();
        } else if (musicTweaks$currentMusicVolumeFactor == 1.0f && musicTweaks$wasMusicPaused) {
            musicTweaks$setMusicVolumeAndHandlePausing();
        } else if (musicTweaks$currentMusicVolumeFactor == 0.0f && !musicTweaks$wasMusicPaused) {
            musicTweaks$setMusicVolumeAndHandlePausing();
        }

        for (SoundInstance sound : records) {
            ChannelAccess.ChannelHandle sourceManager = musicTweaks$wrapper.getInstanceToChannel().get(sound);

            if (sourceManager == null) {
                musicTweaks$coordinates.remove(sound);
                continue;
            }

            if (betterJukeboxes && musicTweaks$coordinates.containsKey(sound)) {
                double distanceSquared = playerPosition.distanceToSqr(musicTweaks$coordinates.get(sound));
                double calculatedVolume = (maxDistanceSquared - distanceSquared) / divisor;
                calculatedVolume = Math.max(0, Math.min(1, calculatedVolume));

                float adjustedVolume = musicTweaks$wrapper.calculateAdjustedVolume((float) calculatedVolume, SoundSource.RECORDS);
                float finalVolume = adjustedVolume * musicTweaks$currentRecordVolumeFactor;

                sourceManager.execute(source -> source.setVolume(finalVolume));

                if (sound instanceof AbstractSoundInstanceWrapper modifiedSound) {
                    modifiedSound.trackVolumeForReferenceOnly(finalVolume);
                }
            } else {
                float maxVolume = sound.getVolume();
                float adjustedVolume = musicTweaks$wrapper.calculateAdjustedVolume(maxVolume, SoundSource.RECORDS);
                sourceManager.execute(source -> source.setVolume(adjustedVolume * musicTweaks$currentRecordVolumeFactor));
            }

            if (sourceManager.isStopped()) {
                musicTweaks$coordinates.remove(sound);
            }
        }
    }

    @Unique
    private void musicTweaks$setMusicVolumeAndHandlePausing() {
        if (!musicTweaks$wrapper.isLoaded()) return;

        Collection<SoundInstance> music = musicTweaks$wrapper.getInstanceBySource().get(SoundSource.MUSIC);

        for (SoundInstance sound : music) {
            if (MusicClientLogic.getInstance().isCombatSound(sound)) continue;

            ChannelAccess.ChannelHandle sourceManager = musicTweaks$wrapper.getInstanceToChannel().get(sound);
            if (sourceManager == null) continue;

            float maxVolume = sound.getVolume();

            sourceManager.execute(source -> {
                source.setVolume(musicTweaks$wrapper.calculateAdjustedVolume(maxVolume * musicTweaks$currentMusicVolumeFactor, SoundSource.MUSIC));

                if (musicTweaks$currentMusicVolumeFactor <= 0 && !musicTweaks$wasMusicPaused) {
                    sourceManager.execute(Channel::pause);
                } else if (musicTweaks$currentMusicVolumeFactor > 0 && musicTweaks$wasMusicPaused) {
                    sourceManager.execute(Channel::unpause);
                }
            });
        }

        if (musicTweaks$currentMusicVolumeFactor <= 0 && !musicTweaks$wasMusicPaused) {
            musicTweaks$wasMusicPaused = true;
        } else if (musicTweaks$currentMusicVolumeFactor > 0 && musicTweaks$wasMusicPaused) {
            musicTweaks$wasMusicPaused = false;
        }
    }
}