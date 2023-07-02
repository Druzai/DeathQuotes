package com.cazsius.deathquotes.event;

import com.cazsius.deathquotes.config.Settings;
import com.cazsius.deathquotes.utils.Constants;
import com.cazsius.deathquotes.utils.Funcs;
import com.cazsius.deathquotes.utils.Logger;
import com.cazsius.deathquotes.utils.State;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.cazsius.deathquotes.utils.Constants.*;

@Mod.EventBusSubscriber(modid = Constants.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEventListener {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        final LiteralArgumentBuilder<CommandSource> literalArgumentBuilder = Commands.literal(Constants.ID)
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2));

        literalArgumentBuilder.then(
                Commands.literal("reloadQuotes")
                        .executes(commandContext -> {
                            if (Funcs.getState() == State.LOADING_QUOTES) {
                                commandContext.getSource().sendSuccess(
                                        new StringTextComponent("Already reloading death quotes!"),
                                        true
                                );
                            } else {
                                boolean done = Funcs.loadQuotes(false);
                                commandContext.getSource().sendSuccess(
                                        new StringTextComponent(
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

        event.getDispatcher().register(literalArgumentBuilder);
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
        if (!(event.getEntity() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) event.getEntity();
        // Check gamerule "showDeathMessages" and associated config parameter
        if (!Settings.getShowDeathQuotesRegardlessOfGameRule() && !player.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
            return;
        }
        // If no quotes in the array
        if (Funcs.getQuotesLength() == 0) {
            Logger.error(
                    "The file {} contains no quotes. Delete it and restart for default quotes. " +
                    "Or edit that file and reload it in the game with command \"/deathquotes reloadQuotes\"!",
                    quotesFileName
            );
            player.sendMessage(new StringTextComponent("The file " + quotesFileName + " contains no quotes. Check Minecraft logs!"), Util.NIL_UUID);
            return;
        }
        // Getting quote
        String quote = Funcs.getRandomQuote();
        // Generating "tellraw" component for quote
        quote = Funcs.handleQuote(quote, player.getGameProfile().getName());
        TextComponent tellrawComponent = generateTellrawComponentForQuote(quote);
        // Send quote only to players
        for (ServerPlayerEntity serverPlayer : player.getServer().getPlayerList().getPlayers()) {
            serverPlayer.sendMessage(tellrawComponent, ChatType.CHAT, Util.NIL_UUID);
        }
    }

    private static TextComponent generateTellrawComponentForQuote(String quote) {
        TextComponent tellrawComponent = new StringTextComponent("");
        final boolean enableItalics = Settings.getEnableItalics();
        // Add clickable links and/or italics if needed
        if (Settings.getEnableHttpLinkProcessing() && httpLinkPattern.matcher(quote).find()) {
            List<TextComponent> textInBetween = Arrays
                    .stream(quote.split(httpLinkPattern.pattern()))
                    .map(string -> {
                        TextComponent textComponent = new StringTextComponent(string);
                        if (enableItalics) {
                            textComponent.withStyle(TextFormatting.ITALIC);
                        }
                        return textComponent;
                    })
                    .collect(Collectors.toList());
            Matcher matcher = httpLinkPattern.matcher(quote);
            for (TextComponent component : textInBetween) {
                tellrawComponent.append(component);
                if (matcher.find()) {
                    IFormattableTextComponent mutableComponent = getUrlLinkComponent(matcher.group("link"));
                    if (enableItalics) {
                        mutableComponent.withStyle(TextFormatting.ITALIC);
                    }
                    tellrawComponent.append(mutableComponent);
                }
            }
        } else {
            TextComponent textComponent = new StringTextComponent(quote);
            if (enableItalics) {
                textComponent.withStyle(TextFormatting.ITALIC);
            }
            tellrawComponent.append(textComponent);
        }
        return tellrawComponent;
    }

    private static IFormattableTextComponent getUrlLinkComponent(String link) {
        return new StringTextComponent(link)
                .setStyle(Style.EMPTY
                        .applyFormat(TextFormatting.BLUE)
                        .applyFormat(TextFormatting.UNDERLINE)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link)));
    }
}
