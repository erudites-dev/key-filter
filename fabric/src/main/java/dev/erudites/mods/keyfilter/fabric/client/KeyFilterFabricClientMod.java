package dev.erudites.mods.keyfilter.fabric.client;

import dev.erudites.mods.keyfilter.client.KeyFilterClientMod;
import dev.erudites.mods.keyfilter.client.input.KeyFilterKeyHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;

public class KeyFilterFabricClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KeyFilterClientMod.initializeConfig(FabricLoader.getInstance().getConfigDir());
        KeyFilterKeyHandler.register(KeyMappingHelper::registerKeyMapping);
        ClientTickEvents.END_CLIENT_TICK.register(KeyFilterKeyHandler::tick);
    }
}
