package com.cazsius.deathquotes.event;

import com.cazsius.deathquotes.commands.QuotesCommands;
import com.cazsius.deathquotes.config.ConfigFileHandler;
import com.cazsius.deathquotes.config.Settings;
import com.cazsius.deathquotes.utils.Funcs;
import com.cazsius.deathquotes.utils.Logger;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class ModEventListener {
    public static void registerCommands(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext registryAccess,
            Commands.CommandSelection environment
    ) {
        QuotesCommands.register(dispatcher);
    }

    public static void beforeShutdown() {
        Logger.debug("Unloading config and config listeners");
        ConfigFileHandler.unload();
    }

    public static void onServerPlayerDeath(
            ServerPlayer serverPlayer,
            DamageSource damageSource,
            boolean gameRuleShowDeathMessages
    ) {
        // Check gamerule "showDeathMessages" and associated config parameter
        if (!Settings.getShowDeathQuotesRegardlessOfGameRule() && !gameRuleShowDeathMessages) {
            return;
        }
        Funcs.handlePlayerDeath(serverPlayer);
    }
}
