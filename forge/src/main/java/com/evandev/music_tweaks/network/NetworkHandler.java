package com.evandev.music_tweaks.network;

import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            JukeboxSyncNetworking.CHANNEL_ID,
            () -> PROTOCOL_VERSION,
            NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION::equals),
            NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION::equals)
    );

    private NetworkHandler() {
    }

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, JukeboxSyncRequestMessage.class,
                JukeboxSyncRequestMessage::encode, JukeboxSyncRequestMessage::new, JukeboxSyncRequestMessage::handle);
        CHANNEL.registerMessage(id++, JukeboxSyncResponseMessage.class,
                JukeboxSyncResponseMessage::encode, JukeboxSyncResponseMessage::new, JukeboxSyncResponseMessage::handle);
        CHANNEL.registerMessage(id++, CombatDamageMessage.class,
                CombatDamageMessage::encode, CombatDamageMessage::new, CombatDamageMessage::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
