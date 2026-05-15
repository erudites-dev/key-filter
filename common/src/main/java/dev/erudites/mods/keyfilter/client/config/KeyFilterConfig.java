package dev.erudites.mods.keyfilter.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.erudites.mods.keyfilter.client.KeyFilterClientMod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public final class KeyFilterConfig {

    private static final String FILE_NAME = "keyfilter.json";
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    private static KeyFilterConfig instance;
    private static Path configFile;

    public final Set<String> lockedKeys = new HashSet<>();
    public final Set<String> hiddenKeys = new HashSet<>();
    public final Set<String> disabledKeys = new HashSet<>();

    private KeyFilterConfig() {}

    public static void initialize(Path configDir) {
        configFile = configDir.resolve(FILE_NAME);
        if (Files.exists(configFile)) {
            try {
                KeyFilterConfig loaded = GSON.fromJson(Files.readString(configFile), KeyFilterConfig.class);
                instance = loaded != null ? loaded : new KeyFilterConfig();
            } catch (IOException e) {
                KeyFilterClientMod.LOGGER.warn("Failed to load config '{}', using defaults", FILE_NAME, e);
                instance = new KeyFilterConfig();
            }
        } else {
            instance = new KeyFilterConfig();
            save();
        }
    }

    public static KeyFilterConfig get() {
        if (instance == null) {
            instance = new KeyFilterConfig();
        }
        return instance;
    }

    public static void save() {
        if (configFile == null) {
            return;
        }
        try {
            Files.createDirectories(configFile.getParent());
            Files.writeString(configFile, GSON.toJson(instance));
        } catch (IOException e) {
            KeyFilterClientMod.LOGGER.warn("Failed to save config '{}'", FILE_NAME, e);
        }
    }
}
