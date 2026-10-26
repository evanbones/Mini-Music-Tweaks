package com.evandev.music_tweaks.forge;

//? if forge {
/*import com.evandev.music_tweaks.client.integration.YACLIntegration;
import com.evandev.music_tweaks.client.music.MusicClientLogic;
import com.evandev.music_tweaks.client.music.MusicDumpHelper;
import com.evandev.music_tweaks.client.music.MusicEventListener;
import com.evandev.music_tweaks.client.music.MusicHandler;
import com.evandev.music_tweaks.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class MiniMusicTweaksForgeClient {

    public static void register(IEventBus modEventBus) {
        if (Services.PLATFORM.isModLoaded("yet_another_config_lib_v3")) {
            ModLoadingContext.get().registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> YACLIntegration.createScreen(parent))
            );
        }

        modEventBus.addListener(MiniMusicTweaksForgeClient::onClientSetup);
        modEventBus.addListener(MiniMusicTweaksForgeClient::onRegisterReloadListeners);

        MinecraftForge.EVENT_BUS.addListener(MiniMusicTweaksForgeClient::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(MiniMusicTweaksForgeClient::onRegisterClientCommands);
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                MusicClientLogic.getInstance().onClientTick(mc);
            }
        }
    }

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
}
*///?}
