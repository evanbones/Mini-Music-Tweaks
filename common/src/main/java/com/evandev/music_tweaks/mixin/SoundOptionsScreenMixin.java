package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.config.ModConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Arrays;

@Mixin(SoundOptionsScreen.class)
public abstract class SoundOptionsScreenMixin {

    @ModifyReturnValue(method = "getAllSoundOptionsExceptMaster", at = @At("RETURN"))
    private OptionInstance<?>[] musicTweaks$appendJukeboxToSoundList(OptionInstance<?>[] original) {
        OptionInstance<Integer> jukeboxOption = new OptionInstance<>(
                "config.music_tweaks.jukebox_distance",
                OptionInstance.noTooltip(),
                (label, val) -> Component.translatable("config.music_tweaks.jukebox_distance").append(": " + val),
                new OptionInstance.IntRange(0, 256),
                ModConfig.get().general.jukeboxDistance.value().intValue(),
                value -> {
                    ModConfig.get().general.jukeboxDistance.setValue(value.doubleValue());
                }
        );

        OptionInstance<?>[] newArray = Arrays.copyOf(original, original.length + 1);
        newArray[original.length] = jukeboxOption;

        return newArray;
    }
}