package dev.erudites.mods.keyfilter.fabric.client.integrations;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.erudites.mods.keyfilter.client.gui.FilteredKeysConfigScreen;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return FilteredKeysConfigScreen::new;
    }
}
