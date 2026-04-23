package com.evandev.music_tweaks;

import com.evandev.music_tweaks.client.MiniMusicTweaksClient;
import com.evandev.music_tweaks.client.jukebox.JukeboxOffsetState;
import com.evandev.music_tweaks.client.jukebox.JukeboxSyncHandler;
import com.evandev.music_tweaks.network.JukeboxSyncRequestPayload;
import com.evandev.music_tweaks.network.JukeboxSyncResponsePayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Optional;

@Mod(Constants.MOD_ID)
public class MiniMusicTweaks {
    public MiniMusicTweaks(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerNetwork); // Add Network Event

        if (FMLEnvironment.dist.isClient()) {
            MiniMusicTweaksClient.register(modContainer, modEventBus);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CommonClass.init();
    }

    private void registerNetwork(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Constants.MOD_ID).versioned("1.0");

        registrar.playToServer(
                JukeboxSyncRequestPayload.ID,
                JukeboxSyncRequestPayload.CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        if (context.player() instanceof ServerPlayer serverPlayer) {
                            var level = serverPlayer.serverLevel();
                            if (level.getBlockEntity(payload.pos()) instanceof JukeboxBlockEntity jukebox) {
                                CompoundTag nbt = jukebox.saveWithFullMetadata(level.registryAccess());
                                long ticks = nbt.getLong("ticks_since_song_started");
                                CompoundTag recordNbt = nbt.getCompound("RecordItem");

                                Optional<ItemStack> recordItemOpt = ItemStack.parse(level.registryAccess(), recordNbt);
                                if (recordItemOpt.isPresent() && !recordItemOpt.get().isEmpty()) {
                                    context.reply(new JukeboxSyncResponsePayload(payload.pos(), ticks, recordItemOpt.get()));
                                }
                            }
                        }
                    });
                }
        );

        registrar.playToClient(
                JukeboxSyncResponsePayload.ID,
                JukeboxSyncResponsePayload.CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        JukeboxOffsetState.resolveCustomQuery(payload.pos());
                        JukeboxSyncHandler.playSyncedSong(payload.pos(), payload.ticksSinceStart(), payload.recordItem());
                    });
                }
        );
    }
}