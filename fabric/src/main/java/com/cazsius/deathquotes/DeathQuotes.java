package com.cazsius.deathquotes;

import com.cazsius.deathquotes.api.application.ApplicationEvents;
import com.cazsius.deathquotes.api.entity.ServerPlayerEvents;
import com.cazsius.deathquotes.config.ConfigFileHandler;
import com.cazsius.deathquotes.event.ModEventListener;
import com.cazsius.deathquotes.utils.Funcs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import static com.cazsius.deathquotes.utils.Constants.quotesAssetPathAndFileName;

public class DeathQuotes implements ModInitializer {
    @Override
    public void onInitialize() {
        ConfigFileHandler.readConfig();
        commonSetup();
        registerEvents();
    }

    private void commonSetup() {
        // Get asset InputStream from main class
        Funcs.setAssetInputStream(getClass().getClassLoader().getResourceAsStream(quotesAssetPathAndFileName));
        // If config folder doesn't exist in root directory - create one
        if (!Funcs.configDirExists()) {
            Funcs.createConfigDir();
        }
        // If no quotes file in the config folder - create the default from the one in the jar file assets folder
        if (!Funcs.quotesFileExistsInConfig()) {
            Funcs.copyQuotesToConfig();
        }
        // End of creation of the default quotes file

        // Load the quotes file into an array for use
        Funcs.loadQuotes();
    }

    private void registerEvents() {
        CommandRegistrationCallback.EVENT.register(ModEventListener::registerCommands);
        ServerPlayerEvents.AFTER_DEATH.register(ModEventListener::onServerPlayerDeath);
        ApplicationEvents.BEFORE_SHUTDOWN.register(ModEventListener::beforeShutdown);
    }
}
