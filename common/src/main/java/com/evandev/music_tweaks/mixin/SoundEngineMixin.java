package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.client.music.MusicClientLogic;
import com.evandev.music_tweaks.config.ModConfig;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {

    @Inject(method = "calculateVolume(Lnet/minecraft/client/resources/sounds/SoundInstance;)F", at = @At("RETURN"), cancellable = true)
    private void onCalculateVolume(SoundInstance sound, CallbackInfoReturnable<Float> cir) {
        if (sound.getSource() == SoundSource.MUSIC || sound.getSource() == SoundSource.RECORDS) {
            if (ModConfig.get().enabled) {
                MusicClientLogic logic = MusicClientLogic.getInstance();
                if (!logic.isCombatSound(sound)) {
                    cir.setReturnValue(cir.getReturnValueF() * logic.getVanillaMusicMultiplier());
                }
            }
        }
    }
}