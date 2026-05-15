package dev.erudites.mods.keyfilter.client.gui;

import dev.erudites.mods.keyfilter.client.config.KeyFilterConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class HiddenKeysList extends ContainerObjectSelectionList<HiddenKeysList.Entry> {

    private static final int LINE_HEIGHT = 9;
    private static final int TOGGLE_BTN_WIDTH = 50;
    private static final int CHANGE_BTN_WIDTH = 75;
    private static final int BTN_GAP = 5;
    private static final int SCROLLBAR_PADDING = 10;

    private static final int TEXT_COLOR_DEFAULT = 0xFFFFFFFF;
    private static final int TEXT_COLOR_DISABLED = 0xFFFF5555;
    private static final int TEXT_COLOR_HIDDEN = 0xFF808080;
    private static final int TEXT_COLOR_LOCKED = 0xFFFFAA00;

    private static final double MARQUEE_INITIAL_PAUSE = 0.2;
    private static final double MARQUEE_END_PAUSE = 0.5;
    private static final double MARQUEE_SCROLL_SPEED = 30.0;

    private final FilteredKeysConfigScreen screen;

    public HiddenKeysList(Minecraft minecraft, FilteredKeysConfigScreen screen) {
        super(minecraft, screen.width, screen.height - 65, 33, 20);
        this.screen = screen;
        KeyMapping[] keyMappings = ArrayUtils.clone(minecraft.options.keyMappings);
        Arrays.sort(keyMappings);
        KeyMapping.Category currentCategory = null;
        for (KeyMapping keyMapping : keyMappings) {
            KeyMapping.Category category = keyMapping.getCategory();
            if (category != currentCategory) {
                currentCategory = category;
                this.addEntry(new CategoryEntry(category.label()));
            }
            this.addEntry(new KeyEntry(keyMapping));
        }
    }

    @Override
    public int getRowWidth() {
        return 340;
    }

    @Override
    protected int scrollBarX() {
        return this.width / 2 + 170 + 10;
    }

    @FunctionalInterface
    private interface StateMessage {
        Component of(boolean active);
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {}

    class CategoryEntry extends Entry {
        private final FocusableTextWidget categoryName;

        CategoryEntry(Component name) {
            this.categoryName = FocusableTextWidget.builder(name, HiddenKeysList.this.minecraft.font)
                .alwaysShowBorder(false)
                .backgroundFill(FocusableTextWidget.BackgroundFill.ON_FOCUS)
                .build();
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            this.categoryName.setPosition(
                HiddenKeysList.this.width / 2 - this.categoryName.getWidth() / 2,
                this.getContentBottom() - this.categoryName.getHeight()
            );
            this.categoryName.extractRenderState(graphics, mouseX, mouseY, a);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(this.categoryName);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(this.categoryName);
        }
    }

    class KeyEntry extends Entry {
        private final KeyMapping key;
        private final Button hideButton;
        private final Button disableButton;
        private final Button lockButton;
        private final Button changeButton;

        private boolean wasHoveringText = false;
        private long hoverStartTime = 0;

        KeyEntry(KeyMapping key) {
            this.key = key;
            this.hideButton = this.createToggleButton(() -> KeyFilterConfig.get().hiddenKeys, KeyEntry::hideMessage);
            this.disableButton = this.createToggleButton(() -> KeyFilterConfig.get().disabledKeys, KeyEntry::disableMessage);
            this.lockButton = this.createToggleButton(() -> KeyFilterConfig.get().lockedKeys, KeyEntry::lockMessage);
            this.changeButton = Button.builder(
                this.key.getTranslatedKeyMessage(),
                _ -> HiddenKeysList.this.screen.selectedKey = this.key
            ).bounds(0, 0, CHANGE_BTN_WIDTH, 20).build();
        }

        private Button createToggleButton(Supplier<Set<String>> setSupplier, StateMessage messageFn) {
            String name = this.key.getName();
            return Button.builder(messageFn.of(setSupplier.get().contains(name)), button -> {
                Set<String> set = setSupplier.get();
                if (set.contains(name)) {
                    set.remove(name);
                } else {
                    set.add(name);
                }
                button.setMessage(messageFn.of(set.contains(name)));
                KeyFilterConfig.save();
            }).bounds(0, 0, TOGGLE_BTN_WIDTH, 20).build();
        }

        private static Component hideMessage(boolean hidden) {
            return hidden
                ? Component.translatable("gui.keyfilter.button.show").withStyle(ChatFormatting.GRAY)
                : Component.translatable("gui.keyfilter.button.hide");
        }

        private static Component disableMessage(boolean disabled) {
            return disabled
                ? Component.translatable("gui.keyfilter.button.enable").withStyle(ChatFormatting.RED)
                : Component.translatable("gui.keyfilter.button.disable");
        }

        private static Component lockMessage(boolean locked) {
            return locked
                ? Component.translatable("gui.keyfilter.button.unlock").withStyle(ChatFormatting.GOLD)
                : Component.translatable("gui.keyfilter.button.lock");
        }

        private boolean isHidden() {
            return KeyFilterConfig.get().hiddenKeys.contains(this.key.getName());
        }

        private boolean isDisabled() {
            return KeyFilterConfig.get().disabledKeys.contains(this.key.getName());
        }

        private boolean isLocked() {
            return KeyFilterConfig.get().lockedKeys.contains(this.key.getName());
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            this.positionButtons();
            int textMaxWidth = this.changeButton.getX() - this.getContentX() - BTN_GAP * 2;
            this.drawKeyName(graphics, mouseX, mouseY, textMaxWidth);
            this.updateChangeButtonMessage();
            this.renderButtons(graphics, mouseX, mouseY, a);
        }

        private void positionButtons() {
            int disableBtnX = HiddenKeysList.this.scrollBarX() - this.disableButton.getWidth() - SCROLLBAR_PADDING;
            int hideBtnX = disableBtnX - this.hideButton.getWidth() - BTN_GAP;
            int lockBtnX = hideBtnX - this.lockButton.getWidth() - BTN_GAP;
            int changeBtnX = lockBtnX - this.changeButton.getWidth() - BTN_GAP;
            int buttonY = this.getY() - 2;
            this.disableButton.setPosition(disableBtnX, buttonY);
            this.hideButton.setPosition(hideBtnX, buttonY);
            this.lockButton.setPosition(lockBtnX, buttonY);
            this.changeButton.setPosition(changeBtnX, buttonY);
        }

        private void drawKeyName(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int maxWidth) {
            MutableComponent displayName = Component.translatable(this.key.getName());
            int textColor = TEXT_COLOR_DEFAULT;
            if (this.isDisabled()) {
                displayName.withStyle(ChatFormatting.STRIKETHROUGH);
                textColor = TEXT_COLOR_DISABLED;
            } else if (this.isHidden()) {
                textColor = TEXT_COLOR_HIDDEN;
            } else if (this.isLocked()) {
                textColor = TEXT_COLOR_LOCKED;
            }
            int textWidth = HiddenKeysList.this.minecraft.font.width(displayName);
            int left = this.getContentX();
            int top = this.getY();
            int textY = top + this.getHeight() / 2 - LINE_HEIGHT / 2;
            if (textWidth <= maxWidth) {
                this.wasHoveringText = false;
                graphics.text(HiddenKeysList.this.minecraft.font, displayName, left, textY, textColor);
                return;
            }
            int bottom = top + this.getHeight();
            graphics.enableScissor(left, top, left + maxWidth, bottom);
            boolean isHoveringText = mouseX >= left && mouseX <= left + maxWidth && mouseY >= top && mouseY <= bottom;
            if (isHoveringText && !this.wasHoveringText) {
                this.hoverStartTime = Util.getMillis();
            }
            this.wasHoveringText = isHoveringText;
            int scrollOffset = isHoveringText ? this.computeMarqueeOffset(textWidth, maxWidth) : 0;
            graphics.text(HiddenKeysList.this.minecraft.font, displayName, left - scrollOffset, textY, textColor);
            graphics.disableScissor();
        }

        private int computeMarqueeOffset(int textWidth, int maxWidth) {
            double elapsedTime = (Util.getMillis() - this.hoverStartTime) / 1000.0;
            if (elapsedTime < MARQUEE_INITIAL_PAUSE) {
                return 0;
            }
            double scrollDistance = (double) textWidth - maxWidth;
            double scrollTime = scrollDistance / MARQUEE_SCROLL_SPEED;
            double cycleTime = (scrollTime + MARQUEE_END_PAUSE) * 2.0;
            double cyclePos = (elapsedTime - MARQUEE_INITIAL_PAUSE) % cycleTime;
            if (cyclePos < scrollTime) {
                return (int) (cyclePos * MARQUEE_SCROLL_SPEED);
            }
            if (cyclePos < scrollTime + MARQUEE_END_PAUSE) {
                return (int) scrollDistance;
            }
            if (cyclePos < scrollTime * 2 + MARQUEE_END_PAUSE) {
                return (int) (scrollDistance - (cyclePos - (scrollTime + MARQUEE_END_PAUSE)) * MARQUEE_SCROLL_SPEED);
            }
            return 0;
        }

        private void updateChangeButtonMessage() {
            MutableComponent btnMessage = this.key.getTranslatedKeyMessage().copy();
            if (HiddenKeysList.this.screen.selectedKey == this.key) {
                this.changeButton.setMessage(Component.literal("> ")
                    .append(btnMessage.withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE))
                    .append(" <").withStyle(ChatFormatting.YELLOW)
                );
            } else if (this.hasCollision()) {
                this.changeButton.setMessage(Component.literal("[ ")
                    .append(btnMessage.withStyle(ChatFormatting.WHITE))
                    .append(" ]").withStyle(ChatFormatting.RED)
                );
            } else {
                this.changeButton.setMessage(btnMessage);
            }
        }

        private boolean hasCollision() {
            if (this.key.isUnbound()) {
                return false;
            }
            for (KeyMapping mapping : HiddenKeysList.this.minecraft.options.keyMappings) {
                if (mapping != this.key && this.key.same(mapping)) {
                    return true;
                }
            }
            return false;
        }

        private void renderButtons(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            this.disableButton.extractRenderState(graphics, mouseX, mouseY, a);
            this.hideButton.extractRenderState(graphics, mouseX, mouseY, a);
            this.lockButton.extractRenderState(graphics, mouseX, mouseY, a);
            this.changeButton.extractRenderState(graphics, mouseX, mouseY, a);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(this.changeButton, this.lockButton, this.disableButton, this.hideButton);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(this.changeButton, this.lockButton, this.disableButton, this.hideButton);
        }
    }
}
