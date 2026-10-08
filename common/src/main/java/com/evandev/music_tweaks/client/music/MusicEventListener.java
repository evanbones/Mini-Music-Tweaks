package com.evandev.music_tweaks.client.music;

import com.evandev.music_tweaks.Constants;
import com.evandev.music_tweaks.client.toast.MusicToast;
import com.evandev.music_tweaks.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import org.jetbrains.annotations.NotNull;

public class MusicEventListener implements SoundEventListener {
    private static final ResourceLocation MUSIC_NOTES = new ResourceLocation(Constants.MOD_ID, "textures/gui/music_notes.png");

    @Override
    public void onPlaySound(@NotNull SoundInstance sound, @NotNull WeighedSoundEvents soundSet) {
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
            RecordItem discItem = findDiscBySound(sound);

            if (discItem == null) {
                return;
            }

            MusicHandler.MusicMetadata metadata = MusicHandler.getDiscInfo(discItem);
            if (MusicToast.isDuplicate(sound.getSource(), metadata)) return;

            ItemStack icon = new ItemStack(discItem);
            mc.getToasts().addToast(new MusicToast(metadata, icon, sound));
        } else {
            MusicHandler.MusicMetadata metadata = MusicHandler.getMusicInfo(sound.getSound().getLocation());
            if (MusicToast.isDuplicate(sound.getSource(), metadata)) return;

            mc.getToasts().addToast(new MusicToast(metadata, MUSIC_NOTES, sound));
        }
    }

    private RecordItem findDiscBySound(SoundInstance sound) {
        ResourceLocation playingLocation = resolveEtchedLocation(sound);

        for (Item item : Registry.ITEM) {
            if (item instanceof RecordItem disc) {
                if (disc.getSound().getLocation().equals(playingLocation)) {
                    return disc;
                }
            }
        }
        return null;
    }

    private static ResourceLocation resolveEtchedLocation(SoundInstance sound) {
        ResourceLocation location = sound.getLocation();
        if (!location.getNamespace().equals("etched")) return location;

        try {
            Object innerSound = sound.getSound();
            if (innerSound.getClass().getName().equals("gg.moonflower.etched.api.sound.AbstractOnlineSoundInstance$OnlineSound")) {
                String url = (String) innerSound.getClass().getMethod("getURL").invoke(innerSound);
                if (url != null && !url.contains("://")) {
                    ResourceLocation parsed = ResourceLocation.tryParse(url);
                    if (parsed != null) return parsed;
                }
            }
        } catch (ReflectiveOperationException | ClassCastException e) {
            Constants.LOG.debug("Failed to extract Etched sound URL", e);
        }
        return location;
    }
}