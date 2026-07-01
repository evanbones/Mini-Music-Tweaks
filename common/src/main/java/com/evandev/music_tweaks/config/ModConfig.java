package com.evandev.music_tweaks.config;

import com.evandev.music_tweaks.Constants;
import com.evandev.music_tweaks.platform.Services;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = Services.PLATFORM.getConfigDirectory().resolve("music_tweaks.json").toFile();
    private static ModConfig INSTANCE;

    @SerializedName("enabled")
    public boolean enabled = false;

    @SerializedName("min_pursuit_entities")
    public int minPursuitEntities = 3;

    @SerializedName("decay_time")
    public int decayTime = 3;

    @SerializedName("sounds")
    public List<String> sounds = new ArrayList<>(List.of("minecraft:music_disc.pigstep", "minecraft:music_disc.mellohi"));

    @SerializedName("music_frequency")
    public MusicFrequency musicFrequency = MusicFrequency.DEFAULT;

    @SerializedName("show_music_toast")
    public boolean showMusicToast = true;

    @SerializedName("better_jukeboxes")
    public boolean betterJukeboxes = true;

    @SerializedName("jukebox_distance")
    public double jukeboxDistance = 64.0;

    @SerializedName("permanent_toast_in_options")
    public boolean permanentToastInOptions = true;

    @SerializedName("play_toast_sound")
    public boolean playToastSound = true;

    public static ModConfig get() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                INSTANCE = GSON.fromJson(reader, ModConfig.class);
            } catch (Exception e) {
                Constants.LOG.error("Failed to load music_tweaks.json", e);
                INSTANCE = new ModConfig();
                save();
            }
        } else {
            INSTANCE = new ModConfig();
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            Constants.LOG.error("Failed to save music_tweaks.json", e);
        }
    }
}