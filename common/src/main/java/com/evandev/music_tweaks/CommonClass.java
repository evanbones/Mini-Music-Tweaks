package com.evandev.music_tweaks;

import com.evandev.music_tweaks.config.ModConfig;
import net.minecraft.server.MinecraftServer;

public class CommonClass {
    private static MinecraftServer currentServer;

    public static MinecraftServer getServer() {
        return currentServer;
    }

    public static void setServer(MinecraftServer server) {
        currentServer = server;
    }

    public static void init() {
        ModConfig.load();
    }
}