package com.cazsius.deathquotes.api.application;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ApplicationEvents {
    /**
     * An event that is called when client or server shuts down.
     */
    public static final Event<OnShutdown> BEFORE_SHUTDOWN = EventFactory.createArrayBacked(OnShutdown.class, callbacks -> () -> {
        for (OnShutdown callback : callbacks) {
            callback.beforeShutdown();
        }
    });

    @FunctionalInterface
    public interface OnShutdown {
        /**
         * Called when client or server shuts down.
         */
        void beforeShutdown();
    }

    private ApplicationEvents() {
    }
}
