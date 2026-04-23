package com.evandev.music_tweaks.platform;

import com.evandev.music_tweaks.network.JukeboxSyncRequestPayload;
import com.evandev.music_tweaks.platform.services.IPlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;

import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isPhysicalClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public boolean isModLoadedOnServer() {
        return ClientPlayNetworking.canSend(JukeboxSyncRequestPayload.ID);
    }

    @Override
    public void sendJukeboxSyncRequest(BlockPos pos) {
        ClientPlayNetworking.send(new JukeboxSyncRequestPayload(pos));
    }
}