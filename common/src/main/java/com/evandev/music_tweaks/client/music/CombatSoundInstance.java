package com.evandev.music_tweaks.client.music;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class CombatSoundInstance extends AbstractTickableSoundInstance {
    private final int fadeDuration = 40;
    private boolean fadingOut = false;
    private float currentFade = 0.0f;

    public CombatSoundInstance(SoundEvent sound) {
        super(sound, SoundSource.MUSIC, SoundInstance.createUnseededRandom());
        this.looping = true;
        this.delay = 0;
        this.volume = 0.001f;
        this.relative = true;
        this.attenuation = Attenuation.NONE;
    }

    public void fadeOut() {
        this.fadingOut = true;
    }

    @Override
    public void tick() {
        if (this.fadingOut) {
            this.currentFade -= 1.0f / fadeDuration;
            if (this.currentFade <= 0.0f) {
                this.currentFade = 0.0f;
                this.stop();
            }
        } else {
            if (this.currentFade < 1.0f) {
                this.currentFade += 1.0f / fadeDuration;
                if (this.currentFade > 1.0f) {
                    this.currentFade = 1.0f;
                }
            }
        }
        this.volume = this.currentFade;
    }
}