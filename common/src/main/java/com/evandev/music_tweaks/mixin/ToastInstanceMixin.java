package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.client.toast.MusicToast;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.gui.components.toasts.ToastComponent$ToastInstance")
public class ToastInstanceMixin {

    @Shadow
    @Final
    private Toast toast;

    @Inject(method = "getVisibility", at = @At("HEAD"), cancellable = true)
    private void musicTweaks$snapVisibility(long time, CallbackInfoReturnable<Float> cir) {
        if (this.toast instanceof MusicToast musicToast) {
            if (musicToast.shouldSnapVisible()) {
                cir.setReturnValue(1.0F);
            } else if (musicToast.shouldSnapHidden()) {
                cir.setReturnValue(0.0F);
            }
        }
    }

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/toasts/Toast$Visibility;playSound(Lnet/minecraft/client/sounds/SoundManager;)V"
            )
    )
    private void musicTweaks$silenceToastSound(Toast.Visibility instance, SoundManager handler, Operation<Void> original) {
        if (this.toast instanceof MusicToast musicToast) {
            if (musicToast.shouldBeSilent()) {
                return;
            }
        }
        original.call(instance, handler);
    }
}