package com.cazsius.deathquotes.mixin.application;

import com.cazsius.deathquotes.api.application.ApplicationEvents;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class ClientMinecraftMixin {
    @Inject(at = @At("HEAD"), method = "destroy")
    private void stop(CallbackInfo ci) {
        ApplicationEvents.BEFORE_SHUTDOWN.invoker().beforeShutdown();
    }
}
