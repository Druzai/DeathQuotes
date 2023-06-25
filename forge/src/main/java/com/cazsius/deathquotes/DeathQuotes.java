package com.cazsius.deathquotes;

import com.cazsius.deathquotes.config.CommonConfig;
import com.cazsius.deathquotes.utils.Constants;
import com.cazsius.deathquotes.utils.Funcs;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.ID)
public class DeathQuotes {
    public DeathQuotes() {
        // Register the setup method for mod loading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

        CommonConfig.build();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.getSpec());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
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
}
