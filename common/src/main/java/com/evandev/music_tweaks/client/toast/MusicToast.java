package com.evandev.music_tweaks.client.toast;

import com.evandev.music_tweaks.client.music.MusicHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class MusicToast implements Toast {
    private final MusicHandler.MusicMetadata music;
    private final ItemStack iconItem;
    private final ResourceLocation iconTexture;

    public MusicToast(MusicHandler.MusicMetadata music, ItemStack icon) {
        this.music = music;
        this.iconItem = icon;
        this.iconTexture = null;
    }

    public MusicToast(MusicHandler.MusicMetadata music, ResourceLocation icon) {
        this.music = music;
        this.iconItem = null;
        this.iconTexture = icon;
    }

    @Override
    public @NotNull Visibility render(GuiGraphics guiGraphics, @NotNull ToastComponent toastComponent, long timeSinceLastVisible) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        guiGraphics.blit(TEXTURE, 0, 0, 0, 0, this.width(), this.height());

        if (iconTexture != null) {
            int iconX = 8;
            int iconY = 8;

            long time = Util.getMillis();
            int frame = (int) ((time / 100L) % 8L);
            int vOffset = frame * 16;

            float hue = (time % 6000L) / 6000.0f;
            int color = Color.HSBtoRGB(hue, 1.0f, 1.0f);
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            RenderSystem.setShaderColor(r, g, b, 1.0F);
            guiGraphics.blit(iconTexture, iconX, iconY, 0, vOffset, 16, 16, 16, 128);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        } else if (iconItem != null) {
            guiGraphics.renderFakeItem(iconItem, 8, 8);
        }

        int textLeft = 30;
        boolean hasAuthor = !music.author().getString().isEmpty();

        if (hasAuthor) {
            guiGraphics.drawString(toastComponent.getMinecraft().font, music.title(), textLeft, 7, 0xFFFFFF00);
            guiGraphics.drawString(toastComponent.getMinecraft().font, music.author(), textLeft, 18, 0xFFFFFFFF);
        } else {
            guiGraphics.drawString(toastComponent.getMinecraft().font, music.title(), textLeft, 12, 0xFFFFFF00);
        }

        return timeSinceLastVisible >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }
}