package com.evandev.music_tweaks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CombatDamageMessage {
    private final String damageSourceId;

    public CombatDamageMessage(String damageSourceId) {
        this.damageSourceId = damageSourceId;
    }

    public CombatDamageMessage(FriendlyByteBuf buf) {
        this.damageSourceId = CombatDamageNetworking.readDamage(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        CombatDamageNetworking.writeDamage(buf, damageSourceId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> CombatDamageNetworking.handleDamageOnClient(damageSourceId));
        ctx.setPacketHandled(true);
    }
}
