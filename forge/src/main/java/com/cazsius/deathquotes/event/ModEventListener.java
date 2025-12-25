package com.cazsius.deathquotes.event;

import com.cazsius.deathquotes.commands.QuotesCommands;
import com.cazsius.deathquotes.config.Settings;
import com.cazsius.deathquotes.utils.Constants;
import com.cazsius.deathquotes.utils.Funcs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.listener.Priority;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEventListener {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        QuotesCommands.register(event.getDispatcher());
    }

    @SubscribeEvent(priority = Priority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        // Run only on dedicated or integrated server
        if (event.getEntity().level().getServer() == null) {
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
