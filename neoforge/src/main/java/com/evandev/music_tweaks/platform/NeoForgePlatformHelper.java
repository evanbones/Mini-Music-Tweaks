package com.evandev.music_tweaks.platform;

import com.evandev.music_tweaks.network.JukeboxSyncRequestPayload;
import com.evandev.music_tweaks.platform.services.IPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;

import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isPhysicalClient() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }

    @Override
    public boolean isModLoadedOnServer() {
        var connection = Minecraft.getInstance().getConnection();
        return connection != null && connection.hasChannel(JukeboxSyncRequestPayload.ID);
    }

    @Override
    public void sendJukeboxSyncRequest(BlockPos pos) {
        PacketDistributor.sendToServer(new JukeboxSyncRequestPayload(pos));
    }
}