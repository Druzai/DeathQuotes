package com.cazsius.deathquotes;

import com.cazsius.deathquotes.config.CommonConfig;
import com.cazsius.deathquotes.utils.Constants;
import com.cazsius.deathquotes.utils.Funcs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(Constants.ID)
public class DeathQuotes {
    public DeathQuotes(IEventBus modEventBus, ModContainer modContainer) {
        // Register the setup method for mod loading
        modEventBus.addListener(this::commonSetup);

        CommonConfig.build();
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.getSpec());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
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
}
