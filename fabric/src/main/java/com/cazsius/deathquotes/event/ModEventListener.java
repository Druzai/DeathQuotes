package com.cazsius.deathquotes.event;

import com.cazsius.deathquotes.config.ConfigFileHandler;
import com.cazsius.deathquotes.config.Settings;
import com.cazsius.deathquotes.utils.Constants;
import com.cazsius.deathquotes.utils.Funcs;
import com.cazsius.deathquotes.utils.Logger;
import com.cazsius.deathquotes.utils.State;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.cazsius.deathquotes.utils.Constants.httpLinkPattern;
import static com.cazsius.deathquotes.utils.Constants.quotesFileName;

public class ModEventListener {
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
        final LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands.literal(Constants.ID)
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2));

        literalArgumentBuilder.then(
                Commands.literal("reloadQuotes")
                        .executes(commandContext -> {
                            if (Funcs.getState() == State.LOADING_QUOTES) {
                                commandContext.getSource().sendSuccess(
                                        new TextComponent("Already reloading death quotes!"),
                                        true
                                );
                            } else {
                                boolean done = Funcs.loadQuotes(false);
                                commandContext.getSource().sendSuccess(
                                        new TextComponent(
                                                done
                                                        ? "Reloaded death quotes!"
                                                        : "Failed to reload death quotes! Check Minecraft logs!"
                                        ),
                                        true
                                );
                            }
                            return Command.SINGLE_SUCCESS;
                        })
        );

        dispatcher.register(literalArgumentBuilder);
    }

    public static void beforeShutdown() {
        Logger.debug("Unloading config and config listeners");
        ConfigFileHandler.unload();
    }

    public static void onServerPlayerDeath(
            ServerPlayer player,
            DamageSource damageSource,
            boolean gameRuleShowDeathMessages
    ) {
        // Check gamerule "showDeathMessages" and associated config parameter
        if (!Settings.getShowDeathQuotesRegardlessOfGameRule() && !gameRuleShowDeathMessages) {
            return;
        }
        // If no quotes in the array
        if (Funcs.getQuotesLength() == 0) {
            Logger.error(
                    "The file {} contains no quotes. Delete it and restart for default quotes. " +
                    "Or edit that file and reload it in the game with command \"/deathquotes reloadQuotes\"!",
                    quotesFileName
            );
            player.sendMessage(new TextComponent("The file " + quotesFileName + " contains no quotes. Check Minecraft logs!"), Util.NIL_UUID);
            return;
        }
        // Getting quote
        String quote = Funcs.getRandomQuote();
        // Generating "tellraw" component for quote
        quote = Funcs.handleQuote(quote, player.getGameProfile().getName());
        TextComponent tellrawComponent = generateTellrawComponentForQuote(quote);
        // Send quote only to players
        for (ServerPlayer serverPlayer : player.getServer().getPlayerList().getPlayers()) {
            serverPlayer.sendMessage(tellrawComponent, ChatType.CHAT, Util.NIL_UUID);
        }
    }

    private static TextComponent generateTellrawComponentForQuote(String quote) {
        TextComponent tellrawComponent = new TextComponent("");
        final boolean enableItalics = Settings.getEnableItalics();
        // Add clickable links and/or italics if needed
        if (Settings.getEnableHttpLinkProcessing() && httpLinkPattern.matcher(quote).find()) {
            List<TextComponent> textInBetween = Arrays
                    .stream(quote.split(httpLinkPattern.pattern()))
                    .map(string -> {
                        TextComponent textComponent = new TextComponent(string);
                        if (enableItalics) {
                            textComponent.withStyle(ChatFormatting.ITALIC);
                        }
                        return textComponent;
                    })
                    .collect(Collectors.toList());
            Matcher matcher = httpLinkPattern.matcher(quote);
            for (TextComponent component : textInBetween) {
                tellrawComponent.append(component);
                if (matcher.find()) {
                    MutableComponent mutableComponent = getUrlLinkComponent(matcher.group("link"));
                    if (enableItalics) {
                        mutableComponent.withStyle(ChatFormatting.ITALIC);
                    }
                    tellrawComponent.append(mutableComponent);
                }
            }
        } else {
            TextComponent textComponent = new TextComponent(quote);
            if (enableItalics) {
                textComponent.withStyle(ChatFormatting.ITALIC);
            }
            tellrawComponent.append(textComponent);
        }
        return tellrawComponent;
    }

    private static MutableComponent getUrlLinkComponent(String link) {
        return new TextComponent(link)
                .setStyle(Style.EMPTY
                        .applyFormat(ChatFormatting.BLUE)
                        .applyFormat(ChatFormatting.UNDERLINE)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link)));
    }
}
