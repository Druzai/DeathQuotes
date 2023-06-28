package com.cazsius.deathquotes.mixin.application;

import com.cazsius.deathquotes.api.application.ApplicationEvents;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DedicatedServer.class)
public class MinecraftServerMixin {
    @Inject(at = @At("HEAD"), method = "stopServer")
    private void stop(CallbackInfo ci) {
        ApplicationEvents.BEFORE_SHUTDOWN.invoker().beforeShutdown();
    }
}
