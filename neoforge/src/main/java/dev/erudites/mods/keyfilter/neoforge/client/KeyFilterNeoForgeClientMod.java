package dev.erudites.mods.keyfilter.neoforge.client;

import dev.erudites.mods.keyfilter.client.KeyFilterClientMod;
import dev.erudites.mods.keyfilter.client.gui.FilteredKeysConfigScreen;
import dev.erudites.mods.keyfilter.client.input.KeyFilterKeyHandler;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = KeyFilterClientMod.MODID, dist = Dist.CLIENT)
public class KeyFilterNeoForgeClientMod {

    public KeyFilterNeoForgeClientMod(IEventBus modEventBus, ModContainer modContainer) {
        KeyFilterClientMod.initializeConfig(FMLPaths.CONFIGDIR.get());
        modContainer.registerExtensionPoint(
            IConfigScreenFactory.class,
            (_, lastScreen) -> new FilteredKeysConfigScreen(lastScreen)
        );
        modEventBus.addListener(this::onRegisterKeyMappings);
        NeoForge.EVENT_BUS.addListener(this::onClientTickPost);
    }

    private void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        KeyFilterKeyHandler.register(event::register);
    }

    private void onClientTickPost(ClientTickEvent.Post event) {
        KeyFilterKeyHandler.tick(Minecraft.getInstance());
    }
}