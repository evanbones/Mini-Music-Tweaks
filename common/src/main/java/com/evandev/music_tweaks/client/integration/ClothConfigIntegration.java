package com.evandev.music_tweaks.client.integration;

import com.evandev.music_tweaks.config.ModConfig;
import com.evandev.music_tweaks.config.MusicFrequency;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ClothConfigIntegration {

    public static Screen createScreen(Screen parent) {
        ModConfig config = ModConfig.get();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.music_tweaks.title"));

        builder.setSavingRunnable(ModConfig::save);

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.music_tweaks.category.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.music_tweaks.enabled"), config.enabled)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.music_tweaks.enabled.tooltip"))
                .setSaveConsumer(newValue -> config.enabled = newValue)
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.music_tweaks.min_pursuit_entities"), config.minPursuitEntities)
                .setDefaultValue(3)
                .setTooltip(Component.translatable("config.music_tweaks.min_pursuit_entities.tooltip"))
                .setSaveConsumer(newValue -> config.minPursuitEntities = newValue)
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.music_tweaks.decay_time"), config.decayTime)
                .setDefaultValue(3)
                .setTooltip(Component.translatable("config.music_tweaks.decay_time.tooltip"))
                .setSaveConsumer(newValue -> config.decayTime = newValue)
                .build());

        general.addEntry(entryBuilder.startStrList(Component.translatable("config.music_tweaks.sounds"), config.sounds)
                .setDefaultValue(List.of("minecraft:music_disc.pigstep", "minecraft:music_disc.mellohi"))
                .setTooltip(Component.translatable("config.music_tweaks.sounds.tooltip"))
                .setSaveConsumer(newValue -> config.sounds = newValue)
                .build());

        general.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.music_tweaks.music_frequency"), MusicFrequency.class, config.musicFrequency)
                .setDefaultValue(MusicFrequency.DEFAULT)
                .setTooltip(Component.translatable("config.music_tweaks.music_frequency.tooltip"))
                .setSaveConsumer(newValue -> config.musicFrequency = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.music_tweaks.show_music_toast"), config.showMusicToast)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.music_tweaks.show_music_toast.tooltip"))
                .setSaveConsumer(newValue -> config.showMusicToast = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.music_tweaks.better_jukeboxes"), config.betterJukeboxes)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.music_tweaks.better_jukeboxes.tooltip"))
                .setSaveConsumer(newValue -> config.betterJukeboxes = newValue)
                .build());

        general.addEntry(entryBuilder.startDoubleField(Component.translatable("config.music_tweaks.jukebox_distance"), config.jukeboxDistance)
                .setDefaultValue(64.0)
                .setTooltip(Component.translatable("config.music_tweaks.jukebox_distance.tooltip"))
                .setSaveConsumer(newValue -> config.jukeboxDistance = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.music_tweaks.permanent_toast_in_options"), config.permanentToastInOptions)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.music_tweaks.permanent_toast_in_options.tooltip"))
                .setSaveConsumer(newValue -> config.permanentToastInOptions = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.music_tweaks.play_toast_sound"), config.playToastSound)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.music_tweaks.play_toast_sound.tooltip"))
                .setSaveConsumer(newValue -> config.playToastSound = newValue)
                .build());

        return builder.build();
    }
}