package com.evandev.music_tweaks.client.integration;

import com.evandev.music_tweaks.config.ModConfig;
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
                .setDefaultValue(20)
                .setTooltip(Component.translatable("config.music_tweaks.decay_time.tooltip"))
                .setSaveConsumer(newValue -> config.decayTime = newValue)
                .build());

        general.addEntry(entryBuilder.startStrList(Component.translatable("config.music_tweaks.sounds"), config.sounds)
                .setDefaultValue(List.of("minecraft:music_disc.pigstep", "minecraft:music_disc.mellohi"))
                .setTooltip(Component.translatable("config.music_tweaks.sounds.tooltip"))
                .setSaveConsumer(newValue -> config.sounds = newValue)
                .build());

        return builder.build();
    }
}