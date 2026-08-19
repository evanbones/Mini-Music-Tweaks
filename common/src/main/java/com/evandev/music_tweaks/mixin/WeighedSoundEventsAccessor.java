package com.evandev.music_tweaks.mixin;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.client.sounds.Weighted;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(WeighedSoundEvents.class)
public interface WeighedSoundEventsAccessor {
    @Accessor("list")
    List<Weighted<Sound>> getList();
}
