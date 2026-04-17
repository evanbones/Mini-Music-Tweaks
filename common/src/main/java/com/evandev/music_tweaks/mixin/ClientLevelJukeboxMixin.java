package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.client.jukebox.JukeboxOffsetState;
import com.evandev.music_tweaks.client.jukebox.OffsetSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(ClientLevel.class)
public class ClientLevelJukeboxMixin {
    @Unique
    private static final double SCAN_RADIUS_SQ = 4900.0D;

    @Inject(method = "tick", at = @At("TAIL"))
    private void jukeboxSync_onTick(CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && client.getConnection() != null) {
            ClientLevel level = (ClientLevel) (Object) this;
            SoundManager soundManager = client.getSoundManager();
            Player player = client.player;
            double px = player.getX();
            double py = player.getY();
            double pz = player.getZ();
            List<BlockPos> toRemove = new ArrayList<>();

            for (Map.Entry<BlockPos, SoundInstance> entry : JukeboxOffsetState.getAllActiveSounds().entrySet()) {
                BlockPos soundPos = entry.getKey();
                SoundInstance soundInstance = entry.getValue();
                BlockState state = level.getBlockState(soundPos);

                boolean discGone = !state.hasProperty(JukeboxBlock.HAS_RECORD) || !state.getValue(JukeboxBlock.HAS_RECORD);
                if (discGone) {
                    soundManager.stop(soundInstance);
                    JukeboxOffsetState.cancelQuery(soundPos);
                    toRemove.add(soundPos);
                } else if (!soundManager.isActive(soundInstance)) {
                    toRemove.add(soundPos);
                    if (soundInstance instanceof OffsetSoundInstance osi) {
                        osi.stop();
                    }
                }
            }

            toRemove.forEach(JukeboxOffsetState::removeSound);
            int playerChunkX = ((int) px) >> 4;
            int playerChunkZ = ((int) pz) >> 4;

            for (int cx = playerChunkX - 5; cx <= playerChunkX + 5; ++cx) {
                for (int cz = playerChunkZ - 5; cz <= playerChunkZ + 5; ++cz) {
                    LevelChunk chunk = level.getChunk(cx, cz);
                    if (chunk != null) {
                        for (BlockEntity be : chunk.getBlockEntities().values()) {
                            if (be instanceof JukeboxBlockEntity) {
                                BlockPos pos = be.getBlockPos();
                                double dx = pos.getX() + 0.5D - px;
                                double dy = pos.getY() + 0.5D - py;
                                double dz = pos.getZ() + 0.5D - pz;
                                if (dx * dx + dy * dy + dz * dz <= SCAN_RADIUS_SQ
                                        && !JukeboxOffsetState.hasActiveSound(pos)
                                        && !JukeboxOffsetState.hasPendingQuery(pos)) {

                                    BlockState blockState = level.getBlockState(pos);
                                    if (blockState.hasProperty(JukeboxBlock.HAS_RECORD) && blockState.getValue(JukeboxBlock.HAS_RECORD)) {
                                        int transactionId = JukeboxOffsetState.registerQuery(pos);
                                        client.getConnection().send(new ServerboundBlockEntityTagQuery(transactionId, pos));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}