package com.evandev.music_tweaks.client;

import com.evandev.music_tweaks.client.jukebox.JukeboxOffsetState;
import com.evandev.music_tweaks.client.jukebox.JukeboxSyncHandler;
import com.evandev.music_tweaks.network.JukeboxSyncRequestPayload;
import com.evandev.music_tweaks.network.JukeboxSyncResponsePayload;
import net.minecraft.core.BlockPos;

//? if fabric {
/*import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
*///?}
//? if fabric && <1.21 {
/*import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
*///?}
//? if forgelike {
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
//?}
//? if forge {
/*import com.evandev.music_tweaks.forge.ForgeNetwork;
*///?}
//? if neoforge && >=26.1 {
/*import net.neoforged.neoforge.client.network.ClientPacketDistributor;
*///?} else if neoforge {
import net.neoforged.neoforge.network.PacketDistributor;
//?}

public class ClientNetworking {

    public static boolean isModLoadedOnServer() {
        //? if fabric && >=1.21 {
        /*return ClientPlayNetworking.canSend(JukeboxSyncRequestPayload.TYPE);
        *///?} else if fabric {
        /*return ClientPlayNetworking.canSend(JukeboxSyncRequestPayload.ID);
        *///?} else if forge {
        /*ClientPacketListener connection = Minecraft.getInstance().getConnection();
        return connection != null && ForgeNetwork.isRemotePresent(connection.getConnection());
        *///?} else {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        return connection != null && connection.hasChannel(JukeboxSyncRequestPayload.TYPE);
        //?}
    }

    public static void sendJukeboxSyncRequest(BlockPos pos) {
        JukeboxSyncRequestPayload payload = new JukeboxSyncRequestPayload(pos);
        //? if fabric && >=1.21 {
        /*ClientPlayNetworking.send(payload);
        *///?} else if fabric {
        /*FriendlyByteBuf buf = PacketByteBufs.create();
        payload.write(buf);
        ClientPlayNetworking.send(JukeboxSyncRequestPayload.ID, buf);
        *///?} else if forge {
        /*ForgeNetwork.sendToServer(payload);
        *///?} else if >=26.1 {
        /*ClientPacketDistributor.sendToServer(payload);
        *///?} else {
        PacketDistributor.sendToServer(payload);
        //?}
    }

    public static void handleJukeboxSyncResponse(JukeboxSyncResponsePayload payload) {
        JukeboxOffsetState.resolveCustomQuery(payload.pos());
        JukeboxSyncHandler.playSyncedSong(payload.pos(), payload.ticksSinceStart(), payload.recordItem());
    }
}
