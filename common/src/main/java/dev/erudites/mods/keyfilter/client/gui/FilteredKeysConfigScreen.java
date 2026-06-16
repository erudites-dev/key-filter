package dev.erudites.mods.keyfilter.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.erudites.mods.keyfilter.client.config.KeyFilterConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public class FilteredKeysConfigScreen extends Screen {

    private static final int FOOTER_BTN_WIDTH = 100;
    private static final int FOOTER_BTN_SPACING = 8;

    private final Screen lastScreen;
    public final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
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
        this.layout.addTitleHeader(this.title, this.font);
        this.list = this.layout.addToContents(new HiddenKeysList(this.minecraft, this));
        LinearLayout footer = this.layout.addToFooter(LinearLayout.horizontal().spacing(FOOTER_BTN_SPACING));
        this.resetLockedButton = footer.addChild(Button.builder(
            Component.translatable("gui.keyfilter.button.reset_locked"),
            _ -> this.resetSet(KeyFilterConfig.get().lockedKeys)
        ).width(FOOTER_BTN_WIDTH).build());
        this.resetHiddenButton = footer.addChild(Button.builder(
            Component.translatable("gui.keyfilter.button.reset_hidden"),
            _ -> this.resetSet(KeyFilterConfig.get().hiddenKeys)
        ).width(FOOTER_BTN_WIDTH).build());
        this.resetDisabledButton = footer.addChild(Button.builder(
            Component.translatable("gui.keyfilter.button.reset_disabled"),
            _ -> this.resetSet(KeyFilterConfig.get().disabledKeys)
        ).width(FOOTER_BTN_WIDTH).build());
        footer.addChild(Button.builder(
            CommonComponents.GUI_DONE,
            _ -> this.minecraft.gui.setScreen(this.lastScreen)
        ).width(FOOTER_BTN_WIDTH).build());
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    private void resetSet(Set<String> set) {
        set.clear();
        KeyFilterConfig.save();
        this.minecraft.gui.setScreen(new FilteredKeysConfigScreen(this.lastScreen));
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.list != null) {
            this.list.updateSize(this.width, this.layout);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        KeyFilterConfig config = KeyFilterConfig.get();
        this.resetLockedButton.active = !config.lockedKeys.isEmpty();
        this.resetHiddenButton.active = !config.hiddenKeys.isEmpty();
        this.resetDisabledButton.active = !config.disabledKeys.isEmpty();
        super.extractRenderState(graphics, mouseX, mouseY, a);
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
