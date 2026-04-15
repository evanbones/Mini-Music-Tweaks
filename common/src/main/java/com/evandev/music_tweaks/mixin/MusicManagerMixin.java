package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.config.ModConfig;
import com.evandev.music_tweaks.config.MusicFrequency;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MusicManager.class)
public class MusicManagerMixin {

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
}