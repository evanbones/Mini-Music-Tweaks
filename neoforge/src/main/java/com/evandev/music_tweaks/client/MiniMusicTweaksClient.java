package com.evandev.music_tweaks.client;

import com.evandev.music_tweaks.Constants;
import com.evandev.music_tweaks.client.music.MusicClientLogic;
import com.evandev.music_tweaks.client.music.MusicHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

public class MiniMusicTweaksClient {
    public static void register(ModContainer container, IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(MiniMusicTweaksClient::onClientTick);

        modEventBus.addListener(MiniMusicTweaksClient::onRegisterReloadListeners);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            MusicClientLogic.getInstance().onClientTick(mc);
        }
    }

    private static void onRegisterReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "musics"), MusicHandler.INSTANCE);
    }
}