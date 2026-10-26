package com.evandev.music_tweaks.client.jukebox;

import com.evandev.music_tweaks.Constants;
import com.evandev.music_tweaks.client.ClientNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;

//? if >=1.21 {
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQueryPacket;
import net.minecraft.world.item.JukeboxSong;

import java.util.Optional;
//?} else {
/*import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
*///?}
//? if >=26.1 {
/*import net.minecraft.server.permissions.Permissions;
*///?}

public class JukeboxSyncHandler {
    //? if >=1.21 {
    private static final TagKey<JukeboxSong> QUARK_AMBIENT = TagKey.create(Registries.JUKEBOX_SONG, Constants.parse("quark:ambient"));
    //?} else {
    /*private static final TagKey<Item> QUARK_AMBIENT = TagKey.create(Registries.ITEM, Constants.parse("quark:ambient"));
    *///?}

    public static boolean requestSync(Minecraft client, BlockPos pos) {
        if (ClientNetworking.isModLoadedOnServer()) {
            JukeboxOffsetState.registerCustomQuery(pos);
            ClientNetworking.sendJukeboxSyncRequest(pos);
            return true;
        }

        if (client.player == null || client.getConnection() == null) return false;

        //? if >=26.1 {
        /*boolean canQuery = client.player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
        *///?} else {
        boolean canQuery = client.player.hasPermissions(2);
        //?}
        if (!canQuery) return false;

        int transactionId = JukeboxOffsetState.registerQuery(pos);
        //? if >=1.21 {
        client.getConnection().send(new ServerboundBlockEntityTagQueryPacket(transactionId, pos));
        //?} else {
        /*client.getConnection().send(new ServerboundBlockEntityTagQuery(transactionId, pos));
        *///?}
        return true;
    }

    public static void playSyncedSong(BlockPos pos, long ticksSinceStart, ItemStack recordItem) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> {
            Level world = client.level;
            if (world != null) {
                BlockState state = world.getBlockState(pos);
                if (state.hasProperty(JukeboxBlock.HAS_RECORD) && state.getValue(JukeboxBlock.HAS_RECORD)) {
                    if (!JukeboxOffsetState.hasActiveSound(pos)) {
                        //? if >=26.1 {
                        /*Optional<Holder<JukeboxSong>> songEntry = JukeboxSong.fromStack(recordItem);
                        *///?} else if >=1.21 {
                        Optional<Holder<JukeboxSong>> songEntry = JukeboxSong.fromStack(world.registryAccess(), recordItem);
                        //?}

                        //? if >=1.21 {
                        if (songEntry.isPresent()) {
                            SoundEvent soundEvent = songEntry.get().value().soundEvent().value();
                            boolean ambient = songEntry.get().is(QUARK_AMBIENT);
                        //?} else {
                        /*if (!recordItem.isEmpty() && recordItem.getItem() instanceof RecordItem record) {
                            SoundEvent soundEvent = record.getSound();
                            boolean ambient = recordItem.is(QUARK_AMBIENT);
                        *///?}
                            JukeboxOffsetState.removeCancelledSound(pos);

                            if (ambient) {
                                SimpleSoundInstance instance = new SimpleSoundInstance(
                                        //? if >=26.1 {
                                        /*soundEvent.location(), SoundSource.RECORDS, 4.0F, 1.0F,
                                        *///?} else {
                                        soundEvent.getLocation(), SoundSource.RECORDS, 4.0F, 1.0F,
                                        //?}
                                        SoundInstance.createUnseededRandom(), true, 0,
                                        SoundInstance.Attenuation.LINEAR, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, false
                                );
                                client.getSoundManager().play(instance);
                            } else {
                                float offsetSeconds = (float) ticksSinceStart / 20.0F;
                                Constants.LOG.debug("Resuming jukebox song at {} from {}s", pos, offsetSeconds);
                                OffsetSoundInstance instance = new OffsetSoundInstance(
                                        soundEvent, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D
                                );
                                JukeboxOffsetState.markOurSound(instance);
                                PendingStreamOffset.set(offsetSeconds);
                                client.getSoundManager().play(instance);
                                JukeboxOffsetState.trackSound(pos, instance);
                            }
                        } else {
                            playFallback(pos);
                        }
                    } else {
                        JukeboxOffsetState.removeCancelledSound(pos);
                    }
                } else {
                    JukeboxOffsetState.removeCancelledSound(pos);
                }
            }
        });
    }

    public static void playFallback(BlockPos pos) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> {
            SoundInstance original = JukeboxOffsetState.getAndRemoveCancelledSound(pos);
            if (original != null) {
                JukeboxOffsetState.markOurSound(original);
                client.getSoundManager().play(original);
                JukeboxOffsetState.trackSound(pos, original);
            } else {
                JukeboxOffsetState.markIdle(pos);
            }
        });
    }
}
