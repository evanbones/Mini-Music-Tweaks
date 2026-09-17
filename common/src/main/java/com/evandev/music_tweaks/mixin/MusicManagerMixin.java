package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.client.jukebox.JukeboxOffsetState;
import com.evandev.music_tweaks.client.music.MusicClientLogic;
import com.evandev.music_tweaks.config.ModConfig;
import com.evandev.music_tweaks.config.MusicFrequency;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public class MusicManagerMixin {

    @Shadow
    private int nextSongDelay;

    @Shadow
    @Nullable
    private SoundInstance currentMusic;

    @Inject(method = "stopPlaying()V", at = @At("TAIL"))
    private void musicTweaks$onStopPlaying(CallbackInfo ci) {
        MusicFrequency frequency = ModConfig.get().musicFrequency;

        if (frequency == MusicFrequency.CONSTANT) {
            this.nextSongDelay = 20;
        } else if (frequency == MusicFrequency.FREQUENT) {
            this.nextSongDelay = 50;
        } else {
            this.nextSongDelay = 100;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void musicTweaks$clampDelayOnTick(CallbackInfo ci) {
        if (this.currentMusic == null) {
            MusicFrequency frequency = ModConfig.get().musicFrequency;

            if (frequency == MusicFrequency.CONSTANT && this.nextSongDelay > 20) {
                this.nextSongDelay = 20;
            }
        }
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I")
    )
    private int musicTweaks$modifySongDelay(RandomSource random, int minimum, int maximum, Operation<Integer> original) {
        int vanillaDelay = original.call(random, minimum, maximum);

        MusicFrequency frequency = ModConfig.get().musicFrequency;

        if (frequency == MusicFrequency.CONSTANT) {
            return 20;
        } else if (frequency == MusicFrequency.FREQUENT) {
            return (int) (vanillaDelay * 0.5f);
        }

        return vanillaDelay;
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/MusicManager;startPlaying(Lnet/minecraft/sounds/Music;)V")
    )
    private void musicTweaks$preventStartingMusicInCombat(MusicManager instance, Music music, Operation<Void> original) {
        if (!MusicClientLogic.getInstance().isInCombat() && !JukeboxOffsetState.isRecordHearable()) {
            original.call(instance, music);
        }
    }
}