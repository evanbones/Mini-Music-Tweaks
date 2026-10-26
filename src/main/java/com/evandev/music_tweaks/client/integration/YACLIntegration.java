package com.evandev.music_tweaks.client.integration;

import com.evandev.music_tweaks.config.ModConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

//? if <26.1 {
import com.evandev.music_tweaks.config.MusicFrequency;
import com.evandev.music_tweaks.platform.Services;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
//?}

public class YACLIntegration {

    public static Screen createScreen(Screen parent) {
        ModConfig config = ModConfig.get();

        OptionGroup combat = OptionGroup.createBuilder()
                .name(Component.translatable("config.music_tweaks.group.combat"))
                .option(createBoolOption("enabled", false,
                        () -> config.enabled, val -> config.enabled = val))
                .option(createIntFieldOption("min_pursuit_entities", 3,
                        () -> config.minPursuitEntities, val -> config.minPursuitEntities = val))
                .option(createIntFieldOption("decay_time", 3,
                        () -> config.decayTime, val -> config.decayTime = val))
                .build();

        ListOption<String> sounds = ListOption.<String>createBuilder()
                .name(Component.translatable("config.music_tweaks.sounds"))
                .description(OptionDescription.of(Component.translatable("config.music_tweaks.sounds.tooltip")))
                .binding(ModConfig.DEFAULT_SOUNDS, () -> config.sounds, val -> config.sounds = new ArrayList<>(val))
                .controller(StringControllerBuilder::create)
                .initial("")
                .build();

        //? if <26.1 {
        OptionGroup.Builder music = OptionGroup.createBuilder()
                .name(Component.translatable("config.music_tweaks.group.music"));
        if (!Services.PLATFORM.isModLoaded("vanillabackport")) {
            music.option(Option.<MusicFrequency>createBuilder()
                            .name(Component.translatable("config.music_tweaks.music_frequency"))
                            .description(OptionDescription.of(Component.translatable("config.music_tweaks.music_frequency.tooltip")))
                            .binding(MusicFrequency.DEFAULT, () -> config.musicFrequency, val -> config.musicFrequency = val)
                            .controller(opt -> EnumControllerBuilder.create(opt)
                                    .enumClass(MusicFrequency.class)
                                    .formatValue(MusicFrequency::getDisplayName))
                            .build());
        }
        music.option(createBoolOption("show_music_toast", true,
                        () -> config.showMusicToast, val -> config.showMusicToast = val))
                .option(createBoolOption("permanent_toast_in_options", true,
                        () -> config.permanentToastInOptions, val -> config.permanentToastInOptions = val))
                .option(createBoolOption("play_toast_sound", true,
                        () -> config.playToastSound, val -> config.playToastSound = val))
                .option(createBoolOption("show_music_id_in_toast", false,
                        () -> config.showMusicIdInToast, val -> config.showMusicIdInToast = val))
                .option(createBoolOption("log_missing_artists", true,
                        () -> config.logMissingArtists, val -> config.logMissingArtists = val))
                .option(createBoolOption("play_music_when_paused", true,
                        () -> config.playMusicWhenPaused, val -> config.playMusicWhenPaused = val));
        //?}

        OptionGroup jukeboxes = OptionGroup.createBuilder()
                .name(Component.translatable("config.music_tweaks.group.jukeboxes"))
                .option(createBoolOption("mono_jukeboxes", true,
                        () -> config.monoJukeboxes, val -> config.monoJukeboxes = val))
                .option(createBoolOption("jukebox_fade_out", true,
                        () -> config.jukeboxFadeOut, val -> config.jukeboxFadeOut = val))
                .option(Option.<Double>createBuilder()
                        .name(Component.translatable("config.music_tweaks.jukebox_distance"))
                        .description(OptionDescription.of(Component.translatable("config.music_tweaks.jukebox_distance.tooltip")))
                        .binding(64.0, () -> config.jukeboxDistance, val -> config.jukeboxDistance = val)
                        .controller(opt -> DoubleSliderControllerBuilder.create(opt).range(0.0, 256.0).step(1.0))
                        .build())
                .build();

        ConfigCategory.Builder general = ConfigCategory.createBuilder()
                .name(Component.translatable("config.music_tweaks.category.general"))
                .group(combat)
                .group(sounds)
                //? if <26.1 {
                .group(music.build())
                //?}
                .group(jukeboxes);

        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("config.music_tweaks.title"))
                .save(ModConfig::save)
                .category(general.build())
                .build()
                .generateScreen(parent);
    }

    private static Option<Boolean> createBoolOption(String name, boolean defaultValue, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return Option.<Boolean>createBuilder()
                .name(Component.translatable("config.music_tweaks." + name))
                .description(OptionDescription.of(Component.translatable("config.music_tweaks." + name + ".tooltip")))
                .binding(defaultValue, getter, setter)
                .controller(TickBoxControllerBuilder::create)
                .build();
    }

    private static Option<Integer> createIntFieldOption(String name, int defaultValue, Supplier<Integer> getter, Consumer<Integer> setter) {
        return Option.<Integer>createBuilder()
                .name(Component.translatable("config.music_tweaks." + name))
                .description(OptionDescription.of(Component.translatable("config.music_tweaks." + name + ".tooltip")))
                .binding(defaultValue, getter, setter)
                .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(0))
                .build();
    }
}
