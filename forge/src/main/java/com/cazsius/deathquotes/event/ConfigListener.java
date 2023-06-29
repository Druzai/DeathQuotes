package com.cazsius.deathquotes.event;

import com.cazsius.deathquotes.config.CommonConfig;
import com.cazsius.deathquotes.utils.Constants;
import com.cazsius.deathquotes.utils.Logger;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = Constants.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigListener {
    @SubscribeEvent
    public static void onLoad(ModConfig.Loading configEvent) {
        Logger.debug("Loaded {} config file {}", Constants.ID, configEvent.getConfig().getFileName());
        CommonConfig.pushChanges();
    }

    @SubscribeEvent
    public static void onFileChange(ModConfig.Reloading configEvent) {
        Logger.debug("Reloaded {} config file {}", Constants.ID, configEvent.getConfig().getFileName());
        CommonConfig.pushChanges();
    }
}
