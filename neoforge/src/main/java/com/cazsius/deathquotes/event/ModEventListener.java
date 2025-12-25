package com.cazsius.deathquotes.event;

import com.cazsius.deathquotes.commands.QuotesCommands;
import com.cazsius.deathquotes.config.Settings;
import com.cazsius.deathquotes.utils.Constants;
import com.cazsius.deathquotes.utils.Funcs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = Constants.ID)
public class ModEventListener {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        QuotesCommands.register(event.getDispatcher());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        // Run only on dedicated or integrated server
        if (event.getEntity().level().getServer() == null) {
            return;
        }
        // Check if event was cancelled by other mod
        if (event.isCanceled()) {
            return;
        }
        // For players only
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        // Check gamerule "show_death_messages" and associated config parameter
        if (
                !Settings.getShowDeathQuotesRegardlessOfGameRule() &&
                !player.level().getGameRules().get(GameRules.SHOW_DEATH_MESSAGES)
        ) {
            return;
        }
        Funcs.handlePlayerDeath(player);
    }
}
