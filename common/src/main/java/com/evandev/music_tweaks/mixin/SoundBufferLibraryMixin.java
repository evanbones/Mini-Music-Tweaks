package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.client.jukebox.OffsetAudioStream;
import com.evandev.music_tweaks.client.jukebox.PendingStreamOffset;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(SoundBufferLibrary.class)
public class SoundBufferLibraryMixin {
    @Inject(method = "getStream", at = @At("RETURN"), cancellable = true)
    private void jukeboxSync_wrapStream(Identifier location, boolean looping, CallbackInfoReturnable<CompletableFuture<AudioStream>> cir) {
        float offset = PendingStreamOffset.consume();
        if (offset > 0.0F) {
            CompletableFuture<AudioStream> wrapped = cir.getReturnValue().thenApply(stream -> new OffsetAudioStream(stream, offset));
            cir.setReturnValue(wrapped);
        }
    }
}