package com.evandev.music_tweaks.mixin;

import com.evandev.music_tweaks.config.ModConfig;
import com.evandev.music_tweaks.config.MusicFrequency;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(SoundOptionsScreen.class)
public abstract class SoundOptionsScreenMixin extends OptionsSubScreen {
    @Unique
    private static final int musicTweaks$ROW_HEIGHT = 22;

    @Unique
    private final List<AbstractWidget> musicTweaks$widgets = new ArrayList<>();
    @Unique
    private int musicTweaks$extraRowY = -1;

    public SoundOptionsScreenMixin(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    @Unique
    private static boolean musicTweaks$hasFreeSliderSlot() {
        return (SoundSource.values().length - 1) % 2 == 1;
    }

    @WrapOperation(method = "init", at = @At(value = "NEW", target = "net/minecraft/client/gui/components/Button"))
    private Button musicTweaks$moveDoneButton(int x, int y, int width, int height, Component message, Button.OnPress onPress, Operation<Button> original) {
        this.musicTweaks$extraRowY = y;
        int extraRows = musicTweaks$hasFreeSliderSlot() ? 1 : 2;
        return original.call(x, y + musicTweaks$ROW_HEIGHT * extraRows, width, height, message, onPress);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void musicTweaks$addMusicOptions(CallbackInfo ci) {
        this.musicTweaks$widgets.clear();
        if (this.musicTweaks$extraRowY == -1) return;

        ModConfig config = ModConfig.get();
        int left = this.width / 2 - 155;
        int right = this.width / 2 + 5;
        int row = this.musicTweaks$extraRowY;

        OptionInstance<Integer> jukeboxDistance = new OptionInstance<>(
                "config.music_tweaks.jukebox_distance",
                OptionInstance.cachedConstantTooltip(Component.translatable("config.music_tweaks.jukebox_distance.tooltip")),
                Options::genericValueLabel,
                new OptionInstance.IntRange(0, 256),
                (int) config.jukeboxDistance,
                value -> config.jukeboxDistance = value
        );

        OptionInstance<MusicFrequency> musicFrequency = new OptionInstance<>(
                "config.music_tweaks.music_frequency",
                OptionInstance.cachedConstantTooltip(Component.translatable("config.music_tweaks.music_frequency.tooltip")),
                (caption, value) -> value.getDisplayName(),
                new OptionInstance.Enum<>(List.of(MusicFrequency.values()), MusicFrequency.CODEC),
                config.musicFrequency,
                value -> config.musicFrequency = value
        );

        OptionInstance<Boolean> showMusicToast = OptionInstance.createBoolean(
                "config.music_tweaks.show_music_toast",
                OptionInstance.cachedConstantTooltip(Component.translatable("config.music_tweaks.show_music_toast.tooltip")),
                config.showMusicToast,
                value -> config.showMusicToast = value
        );

        if (musicTweaks$hasFreeSliderSlot()) {
            int sliderIndex = 2 + SoundSource.values().length - 1;
            int sliderY = this.height / 6 - 12 + musicTweaks$ROW_HEIGHT * (sliderIndex >> 1);
            musicTweaks$add(jukeboxDistance.createButton(this.options, right, sliderY, 150));
            musicTweaks$add(musicFrequency.createButton(this.options, left, row, 150));
            musicTweaks$add(showMusicToast.createButton(this.options, right, row, 150));
        } else {
            musicTweaks$add(jukeboxDistance.createButton(this.options, left, row, 310));
            musicTweaks$add(musicFrequency.createButton(this.options, left, row + musicTweaks$ROW_HEIGHT, 150));
            musicTweaks$add(showMusicToast.createButton(this.options, right, row + musicTweaks$ROW_HEIGHT, 150));
        }
    }

    @Unique
    private void musicTweaks$add(AbstractWidget widget) {
        this.musicTweaks$widgets.add(widget);
        this.addRenderableWidget(widget);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void musicTweaks$renderTooltips(PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        for (AbstractWidget widget : this.musicTweaks$widgets) {
            if (widget.isMouseOver(mouseX, mouseY) && widget instanceof TooltipAccessor tooltipAccessor) {
                this.renderTooltip(poseStack, tooltipAccessor.getTooltip(), mouseX, mouseY);
                return;
            }
        }
    }

    @Override
    public void removed() {
        super.removed();
        ModConfig.save();
    }
}
