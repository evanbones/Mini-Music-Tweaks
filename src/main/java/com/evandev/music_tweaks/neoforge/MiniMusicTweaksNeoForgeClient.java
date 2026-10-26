package com.evandev.music_tweaks.neoforge;

//? if neoforge {
import com.evandev.music_tweaks.client.integration.YACLIntegration;
import com.evandev.music_tweaks.client.music.MusicClientLogic;
import com.evandev.music_tweaks.platform.Services;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

//? if <26.1 {
import com.evandev.music_tweaks.client.music.MusicDumpHelper;
import com.evandev.music_tweaks.client.music.MusicEventListener;
import com.evandev.music_tweaks.client.music.MusicHandler;
import net.minecraft.commands.Commands;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
//?}

public class MiniMusicTweaksNeoForgeClient {

    public static void register(ModContainer container, IEventBus modEventBus) {
        if (Services.PLATFORM.isModLoaded("yet_another_config_lib_v3")) {
            container.registerExtensionPoint(IConfigScreenFactory.class, (c, parent) -> YACLIntegration.createScreen(parent));
        }

        NeoForge.EVENT_BUS.addListener(MiniMusicTweaksNeoForgeClient::onClientTick);

        //? if <26.1 {
        NeoForge.EVENT_BUS.addListener(MiniMusicTweaksNeoForgeClient::onRegisterClientCommands);
        modEventBus.addListener(MiniMusicTweaksNeoForgeClient::onClientSetup);
        modEventBus.addListener(MiniMusicTweaksNeoForgeClient::onRegisterReloadListeners);
        //?}
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            MusicClientLogic.getInstance().onClientTick(mc);
        }
    }

    //? if <26.1 {
    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> Minecraft.getInstance().getSoundManager().addListener(new MusicEventListener()));
    }

    private static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(MusicHandler.INSTANCE);
    }

    private static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        for (String name : new String[]{"music_tweaks", "musictweaks"}) {
            event.getDispatcher().register(Commands.literal(name)
                    .then(Commands.literal("dump")
                            .executes(context -> {
                                MusicDumpHelper.dumpMusicIds(msg -> context.getSource().sendSystemMessage(msg));
                                return 1;
                            })
                    )
            );
        }
    }
    //?}
}
//?}
