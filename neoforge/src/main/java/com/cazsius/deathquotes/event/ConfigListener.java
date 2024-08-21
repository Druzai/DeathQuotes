package com.cazsius.deathquotes.event;

import com.cazsius.deathquotes.config.CommonConfig;
import com.cazsius.deathquotes.utils.Constants;
import com.cazsius.deathquotes.utils.Logger;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = Constants.ID, bus = EventBusSubscriber.Bus.MOD)
public class ConfigListener {
    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading configEvent) {
        Logger.debug("Loaded {} config file {}", Constants.ID, configEvent.getConfig().getFileName());
        CommonConfig.pushChanges();
    }

    @SubscribeEvent
    public static void onFileChange(ModConfigEvent.Reloading configEvent) {
        Logger.debug("Reloaded {} config file {}", Constants.ID, configEvent.getConfig().getFileName());
        CommonConfig.pushChanges();
    }
}
