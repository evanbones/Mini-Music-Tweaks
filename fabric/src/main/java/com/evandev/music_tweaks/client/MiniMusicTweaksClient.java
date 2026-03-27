package com.evandev.music_tweaks.client;

import com.evandev.music_tweaks.Constants;
import com.evandev.music_tweaks.client.music.MusicClientLogic;
import com.evandev.music_tweaks.client.music.MusicEventListener;
import com.evandev.music_tweaks.client.music.MusicHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MiniMusicTweaksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level != null) {
                MusicClientLogic.getInstance().onClientTick(client);
            }
        });

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "musics");
            }

            @Override
            public @NotNull CompletableFuture<Void> reload(@NotNull PreparationBarrier preparationBarrier, @NotNull ResourceManager resourceManager,
                                                           @NotNull ProfilerFiller preparationsProfiler, @NotNull ProfilerFiller reloadProfiler,
                                                           @NotNull Executor backgroundExecutor, @NotNull Executor gameExecutor) {
                return CompletableFuture.runAsync(() -> MusicHandler.INSTANCE.onResourceManagerReload(resourceManager), gameExecutor)
                        .thenCompose(preparationBarrier::wait);
            }
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            client.getSoundManager().addListener(new MusicEventListener());
        });
    }
}