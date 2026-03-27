package com.evandev.music_tweaks.client.music;

import com.evandev.music_tweaks.Constants;
import com.evandev.music_tweaks.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class MusicClientLogic {

    private static MusicClientLogic INSTANCE;
    private CombatSoundInstance currentCombatMusic;
    private boolean inCombat = false;
    private int ticksSinceCombatUpdate = 0;
    private boolean wasHurtLastTick = false;

    private MusicClientLogic() {
    }

    public static MusicClientLogic getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MusicClientLogic();
        }
        return INSTANCE;
    }

    public boolean isCombatSound(SoundInstance sound) {
        return sound == currentCombatMusic;
    }

    public boolean isInCombat() {
        return inCombat;
    }

    public void onClientTick(Minecraft mc) {
        ModConfig config = ModConfig.get();
        if (mc.level == null || !config.enabled) return;

        LocalPlayer player = mc.player;
        if (player == null) return;

        boolean isHurt = player.hurtTime > 0;
        if (isHurt && !wasHurtLastTick) {
            if (mc.level.getDifficulty() != Difficulty.PEACEFUL) {
                ticksSinceCombatUpdate = 0;
                inCombat = true;
            }
        }
        wasHurtLastTick = isHurt;

        if (mc.level.getGameTime() % 20 == 0) {
            int entityCount = getEntities(player);
            if (entityCount > config.minPursuitEntities) {
                ticksSinceCombatUpdate = 0;
                inCombat = true;
            } else {
                ticksSinceCombatUpdate += 20;
            }

            if (inCombat && ticksSinceCombatUpdate > config.decayTime * 20) {
                endCombat();
            }
        }

        if (inCombat) {
            if (currentCombatMusic == null || currentCombatMusic.isStopped()) {
                startCombatMusic(mc, config);
            }
        }
    }

    private void startCombatMusic(Minecraft mc, ModConfig config) {
        SoundEvent sound = pickSound(mc.player.getRandom(), config);
        if (sound != null) {
            currentCombatMusic = new CombatSoundInstance(sound);
            mc.getSoundManager().play(currentCombatMusic);
        }
    }

    private void endCombat() {
        inCombat = false;
        if (currentCombatMusic != null) {
            currentCombatMusic.fadeOut();
        }
    }

    private int getEntities(LocalPlayer player) {
        if (player.isCreative() || player.isSpectator()) {
            return 0;
        }

        AABB box = new AABB(-12D, -10D, -12D, 12D, 10D, 12D).move(player.blockPosition());
        return player.clientLevel.getEntitiesOfClass(
                Monster.class,
                box,
                LivingEntity::isAlive
        ).size();
    }

    private SoundEvent pickSound(RandomSource rand, ModConfig config) {
        List<String> soundList = config.sounds;
        if (soundList.isEmpty()) return null;

        int idx = rand.nextInt(soundList.size());
        String soundStr = soundList.get(idx).trim();

        ResourceLocation soundLocation = ResourceLocation.parse(soundStr);
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(soundLocation);

        if (sound == null) {
            Constants.LOG.error("Invalid sound event resource location detected: {}", soundStr);
            return null;
        }
        return sound;
    }
}