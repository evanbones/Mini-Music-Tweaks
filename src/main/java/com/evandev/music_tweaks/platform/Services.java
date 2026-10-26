package com.evandev.music_tweaks.platform;

//? if fabric {
/*import net.fabricmc.loader.api.FabricLoader;
*///?}
//? if forge {
/*import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
*///?}
//? if neoforge {
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
//?}

import java.nio.file.Path;

public class Services {
    public static final Services PLATFORM = new Services();

    public boolean isModLoaded(String modId) {
        //? if fabric {
        /*return FabricLoader.getInstance().isModLoaded(modId);
        *///?} else {
        return ModList.get().isLoaded(modId);
        //?}
    }

    public Path getConfigDirectory() {
        //? if fabric {
        /*return FabricLoader.getInstance().getConfigDir();
        *///?} else {
        return FMLPaths.CONFIGDIR.get();
        //?}
    }
}
