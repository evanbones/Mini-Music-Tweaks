package com.evandev.music_tweaks;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class ModTags {
    public static final TagKey<DamageType> DOES_NOT_TRIGGER_COMBAT_MUSIC = TagKey.create(
            Registries.DAMAGE_TYPE,
            Constants.id("does_not_trigger_combat_music")
    );
}
