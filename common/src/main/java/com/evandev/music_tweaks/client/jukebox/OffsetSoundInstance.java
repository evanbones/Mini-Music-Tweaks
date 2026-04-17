package com.evandev.music_tweaks.client.jukebox;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class OffsetSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
    private boolean done = false;

    public OffsetSoundInstance(SoundEvent soundEvent, double x, double y, double z) {
        super(soundEvent.getLocation(), SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.x = x;
        this.y = y;
        this.z = z;
        this.volume = 4.0F;
        this.pitch = 1.0F;
        this.looping = false;
        this.attenuation = SoundInstance.Attenuation.LINEAR;
        this.relative = false;
    }

    @Override
    public boolean isStopped() {
        return this.done;
    }

    @Override
    public void tick() {
    }

    public void stop() {
        this.done = true;
    }
}