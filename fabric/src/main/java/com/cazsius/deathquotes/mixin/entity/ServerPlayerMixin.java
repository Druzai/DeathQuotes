package com.cazsius.deathquotes.mixin.entity;

import com.cazsius.deathquotes.api.entity.ServerPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V"))
    public void onDeathWithMessage(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEvents.AFTER_DEATH.invoker().afterDeath((ServerPlayer) (Object) this, damageSource, true);
    }

    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
    public void onDeathWithoutMessage(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEvents.AFTER_DEATH.invoker().afterDeath((ServerPlayer) (Object) this, damageSource, false);
    }
}
