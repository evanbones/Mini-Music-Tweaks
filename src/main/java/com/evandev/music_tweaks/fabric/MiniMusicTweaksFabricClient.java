package com.evandev.music_tweaks.fabric;

//? if fabric {
/*import com.evandev.music_tweaks.client.ClientNetworking;
import com.evandev.music_tweaks.client.music.MusicClientLogic;
import com.evandev.music_tweaks.network.JukeboxSyncResponsePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

//? if <26.1 {
import com.evandev.music_tweaks.Constants;
import com.evandev.music_tweaks.client.music.MusicDumpHelper;
import com.evandev.music_tweaks.client.music.MusicEventListener;
import com.evandev.music_tweaks.client.music.MusicHandler;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
//?}

public class MiniMusicTweaksFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level != null) {
                MusicClientLogic.getInstance().onClientTick(client);
            }
        });

        //? if >=1.21 {
        ClientPlayNetworking.registerGlobalReceiver(JukeboxSyncResponsePayload.TYPE, (payload, context) ->
                context.client().execute(() -> ClientNetworking.handleJukeboxSyncResponse(payload)));
        //?} else {
        /^ClientPlayNetworking.registerGlobalReceiver(JukeboxSyncResponsePayload.ID, (client, handler, buf, responseSender) -> {
            JukeboxSyncResponsePayload payload = JukeboxSyncResponsePayload.read(buf);
            client.execute(() -> ClientNetworking.handleJukeboxSyncResponse(payload));
        });
        ^///?}

        //? if <26.1 {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return Constants.id("musics");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                MusicHandler.INSTANCE.onResourceManagerReload(resourceManager);
            }
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> client.getSoundManager().addListener(new MusicEventListener()));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            for (String name : new String[]{"music_tweaks", "musictweaks"}) {
                dispatcher.register(ClientCommandManager.literal(name)
                        .then(ClientCommandManager.literal("dump")
                                .executes(context -> {
                                    MusicDumpHelper.dumpMusicIds(msg -> context.getSource().sendFeedback(msg));
                                    return 1;
                                })
                        )
                );
            }
        });
        //?}
    }
}
*///?}
