package com.cazsius.deathquotes;

import com.cazsius.deathquotes.api.application.ApplicationEvents;
import com.cazsius.deathquotes.api.entity.ServerPlayerEvents;
import com.cazsius.deathquotes.config.ConfigFileHandler;
import com.cazsius.deathquotes.event.ModEventListener;
import com.cazsius.deathquotes.utils.Funcs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class DeathQuotes implements ModInitializer {
    @Override
    public void onInitialize() {
        ConfigFileHandler.readConfig();
        commonSetup();
        registerEvents();
    }

    private void commonSetup() {
        // If config folder doesn't exist in root directory - create one
        if (!Funcs.configDirExists()) {
            Funcs.createConfigDir();
        }
        // If no quotes file in the config folder - create the default from the one in the jar file assets folder
        boolean readFromJar = false;
        if (!Funcs.quotesFileExistsInConfig()) {
            readFromJar = !Funcs.copyQuotesToConfig();
        }
        // End of creation of the default quotes file

        // Load the quotes file into an array for use
        Funcs.loadQuotes(readFromJar);
    }

    private void registerEvents() {
        CommandRegistrationCallback.EVENT.register(ModEventListener::registerCommands);
        ServerPlayerEvents.AFTER_DEATH.register(ModEventListener::onServerPlayerDeath);
        ApplicationEvents.BEFORE_SHUTDOWN.register(ModEventListener::beforeShutdown);
    }
}
