package dev.erudites.mods.keyfilter.client;

import dev.erudites.mods.keyfilter.client.config.KeyFilterConfig;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public final class KeyFilterClientMod {

    public static final String MODID = "keyfilter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    private KeyFilterClientMod() {}

    public static void initializeConfig(Path configDir) {
        KeyFilterConfig.initialize(configDir);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
