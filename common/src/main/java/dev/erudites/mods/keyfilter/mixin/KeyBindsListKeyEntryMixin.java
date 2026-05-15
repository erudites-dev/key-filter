package dev.erudites.mods.keyfilter.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.erudites.mods.keyfilter.client.config.KeyFilterConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBindsList.KeyEntry.class)
abstract class KeyBindsListKeyEntryMixin {

    @Shadow @Final
    private KeyMapping key;
    @Shadow @Final
    private Button changeButton;
    @Shadow @Final
    private Button resetButton;

    @ModifyExpressionValue(
        method = "refreshEntry",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/KeyMapping;same(Lnet/minecraft/client/KeyMapping;)Z"
        )
    )
    private boolean keyfilter$suppressConflictHighlight(boolean original) {
        return false;
    }

    @Inject(method = "refreshEntry", at = @At("TAIL"))
    private void keyfilter$lockEntry(CallbackInfo ci) {
        if (KeyFilterConfig.get().lockedKeys.contains(this.key.getName())) {
            this.changeButton.active = false;
            this.resetButton.active = false;
        }
    }
}
