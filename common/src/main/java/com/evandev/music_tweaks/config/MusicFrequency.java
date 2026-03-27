package com.evandev.music_tweaks.config;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum MusicFrequency implements StringRepresentable {
    DEFAULT("Default"),
    FREQUENT("Frequent"),
    CONSTANT("Constant");

    public static final StringRepresentable.EnumCodec<MusicFrequency> CODEC = StringRepresentable.fromEnum(MusicFrequency::values);
    private final String name;

    MusicFrequency(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }

    public Component getDisplayName() {
        return Component.literal(this.name);
    }
}