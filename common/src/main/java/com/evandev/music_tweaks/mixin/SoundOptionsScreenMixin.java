package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.config.ModConfig;
import com.evandev.music_tweaks.config.MusicFrequency;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Arrays;
import java.util.List;

@Mixin(SoundOptionsScreen.class)
public abstract class SoundOptionsScreenMixin {

    @ModifyReturnValue(method = "buttonOptions", at = @At("RETURN"))
    private static OptionInstance<?>[] musicTweaks$appendMiscOptions(OptionInstance<?>[] original) {
        OptionInstance<MusicFrequency> frequencyOption = new OptionInstance<>(
                "config.music_tweaks.music_frequency",
                OptionInstance.noTooltip(),
                (label, value) -> value.getDisplayName(),
                new OptionInstance.Enum<>(List.of(MusicFrequency.values()), MusicFrequency.CODEC),
                ModConfig.get().musicFrequency,
                value -> {
                    ModConfig.get().musicFrequency = value;
                    ModConfig.save();
                }
        );

        OptionInstance<Boolean> toastOption = OptionInstance.createBoolean(
                "config.music_tweaks.show_music_toast",
                OptionInstance.cachedConstantTooltip(Component.translatable("config.music_tweaks.show_music_toast.tooltip")),
                (label, value) -> value ? Component.literal("ON") : Component.literal("OFF"),
                ModConfig.get().showMusicToast,
                value -> {
                    ModConfig.get().showMusicToast = value;
                    ModConfig.save();
                }
        );

        OptionInstance<?>[] newArray = Arrays.copyOf(original, original.length + 2);
        newArray[original.length] = frequencyOption;
        newArray[original.length + 1] = toastOption;

        return newArray;
    }

    @ModifyReturnValue(method = "getAllSoundOptionsExceptMaster", at = @At("RETURN"))
    private OptionInstance<?>[] musicTweaks$appendJukeboxToSoundList(OptionInstance<?>[] original) {
        OptionInstance<Integer> jukeboxOption = new OptionInstance<>(
                "config.music_tweaks.jukebox_distance",
                OptionInstance.noTooltip(),
                (label, val) -> Component.translatable("config.music_tweaks.jukebox_distance").append(": " + val),
                new OptionInstance.IntRange(0, 256),
                (int) ModConfig.get().jukeboxDistance,
                value -> {
                    ModConfig.get().jukeboxDistance = value.doubleValue();
                    ModConfig.save();
                }
        );

        OptionInstance<?>[] newArray = Arrays.copyOf(original, original.length + 1);
        newArray[original.length] = jukeboxOption;

        return newArray;
    }
}