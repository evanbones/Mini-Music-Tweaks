package com.evandev.music_tweaks.forge;

//? if forge {
/*import com.evandev.music_tweaks.Constants;
import com.evandev.music_tweaks.client.ClientNetworking;
import com.evandev.music_tweaks.network.JukeboxSyncRequestPayload;
import com.evandev.music_tweaks.network.JukeboxSyncResponsePayload;
import com.evandev.music_tweaks.network.JukeboxSyncServer;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ForgeNetwork {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            Constants.id("jukebox_sync"),
            () -> PROTOCOL_VERSION,
            NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION),
            NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION)
    );

    public static void register() {
        CHANNEL.messageBuilder(JukeboxSyncRequestPayload.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(JukeboxSyncRequestPayload::write)
                .decoder(JukeboxSyncRequestPayload::read)
                .consumerMainThread((payload, context) -> {
                    ServerPlayer player = context.get().getSender();
                    if (player != null) {
                        JukeboxSyncServer.createResponse(player.level(), payload.pos())
                                .ifPresent(response -> CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), response));
                    }
                })
                .add();

        CHANNEL.messageBuilder(JukeboxSyncResponsePayload.class, 1, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(JukeboxSyncResponsePayload::write)
                .decoder(JukeboxSyncResponsePayload::read)
                .consumerMainThread((payload, context) -> ClientNetworking.handleJukeboxSyncResponse(payload))
                .add();
    }

    public static boolean isRemotePresent(Connection connection) {
        return CHANNEL.isRemotePresent(connection);
    }

    public static void sendToServer(Object payload) {
        CHANNEL.sendToServer(payload);
    }
}
*///?}
