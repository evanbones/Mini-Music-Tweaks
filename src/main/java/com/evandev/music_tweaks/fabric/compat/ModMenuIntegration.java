package com.evandev.music_tweaks.fabric.compat;

//? if fabric {
/*import com.evandev.music_tweaks.client.integration.YACLIntegration;
import com.evandev.music_tweaks.platform.Services;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (Services.PLATFORM.isModLoaded("yet_another_config_lib_v3")) {
            return YACLIntegration::createScreen;
        }

        return parent -> null;
    }
}
*///?}
