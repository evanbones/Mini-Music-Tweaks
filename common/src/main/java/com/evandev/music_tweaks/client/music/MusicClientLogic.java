package com.evandev.music_tweaks.client.music;

import com.evandev.music_tweaks.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        if (mc.level == null || !config.general.enabled.value()) return;

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
            if (entityCount > config.general.minPursuitEntities.value()) {
                ticksSinceCombatUpdate = 0;
                inCombat = true;
            } else {
                ticksSinceCombatUpdate += 20;
            }

            if (inCombat && ticksSinceCombatUpdate > config.general.decayTime.value() * 20) {
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
        Optional<Holder.Reference<SoundEvent>> sound = pickSound(Objects.requireNonNull(mc.player).getRandom(), config);
        if (sound.isPresent()) {
            currentCombatMusic = new CombatSoundInstance(sound.get().value());
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
        return player.level().getEntitiesOfClass(
                Monster.class,
                box,
                LivingEntity::isAlive
        ).size();
    }

    private Optional<Holder.Reference<SoundEvent>> pickSound(RandomSource rand, ModConfig config) {
        List<String> soundList = config.general.sounds.value();
        if (soundList.isEmpty()) return Optional.empty();

        int idx = rand.nextInt(soundList.size());
        String soundStr = soundList.get(idx).trim();

        Identifier soundLocation = Identifier.parse(soundStr);

        return BuiltInRegistries.SOUND_EVENT.get(soundLocation);
    }
}