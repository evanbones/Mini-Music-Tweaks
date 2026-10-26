package com.evandev.music_tweaks.fabric;

//? if fabric {
/*import com.evandev.music_tweaks.CommonClass;
import com.evandev.music_tweaks.network.JukeboxSyncRequestPayload;
import com.evandev.music_tweaks.network.JukeboxSyncResponsePayload;
import com.evandev.music_tweaks.network.JukeboxSyncServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

//? if >=1.21 {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
//?} else {
/^import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
^///?}

public class MiniMusicTweaksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CommonClass.init();
        registerPayloads();
    }

    private static void registerPayloads() {
        //? if >=26.1 {
        /^PayloadTypeRegistry.serverboundPlay().register(JukeboxSyncRequestPayload.TYPE, JukeboxSyncRequestPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(JukeboxSyncResponsePayload.TYPE, JukeboxSyncResponsePayload.STREAM_CODEC);
        ^///?} else if >=1.21 {
        PayloadTypeRegistry.playC2S().register(JukeboxSyncRequestPayload.TYPE, JukeboxSyncRequestPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(JukeboxSyncResponsePayload.TYPE, JukeboxSyncResponsePayload.STREAM_CODEC);
        //?}

        //? if >=1.21 {
        ServerPlayNetworking.registerGlobalReceiver(JukeboxSyncRequestPayload.TYPE, (payload, context) ->
                context.player().level().getServer().execute(() ->
                        JukeboxSyncServer.createResponse(context.player().level(), payload.pos())
                                .ifPresent(response -> ServerPlayNetworking.send(context.player(), response))));
        //?} else {
        /^ServerPlayNetworking.registerGlobalReceiver(JukeboxSyncRequestPayload.ID, (server, player, handler, buf, responseSender) -> {
            JukeboxSyncRequestPayload payload = JukeboxSyncRequestPayload.read(buf);
            server.execute(() -> JukeboxSyncServer.createResponse(player.level(), payload.pos()).ifPresent(response -> {
                FriendlyByteBuf responseBuf = PacketByteBufs.create();
                response.write(responseBuf);
                ServerPlayNetworking.send(player, JukeboxSyncResponsePayload.ID, responseBuf);
            }));
        });
        ^///?}
    }
}
*///?}
