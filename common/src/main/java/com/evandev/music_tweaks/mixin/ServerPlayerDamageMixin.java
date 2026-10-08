package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerDamageMixin {
    @Inject(method = "hurt", at = @At("RETURN"))
    private void musicTweaks_onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            Services.PLATFORM.sendCombatDamage((ServerPlayer) (Object) this, source.getMsgId());
        }
    }
}
