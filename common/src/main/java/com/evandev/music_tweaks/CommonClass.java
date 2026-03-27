package com.evandev.music_tweaks;

import com.evandev.music_tweaks.config.ModConfig;

public class CommonClass {
    public static void init() {
        ModConfig.load();
    }
}