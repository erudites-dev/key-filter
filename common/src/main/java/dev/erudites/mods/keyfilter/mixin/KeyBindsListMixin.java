package dev.erudites.mods.keyfilter.mixin;

import dev.erudites.mods.keyfilter.client.config.KeyFilterConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Arrays;

@Mixin(KeyBindsList.class)
abstract class KeyBindsListMixin {

    @ModifyVariable(
        method = "<init>",
        at = @At(
            value = "STORE",
            ordinal = 0
        )
    )
    private KeyMapping[] keyfilter$filterHiddenKeyMappings(KeyMapping[] original) {
        return Arrays.stream(original)
            .filter(mapping -> !KeyFilterConfig.get().hiddenKeys.contains(mapping.getName()))
            .toArray(KeyMapping[]::new);
    }
}
