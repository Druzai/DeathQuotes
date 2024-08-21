package com.cazsius.deathquotes.event;

import com.cazsius.deathquotes.commands.QuotesCommands;
import com.cazsius.deathquotes.config.Settings;
import com.cazsius.deathquotes.utils.Constants;
import com.cazsius.deathquotes.utils.Funcs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@Mod.EventBusSubscriber(modid = Constants.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEventListener {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        QuotesCommands.register(event.getDispatcher());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        // Run only on dedicated or integrated server
        if (event.getEntity().getServer() == null) {
            return;
        }
        // Check if event was cancelled by other mod
        if (event.isCanceled()) {
            return;
        }
        // For players only
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        // Check gamerule "showDeathMessages" and associated config parameter
        if (!Settings.getShowDeathQuotesRegardlessOfGameRule() && !player.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
            return;
        }
        Funcs.handlePlayerDeath(player);
    }
}
