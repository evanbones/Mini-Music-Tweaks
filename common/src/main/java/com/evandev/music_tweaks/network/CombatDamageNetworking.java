package com.evandev.music_tweaks.network;

import com.evandev.music_tweaks.Constants;
import com.evandev.music_tweaks.client.music.MusicClientLogic;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public final class CombatDamageNetworking {
    public static final ResourceLocation DAMAGE_ID = new ResourceLocation(Constants.MOD_ID, "combat_damage");

    private CombatDamageNetworking() {
    }

    public static void writeDamage(FriendlyByteBuf buf, String damageSourceId) {
        buf.writeUtf(damageSourceId);
    }

    public static String readDamage(FriendlyByteBuf buf) {
        return buf.readUtf();
    }

    public static void handleDamageOnClient(String damageSourceId) {
        MusicClientLogic.getInstance().onPlayerDamaged(damageSourceId);
    }
}
