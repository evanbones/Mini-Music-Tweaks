package com.evandev.music_tweaks;

import com.evandev.music_tweaks.client.MiniMusicTweaksClient;
import com.evandev.music_tweaks.network.NetworkHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkConstants;

@Mod(Constants.MOD_ID)
public class MiniMusicTweaks {
    public MiniMusicTweaks() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        NetworkHandler.register();

        ModLoadingContext.get().registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> NetworkConstants.IGNORESERVERONLY,
                        (serverVersion, isNetwork) -> true
                )
        );

        if (FMLEnvironment.dist.isClient()) {
            MiniMusicTweaksClient.register(ModLoadingContext.get().getActiveContainer(), modEventBus);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CommonClass::init);
    }
}