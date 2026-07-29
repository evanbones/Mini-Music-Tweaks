package com.evandev.music_tweaks.platform;

import com.evandev.music_tweaks.network.JukeboxSyncRequestMessage;
import com.evandev.music_tweaks.network.NetworkHandler;
import com.evandev.music_tweaks.platform.services.IPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
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
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        return connection != null && NetworkHandler.CHANNEL.isRemotePresent(connection.getConnection());
    }

    @Override
    public void sendJukeboxSyncRequest(BlockPos pos) {
        NetworkHandler.CHANNEL.sendToServer(new JukeboxSyncRequestMessage(pos));
    }
}