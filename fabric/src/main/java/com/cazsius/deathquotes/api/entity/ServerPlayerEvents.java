package com.cazsius.deathquotes.api.entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public final class ServerPlayerEvents {
    /**
     * An event that is called when server player dies and before death message is printed.
     */
    public static final Event<OnDeath> AFTER_DEATH = EventFactory.createArrayBacked(OnDeath.class, callbacks -> (serverPlayer, damageSource, gameRuleShowDeathMessages) -> {
        for (OnDeath callback : callbacks) {
            callback.afterDeath(serverPlayer, damageSource, gameRuleShowDeathMessages);
        }
    });

    @FunctionalInterface
    public interface OnDeath {
        /**
         * Called when server player dies and before death message is printed.
         */
        void afterDeath(ServerPlayer serverPlayer, DamageSource damageSource, boolean gameRuleShowDeathMessages);
    }

    private ServerPlayerEvents() {
    }
}
