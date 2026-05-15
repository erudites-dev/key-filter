package dev.erudites.mods.keyfilter.mixin;

import dev.erudites.mods.keyfilter.client.config.KeyFilterConfig;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
abstract class KeyMappingMixin {

    @Inject(method = "isDown", at = @At("HEAD"), cancellable = true)
    private void keyfilter$preventDisabledKeyIsDown(CallbackInfoReturnable<Boolean> cir) {
        KeyMapping self = (KeyMapping) (Object) this;
        if (KeyFilterConfig.get().disabledKeys.contains(self.getName())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "consumeClick", at = @At("HEAD"), cancellable = true)
    private void keyfilter$preventDisabledKeyConsumeClick(CallbackInfoReturnable<Boolean> cir) {
        KeyMapping self = (KeyMapping) (Object) this;
        if (KeyFilterConfig.get().disabledKeys.contains(self.getName())) {
            cir.setReturnValue(false);
        }
    }
}
