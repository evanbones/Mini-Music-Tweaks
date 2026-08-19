package com.evandev.music_tweaks.client.music;

import com.evandev.music_tweaks.Constants;
import com.evandev.music_tweaks.mixin.SoundManagerAccessor;
import com.evandev.music_tweaks.mixin.WeighedSoundEventsAccessor;
import com.evandev.music_tweaks.platform.Services;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class MusicDumpHelper {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void dumpMusicIds(Consumer<Component> feedback) {
        Minecraft mc = Minecraft.getInstance();
        Set<ResourceLocation> discoveredMusicIds = new TreeSet<>(Comparator.comparing(ResourceLocation::toString));

        discoveredMusicIds.addAll(MusicHandler.getMusicDb().keySet());

        try {
            Map<ResourceLocation, WeighedSoundEvents> soundRegistry = ((SoundManagerAccessor) mc.getSoundManager()).getRegistry();
            if (soundRegistry != null) {
                for (Map.Entry<ResourceLocation, WeighedSoundEvents> entry : soundRegistry.entrySet()) {
                    ResourceLocation eventId = entry.getKey();
                    WeighedSoundEvents weighedEvents = entry.getValue();

                    boolean isMusicEvent = isMusicIdentifier(eventId);

                    if (weighedEvents != null) {
                        extractSounds(weighedEvents, isMusicEvent, discoveredMusicIds);
                    }
                }
            }
        } catch (Exception e) {
            Constants.LOG.error("Failed to inspect SoundManager registry for music dump", e);
        }

        if (mc.level != null) {
            mc.level.registryAccess().registry(Registries.JUKEBOX_SONG).ifPresent(registry -> {
                registry.holders().forEach(holder -> {
                    ResourceLocation soundEventLoc = holder.value().soundEvent().value().getLocation();
                    if (isMusicIdentifier(soundEventLoc)) {
                        discoveredMusicIds.add(soundEventLoc);
                    }
                });
            });
        }

        JsonObject fullDumpJson = new JsonObject();
        JsonObject missingArtistsJson = new JsonObject();

        int totalCount = 0;
        int missingCount = 0;

        for (ResourceLocation loc : discoveredMusicIds) {
            MusicHandler.MusicMetadata metadata = MusicHandler.getMusicInfo(loc);
            String title = metadata.title().getString();
            String author = metadata.author().getString();

            JsonObject entry = new JsonObject();
            entry.addProperty("title", title);
            entry.addProperty("author", author);

            fullDumpJson.add(loc.toString(), entry);
            totalCount++;

            if (author.isEmpty()) {
                missingArtistsJson.add(loc.toString(), entry);
                missingCount++;
            }
        }

        Path outputDir = Services.PLATFORM.getConfigDirectory().resolve("music_tweaks");
        try {
            Files.createDirectories(outputDir);

            File fullDumpFile = outputDir.resolve("music_dump.json").toFile();
            try (FileWriter writer = new FileWriter(fullDumpFile)) {
                GSON.toJson(fullDumpJson, writer);
            }

            File missingFile = outputDir.resolve("missing_artists.json").toFile();
            try (FileWriter writer = new FileWriter(missingFile)) {
                GSON.toJson(missingArtistsJson, writer);
            }

            MutableComponent fullDumpLink = createClickableFileLink(fullDumpFile);
            MutableComponent missingLink = createClickableFileLink(missingFile);

            feedback.accept(Component.translatable("music_tweaks.dump.success", totalCount, missingCount, fullDumpLink));
            if (missingCount > 0) {
                feedback.accept(Component.translatable("music_tweaks.dump.view_missing", missingLink));
            }

            Constants.LOG.info("Dumped {} music tracks ({} without artists) to {} and {}",
                    totalCount, missingCount, fullDumpFile.getAbsolutePath(), missingFile.getAbsolutePath());

        } catch (IOException e) {
            Constants.LOG.error("Failed to write music dump files", e);
            feedback.accept(Component.translatable("music_tweaks.dump.failed", e.getMessage()));
        }
    }

    private static void extractSounds(WeighedSoundEvents weighedEvents, boolean isMusicEvent, Set<ResourceLocation> results) {
        List<Weighted<Sound>> list = ((WeighedSoundEventsAccessor) weighedEvents).getList();
        if (list == null) return;

        for (Weighted<Sound> weighted : list) {
            if (weighted instanceof Sound sound) {
                ResourceLocation soundLoc = sound.getLocation();
                if (isMusicEvent || isMusicIdentifier(soundLoc)) {
                    results.add(soundLoc);
                }
            } else if (weighted instanceof WeighedSoundEvents nested) {
                extractSounds(nested, isMusicEvent, results);
            }
        }
    }

    private static boolean isMusicIdentifier(ResourceLocation id) {
        if (id == null) return false;
        String path = id.getPath().toLowerCase(Locale.ROOT);
        return path.startsWith("music/") ||
                path.startsWith("sounds/music/") ||
                path.startsWith("records/") ||
                path.contains("music") ||
                path.contains("record") ||
                path.contains("disc");
    }

    private static MutableComponent createClickableFileLink(File file) {
        return Component.literal(file.getName())
                .withStyle(style -> style
                        .withColor(ChatFormatting.AQUA)
                        .withUnderlined(true)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to open " + file.getAbsolutePath())))
                );
    }
}
