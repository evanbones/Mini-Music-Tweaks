package com.evandev.music_tweaks.client.music;

import com.evandev.music_tweaks.Constants;
import com.evandev.music_tweaks.client.toast.MusicToast;
import com.evandev.music_tweaks.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Optional;

public class MusicEventListener implements SoundEventListener {
    private static final ResourceLocation MUSIC_NOTES = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/music_notes.png");

    @Override
    public void onPlaySound(@NotNull SoundInstance sound, @NotNull WeighedSoundEvents soundSet, float range) {
        if (!ModConfig.get().showMusicToast) return;

        if (sound.getSource() != SoundSource.MUSIC && sound.getSource() != SoundSource.RECORDS) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc.options.getSoundSourceVolume(SoundSource.MASTER) <= 0.0F) {
            return;
        }

        if (mc.options.getSoundSourceVolume(sound.getSource()) <= 0.0F) {
            return;
        }

        Constants.LOG.debug("Music detected: {}", sound.getSound().getLocation());

        if (sound.getSource() == SoundSource.RECORDS) {
            Item discItem = findDiscBySound(sound);

            if (discItem == null) {
                return;
            }

            ItemStack icon = new ItemStack(discItem);
            MusicHandler.MusicMetadata metadata = MusicHandler.getDiscInfo(discItem);

            mc.getToasts().addToast(new MusicToast(metadata, icon));

        } else {
            MusicHandler.MusicMetadata metadata = MusicHandler.getMusicInfo(sound.getSound().getLocation());
            mc.getToasts().addToast(new MusicToast(metadata, MUSIC_NOTES));
        }
    }

    private Item findDiscBySound(SoundInstance sound) {
        ResourceLocation playingLocation = sound.getLocation();

        if (playingLocation.getNamespace().equals("etched")) {
            try {
                Object innerSound = sound.getSound();
                if (innerSound.getClass().getName().equals("gg.moonflower.etched.api.sound.AbstractOnlineSoundInstance$OnlineSound")) {
                    Method getUrlMethod = innerSound.getClass().getMethod("getURL");
                    String url = (String) getUrlMethod.invoke(innerSound);

                    if (url != null && !url.contains("://")) {
                        playingLocation = ResourceLocation.parse(url);
                    }
                }
            } catch (Exception e) {
                Constants.LOG.warn("Failed to extract Etched sound URL", e);
            }
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return null;

        for (Item item : BuiltInRegistries.ITEM) {
            JukeboxPlayable jukeboxPlayable = item.components().get(DataComponents.JUKEBOX_PLAYABLE);

            if (jukeboxPlayable != null) {
                Optional<Holder<JukeboxSong>> songHolder = jukeboxPlayable.song().unwrap(mc.level.registryAccess());
                if (songHolder.isPresent() && songHolder.get().isBound()) {
                    ResourceLocation songLocation = songHolder.get().value().soundEvent().value().getLocation();

                    if (songLocation.equals(playingLocation)) {
                        return item;
                    }
                }
            }
        }
        return null;
    }
}