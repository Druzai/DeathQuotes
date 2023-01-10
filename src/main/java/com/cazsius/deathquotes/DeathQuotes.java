package com.cazsius.deathquotes;

import com.cazsius.deathquotes.config.CommonConfig;
import com.cazsius.deathquotes.utils.Constants;
import com.cazsius.deathquotes.utils.Funcs;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Constants.ID)
public class DeathQuotes {
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogManager.getLogger();
    public static final CommonConfig COMMON_CONFIG = new CommonConfig();

    public DeathQuotes() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG.getSpec());
    }

    private void setup(final FMLCommonSetupEvent event) {
        // If no quotes file in the config folder - create the default from the one in the jar file assets folder
        boolean readFromJar = false;
        if (!Funcs.quotesFileExistsInConfig()) {
            readFromJar = !Funcs.copyQuotesToConfig();
        }
        // End of creation of the default quotes file

        // Load the quotes file into an array for use
        Funcs.loadQuotes(readFromJar);
    }
}
