package com.evandev.music_tweaks.client;

import com.evandev.music_tweaks.client.music.MusicClientLogic;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

public class MiniMusicTweaksClient {
    public static void register(ModContainer container, IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(MiniMusicTweaksClient::onClientTick);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            MusicClientLogic.getInstance().onClientTick(mc);
        }
    }
}