package com.evandev.music_tweaks.neoforge;

//? if neoforge {
import com.evandev.music_tweaks.CommonClass;
import com.evandev.music_tweaks.Constants;
import com.evandev.music_tweaks.client.ClientNetworking;
import com.evandev.music_tweaks.network.JukeboxSyncRequestPayload;
import com.evandev.music_tweaks.network.JukeboxSyncResponsePayload;
import com.evandev.music_tweaks.network.JukeboxSyncServer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(Constants.MOD_ID)
public class MiniMusicTweaksNeoForge {
    public MiniMusicTweaksNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloads);

        //? if >=26.1 {
        /*if (FMLEnvironment.getDist().isClient()) {
        *///?} else {
        if (FMLEnvironment.dist.isClient()) {
        //?}
            MiniMusicTweaksNeoForgeClient.register(modContainer, modEventBus);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CommonClass.init();
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Constants.MOD_ID).versioned("1.0").optional();

        registrar.playToServer(
                JukeboxSyncRequestPayload.TYPE,
                JukeboxSyncRequestPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        JukeboxSyncServer.createResponse(context.player().level(), payload.pos()).ifPresent(context::reply))
        );

        registrar.playToClient(
                JukeboxSyncResponsePayload.TYPE,
                JukeboxSyncResponsePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientNetworking.handleJukeboxSyncResponse(payload))
        );
    }
}
//?}
