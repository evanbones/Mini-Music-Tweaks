package com.evandev.music_tweaks.config;

import com.evandev.music_tweaks.Constants;
import com.evandev.music_tweaks.platform.Services;
import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueList;

public class ModConfig extends ReflectiveConfig {

    public static final ModConfig INSTANCE = ReflectiveConfig.createToml(
            Services.PLATFORM.getConfigDirectory(),
            Constants.MOD_ID,
            "main",
            ModConfig.class
    );
    public final General general = new General();

    public static ModConfig get() {
        return INSTANCE;
    }

    public static class General extends Section {

        @Comment("Toggle the dynamic combat music feature on or off.")
        public final TrackedValue<Boolean> enabled = this.value(false);

        @Comment("The minimum amount of hostile mobs that must be nearby to trigger combat music.")
        public final TrackedValue<Integer> minPursuitEntities = this.value(3);

        @Comment("How long combat music will continue playing after no hostile mobs are nearby.")
        public final TrackedValue<Integer> decayTime = this.value(3);

        @Comment("List of SoundEvent Resource Locations that can be chosen for combat music.")
        public final TrackedValue<ValueList<String>> sounds = this.value(ValueList.create(
                "",
                "minecraft:music_disc.mellohi", "minecraft:music_disc.pigstep"
        ));

        @Comment("Fades out background music when near a playing Jukebox.")
        public final TrackedValue<Boolean> betterJukeboxes = this.value(true);

        @Comment("The radius in blocks around a jukebox that will fade out background music.")
        public final TrackedValue<Double> jukeboxDistance = this.value(64.0);
    }
}