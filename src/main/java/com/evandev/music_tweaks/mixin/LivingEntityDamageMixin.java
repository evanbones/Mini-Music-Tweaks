package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.client.music.MusicClientLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityDamageMixin {
    @Inject(method = "handleDamageEvent", at = @At("HEAD"))
    private void musicTweaks_onHandleDamageEvent(DamageSource damageSource, CallbackInfo ci) {
        if ((Object) this == Minecraft.getInstance().player) {
            MusicClientLogic.getInstance().onPlayerDamaged(damageSource);
        }
    }
}
