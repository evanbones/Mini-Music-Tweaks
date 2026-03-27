package com.evandev.music_tweaks.client.music;

import com.evandev.music_tweaks.Constants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicHandler implements ResourceManagerReloadListener {
    public static final MusicHandler INSTANCE = new MusicHandler();
    private static final Map<Identifier, MusicMetadata> MUSIC_DB = new HashMap<>();
    private static final Identifier DATA_LOCATION = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "musics.json");

    public static MusicMetadata getMusicInfo(Identifier location) {
        if (MUSIC_DB.containsKey(location)) {
            return MUSIC_DB.get(location);
        }

        String path = location.getPath();

        if (path.endsWith(".ogg")) {
            path = path.substring(0, path.length() - 4);
        }

        if (path.contains("/")) {
            path = path.substring(path.lastIndexOf('/') + 1);
        } else if (path.contains(".")) {
            path = path.substring(path.lastIndexOf('.') + 1);
        }

        String translationKey = location.getNamespace() + "." + location.getPath();
        Component translatedName = Component.translatable(translationKey);

        Component titleComponent;
        if (!translatedName.getString().equals(translationKey)) {
            titleComponent = translatedName;
        } else {
            titleComponent = Component.literal(beautifyName(path));
        }

        return new MusicMetadata(titleComponent, Component.empty());
    }

    public static MusicMetadata getDiscInfo(Item disc) {
        JukeboxPlayable playable = disc.components().get(DataComponents.JUKEBOX_PLAYABLE);

        if (playable != null) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                JukeboxSong song = playable.song().value();
                Component description = song.description();
                String fullDescription = description.getString();

                String[] split = fullDescription.split(" - ");
                if (split.length >= 2) {
                    return new MusicMetadata(Component.literal(split[1]), Component.literal(split[0]));
                } else {
                    return new MusicMetadata(description, Component.empty());
                }
            }
        }
        return new MusicMetadata(disc.getDefaultInstance().getDisplayName(), Component.empty());
    }

    private static String beautifyName(String input) {
        if (input == null || input.isEmpty()) return "";
        String[] words = input.split("[_.\\s]+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) sb.append(word.substring(1));
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        MUSIC_DB.clear();

        List<Resource> resources = resourceManager.getResourceStack(DATA_LOCATION);
        for (Resource resource : resources) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {
                JsonObject json = GsonHelper.parse(reader);
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    Identifier id = Identifier.parse(entry.getKey());
                    JsonObject data = entry.getValue().getAsJsonObject();

                    String title = GsonHelper.getAsString(data, "title", "Unknown");
                    String author = GsonHelper.getAsString(data, "author", "");

                    MUSIC_DB.put(id, new MusicMetadata(Component.literal(title), Component.literal(author)));
                }
            } catch (RuntimeException | IOException e) {
                Constants.LOG.error("Failed to load music data from {}", resource.sourcePackId(), e);
            }
        }
    }

    public record MusicMetadata(Component title, Component author) {
    }
}