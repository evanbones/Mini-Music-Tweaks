package com.evandev.music_tweaks.client;

import com.evandev.music_tweaks.client.integration.ClothConfigIntegration;
import com.evandev.music_tweaks.client.music.MusicClientLogic;
import com.evandev.music_tweaks.client.music.MusicDumpHelper;
import com.evandev.music_tweaks.client.music.MusicEventListener;
import com.evandev.music_tweaks.client.music.MusicHandler;
import com.evandev.music_tweaks.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

public class MiniMusicTweaksClient {
    public static void register(ModContainer container, IEventBus modEventBus) {
        if (Services.PLATFORM.isModLoaded("cloth_config")) {
            container.registerExtensionPoint(IConfigScreenFactory.class, (mc, parent) -> ClothConfigIntegration.createScreen(parent));
        }

        NeoForge.EVENT_BUS.addListener(MiniMusicTweaksClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(MiniMusicTweaksClient::onRegisterClientCommands);

        modEventBus.addListener(MiniMusicTweaksClient::onClientSetup);
        modEventBus.addListener(MiniMusicTweaksClient::onRegisterReloadListeners);
    }

    private static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("music_tweaks")
                .then(Commands.literal("dump")
                        .executes(context -> {
                            MusicDumpHelper.dumpMusicIds(msg -> context.getSource().sendSystemMessage(msg));
                            return 1;
                        })
                )
        );
        event.getDispatcher().register(Commands.literal("musictweaks")
                .then(Commands.literal("dump")
                        .executes(context -> {
                            MusicDumpHelper.dumpMusicIds(msg -> context.getSource().sendSystemMessage(msg));
                            return 1;
                        })
                )
        );
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            MusicClientLogic.getInstance().onClientTick(mc);
        }
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Minecraft.getInstance().getSoundManager().addListener(new MusicEventListener());
        });
    }

    private static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(MusicHandler.INSTANCE);
    }
}