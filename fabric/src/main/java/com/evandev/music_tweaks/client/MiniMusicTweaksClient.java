package com.evandev.music_tweaks.client;

import com.evandev.music_tweaks.Constants;
import com.evandev.music_tweaks.client.music.MusicClientLogic;
import com.evandev.music_tweaks.client.music.MusicHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.NonNull;

public class MiniMusicTweaksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level != null) {
                MusicClientLogic.getInstance().onClientTick(client);
            }
        });

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public @NonNull Identifier getFabricId() {
                return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "musics");
            }

            @Override
            public void onResourceManagerReload(@NonNull ResourceManager resourceManager) {
                MusicHandler.INSTANCE.onResourceManagerReload(resourceManager);
            }
        });
    }
}