package com.evandev.music_tweaks.client.toast;

import com.evandev.music_tweaks.Constants;
import com.evandev.music_tweaks.client.jukebox.JukeboxOffsetState;
import com.evandev.music_tweaks.client.music.MusicHandler;
import com.evandev.music_tweaks.config.ModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class MusicToast implements Toast {
    public static final Object TOKEN = new Object();
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "toast/music_toast_bg");

    private static MusicToast activeMusicToast;
    private static MusicToast activeRecordToast;

    private final ItemStack iconItem;
    private final ResourceLocation iconTexture;
    private final Component displayText;
    private final int width;
    private final SoundInstance soundInstance;
    private final long startTime;
    private boolean forceSnapHidden = false;
    private boolean wasMenuOpen = false;

    public MusicToast(MusicHandler.MusicMetadata music, ItemStack icon, SoundInstance soundInstance) {
        this.iconItem = icon;
        this.iconTexture = null;
        this.displayText = buildText(music);
        this.width = calculateWidth(this.displayText);
        this.soundInstance = soundInstance;
        this.startTime = Util.getMillis();
        assignAsActive(this);
    }

    public MusicToast(MusicHandler.MusicMetadata music, ResourceLocation icon, SoundInstance soundInstance) {
        this.iconItem = null;
        this.iconTexture = icon;
        this.displayText = buildText(music);
        this.width = calculateWidth(this.displayText);
        this.soundInstance = soundInstance;
        this.startTime = Util.getMillis();
        assignAsActive(this);
    }

    private static void assignAsActive(MusicToast toast) {
        if (toast.soundInstance.getSource() == SoundSource.RECORDS) {
            activeRecordToast = toast;
        } else {
            activeMusicToast = toast;
        }
    }

    /**
     * Determines which toast is the active one based on audio priority and distance.
     */
    public static MusicToast getActiveToast() {
        Minecraft mc = Minecraft.getInstance();

        if (activeRecordToast != null) {
            if (!mc.getSoundManager().isActive(activeRecordToast.soundInstance)) {
                activeRecordToast = null;
            } else if (JukeboxOffsetState.isRecordHearable()) {
                return activeRecordToast;
            }
        }

        if (activeMusicToast != null) {
            if (!mc.getSoundManager().isActive(activeMusicToast.soundInstance)) {
                activeMusicToast = null;
            } else {
                return activeMusicToast;
            }
        }

        return null;
    }

    public static void resurrectIfPlaying() {
        MusicToast validToast = getActiveToast();
        if (validToast != null) {
            ToastComponent toasts = Minecraft.getInstance().getToasts();
            if (toasts.getToast(MusicToast.class, TOKEN) != validToast) {
                validToast.forceSnapHidden = false;
                toasts.addToast(validToast);
            }
        }
    }

    private static Component buildText(MusicHandler.MusicMetadata music) {
        boolean hasAuthor = !music.author().getString().isEmpty();
        if (hasAuthor) {
            return Component.literal(music.author().getString() + " - " + music.title().getString());
        } else {
            return music.title();
        }
    }

    private static int calculateWidth(Component text) {
        int textWidth = Minecraft.getInstance().font.width(text);
        return Math.max(160, 30 + textWidth + 8);
    }

    @Override
    public int height() {
        return 30;
    }

    @Override
    public @NotNull Object getToken() {
        return TOKEN;
    }

    public boolean isMenuOpen() {
        Minecraft mc = Minecraft.getInstance();
        return mc.screen instanceof PauseScreen ||
                mc.screen instanceof OptionsScreen ||
                mc.screen instanceof OptionsSubScreen;
    }

    public boolean shouldBeSilent() {
        if (!ModConfig.get().playToastSound) return true;
        if (this != getActiveToast()) return true;
        return ModConfig.get().permanentToastInOptions && (isMenuOpen() || this.forceSnapHidden);
    }

    public boolean shouldSnapVisible() {
        return this == getActiveToast() && ModConfig.get().permanentToastInOptions && isMenuOpen();
    }

    public boolean shouldSnapHidden() {
        return this != getActiveToast() || (ModConfig.get().permanentToastInOptions && this.forceSnapHidden);
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public @NotNull Visibility render(@NotNull GuiGraphics guiGraphics, @NotNull ToastComponent toastComponent, long timeSinceLastVisible) {
        MusicToast active = getActiveToast();

        if (this != active) {
            if (active != null && ModConfig.get().permanentToastInOptions && isMenuOpen()) {
                active.forceSnapHidden = false;
                toastComponent.addToast(active);
            }
            return Visibility.HIDE;
        }

        guiGraphics.blitSprite(BACKGROUND_SPRITE, 0, 0, this.width, this.height());

        if (iconTexture != null) {
            int iconX = 8;
            int iconY = 8;

            long time = Util.getMillis();
            int vOffset;
            int texHeight;

            AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(iconTexture);
            if (texture.getClass().getName().endsWith("AnimatableTexture")) {
                try {
                    int tick = (int) (time / 50L);
                    texture.getClass().getMethod("setAnimationFrame", int.class).invoke(texture, tick);
                } catch (Exception ignored) {
                }
                vOffset = 0;
                texHeight = 16;
            } else {
                int frame = (int) ((time / 100L) % 8L);
                vOffset = frame * 16;
                texHeight = 128;
            }

            float hue = (time % 6000L) / 6000.0f;
            int color = Color.HSBtoRGB(hue, 1.0f, 1.0f);
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            RenderSystem.setShaderColor(r, g, b, 1.0F);
            guiGraphics.blit(iconTexture, iconX, iconY, 0, vOffset, 16, 16, 16, texHeight);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else if (iconItem != null) {
            guiGraphics.renderFakeItem(iconItem, 8, 8);
        }

        guiGraphics.drawString(toastComponent.getMinecraft().font, this.displayText, 30, 12, 0xFFFFFFFF);

        boolean currentMenuOpen = isMenuOpen();
        long elapsed = Util.getMillis() - this.startTime;

        if (ModConfig.get().permanentToastInOptions) {
            if (!currentMenuOpen && this.wasMenuOpen && elapsed >= 5000L) {
                this.forceSnapHidden = true;
            }
            this.wasMenuOpen = currentMenuOpen;
            if (currentMenuOpen) {
                return Visibility.SHOW;
            }
        }

        return elapsed >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }
}