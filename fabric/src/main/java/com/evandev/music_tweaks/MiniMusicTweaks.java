package com.evandev.music_tweaks;

import com.evandev.music_tweaks.network.JukeboxSyncNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class MiniMusicTweaks implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();

        ServerPlayNetworking.registerGlobalReceiver(JukeboxSyncNetworking.REQUEST_ID, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = JukeboxSyncNetworking.readRequest(buf);
            server.execute(() -> JukeboxSyncNetworking.handleRequestOnServer(player.serverLevel(), pos, (ticks, item) -> {
                FriendlyByteBuf response = PacketByteBufs.create();
                JukeboxSyncNetworking.writeResponse(response, pos, ticks, item);
                ServerPlayNetworking.send(player, JukeboxSyncNetworking.RESPONSE_ID, response);
            }));
        });
    }

}
