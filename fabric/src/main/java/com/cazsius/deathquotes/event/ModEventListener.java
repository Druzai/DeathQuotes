package com.cazsius.deathquotes.event;

import com.cazsius.deathquotes.commands.QuotesCommands;
import com.cazsius.deathquotes.config.ConfigFileHandler;
import com.cazsius.deathquotes.utils.Funcs;
import com.cazsius.deathquotes.utils.Logger;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ModEventListener {
    public static void registerCommands(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext registryAccess,
            Commands.CommandSelection environment
    ) {
        if (environment != Commands.CommandSelection.ALL) {
            QuotesCommands.register(dispatcher);
        }
    }

    public static void beforeShutdown() {
        Logger.debug("Unloading config and config listeners");
        ConfigFileHandler.unload();
    }

    public static void onLivingDeath(LivingEntity entity, DamageSource damageSource) {
        // Run only on dedicated or integrated server
        if (entity.getServer() == null) {
            return;
        }
        // For players only
        if (!(entity instanceof Player player)) {
            return;
        }
        Funcs.handlePlayerDeath(player);
    }
}
