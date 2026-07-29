package com.evandev.music_tweaks.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class JukeboxSyncRequestMessage {
    private final BlockPos pos;

    public JukeboxSyncRequestMessage(BlockPos pos) {
        this.pos = pos;
    }

    public JukeboxSyncRequestMessage(FriendlyByteBuf buf) {
        this.pos = JukeboxSyncNetworking.readRequest(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        JukeboxSyncNetworking.writeRequest(buf, pos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                JukeboxSyncNetworking.handleRequestOnServer(player.serverLevel(), pos, (ticks, item) ->
                        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                                new JukeboxSyncResponseMessage(pos, ticks, item)));
            }
        });
        ctx.setPacketHandled(true);
    }
}
