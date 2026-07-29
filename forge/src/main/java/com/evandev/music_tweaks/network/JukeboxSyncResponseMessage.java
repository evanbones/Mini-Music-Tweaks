package com.evandev.music_tweaks.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class JukeboxSyncResponseMessage {
    private final BlockPos pos;
    private final long ticksSinceStart;
    private final ItemStack recordItem;

    public JukeboxSyncResponseMessage(BlockPos pos, long ticksSinceStart, ItemStack recordItem) {
        this.pos = pos;
        this.ticksSinceStart = ticksSinceStart;
        this.recordItem = recordItem;
    }

    public JukeboxSyncResponseMessage(FriendlyByteBuf buf) {
        this.pos = JukeboxSyncNetworking.readResponsePos(buf);
        this.ticksSinceStart = JukeboxSyncNetworking.readResponseTicks(buf);
        this.recordItem = JukeboxSyncNetworking.readResponseItem(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        JukeboxSyncNetworking.writeResponse(buf, pos, ticksSinceStart, recordItem);
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> JukeboxSyncNetworking.handleResponseOnClient(pos, ticksSinceStart, recordItem));
        ctx.setPacketHandled(true);
    }
}
