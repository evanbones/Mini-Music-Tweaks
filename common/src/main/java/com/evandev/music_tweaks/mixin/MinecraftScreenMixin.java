package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.client.toast.MusicToast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftScreenMixin {

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void musicTweaks$resurrectMusicToast(Screen screen, CallbackInfo ci) {
        if (screen instanceof PauseScreen || screen instanceof OptionsScreen || screen instanceof OptionsSubScreen) {
            MusicToast.resurrectIfPlaying();
        }
    }
}