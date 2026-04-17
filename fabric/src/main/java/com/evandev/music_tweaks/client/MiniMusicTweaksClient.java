package com.evandev.music_tweaks.client;

import com.evandev.music_tweaks.client.music.MusicClientLogic;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class MiniMusicTweaksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level != null) {
                MusicClientLogic.getInstance().onClientTick(client);
            }
        });
    }
}