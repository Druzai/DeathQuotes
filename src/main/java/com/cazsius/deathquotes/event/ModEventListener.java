package com.cazsius.deathquotes.event;

import com.cazsius.deathquotes.commands.QuotesCommands;
import com.cazsius.deathquotes.utils.Constants;
import com.cazsius.deathquotes.utils.Funcs;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.cazsius.deathquotes.utils.Constants.quotesFileName;

@Mod.EventBusSubscriber(modid = Constants.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEventListener {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        QuotesCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void livingDeath(LivingDeathEvent event) {
        // Run only on dedicated or integrated server
        if (event.getEntity().getServer() == null) {
            return;
        }
        // For players only
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        // If no quotes in the array
        if (Funcs.getQuotesLength() == 0) {
            LOGGER.error("The file " + quotesFileName + " contains no quotes. Delete it and restart for default quotes.");
            player.sendSystemMessage(Component.literal("The file " + quotesFileName + " contains no quotes. Check Minecraft logs!"));
            return;
        }
        // Getting quote
        String quote = Funcs.getRandomQuote();
        // Generating "tellraw" component for quote
        quote = Funcs.handleQuote(quote, player);
        Component tellrawComponent = Funcs.generateTellrawComponentForQuote(quote);
        if (!event.isCanceled()) {
            // Send quote only to players
            for (ServerPlayer serverPlayer : player.getServer().getPlayerList().getPlayers()) {
                serverPlayer.sendSystemMessage(tellrawComponent);
            }
        }
    }
}
