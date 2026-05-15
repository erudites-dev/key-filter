package dev.erudites.mods.keyfilter.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.erudites.mods.keyfilter.client.config.KeyFilterConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class FilteredKeysConfigScreen extends Screen {

    private final Screen lastScreen;
    private HiddenKeysList list;
    private Button resetLockedButton;
    private Button resetHiddenButton;
    private Button resetDisabledButton;

    @Nullable
    public KeyMapping selectedKey;

    public FilteredKeysConfigScreen(Screen lastScreen) {
        super(Component.translatable("gui.keyfilter.config.title"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        this.list = new HiddenKeysList(this.minecraft, this);
        this.addRenderableWidget(this.list);
        int buttonY = this.height - 29;
        int buttonWidth = 100;
        int gap = 5;
        int totalWidth = buttonWidth * 4 + gap * 3;
        int startX = this.width / 2 - totalWidth / 2;
        this.resetLockedButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.keyfilter.button.reset_locked"), _ -> {
            KeyFilterConfig.get().lockedKeys.clear();
            KeyFilterConfig.save();
            this.minecraft.setScreen(new FilteredKeysConfigScreen(this.lastScreen));
        }).bounds(startX, buttonY, buttonWidth, 20).build());
        this.resetHiddenButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.keyfilter.button.reset_hidden"), _ -> {
            KeyFilterConfig.get().hiddenKeys.clear();
            KeyFilterConfig.save();
            this.minecraft.setScreen(new FilteredKeysConfigScreen(this.lastScreen));
        }).bounds(startX + buttonWidth + gap, buttonY, buttonWidth, 20).build());
        this.resetDisabledButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.keyfilter.button.reset_disabled"), _ -> {
            KeyFilterConfig.get().disabledKeys.clear();
            KeyFilterConfig.save();
            this.minecraft.setScreen(new FilteredKeysConfigScreen(this.lastScreen));
        }).bounds(startX + (buttonWidth + gap) * 2, buttonY, buttonWidth, 20)
            .build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, _ -> this.minecraft.setScreen(this.lastScreen))
            .bounds(startX + (buttonWidth + gap) * 3, buttonY, buttonWidth, 20)
            .build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        KeyFilterConfig config = KeyFilterConfig.get();
        this.resetLockedButton.active = !config.lockedKeys.isEmpty();
        this.resetHiddenButton.active = !config.hiddenKeys.isEmpty();
        this.resetDisabledButton.active = !config.disabledKeys.isEmpty();
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.centeredText(this.font, this.title, this.width / 2, 12, -1);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.selectedKey != null) {
            if (event.isEscape()) {
                this.selectedKey.setKey(InputConstants.UNKNOWN);
            } else {
                this.selectedKey.setKey(InputConstants.getKey(event));
            }
            this.selectedKey = null;
            KeyMapping.resetMapping();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.selectedKey != null) {
            this.selectedKey.setKey(InputConstants.Type.MOUSE.getOrCreate(event.button()));
            this.selectedKey = null;
            KeyMapping.resetMapping();
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }
}
