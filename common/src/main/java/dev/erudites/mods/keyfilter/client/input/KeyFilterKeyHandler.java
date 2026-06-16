package dev.erudites.mods.keyfilter.client.input;

import dev.erudites.mods.keyfilter.client.KeyFilterClientMod;
import dev.erudites.mods.keyfilter.client.gui.FilteredKeysConfigScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public final class KeyFilterKeyHandler {

    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
        KeyFilterClientMod.id(KeyFilterClientMod.MODID)
    );
    public static final KeyMapping CONFIG_KEY_MAPPING = new KeyMapping(
        "key.keyfilter.open_config",
        GLFW.GLFW_KEY_F8,
        CATEGORY
    );

    private KeyFilterKeyHandler() {}

    public static void register(Consumer<KeyMapping> keyMappingRegistrar) {
        keyMappingRegistrar.accept(CONFIG_KEY_MAPPING);
    }

    public static void tick(Minecraft minecraft) {
        while (CONFIG_KEY_MAPPING.consumeClick()) {
            if (minecraft.gui.screen() == null) {
                minecraft.gui.setScreen(new FilteredKeysConfigScreen(null));
            }
        }
    }
}
