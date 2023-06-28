package com.cazsius.deathquotes.commands;

import com.cazsius.deathquotes.utils.Constants;
import com.cazsius.deathquotes.utils.Funcs;
import com.cazsius.deathquotes.utils.State;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class QuotesCommands {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        final LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands.literal(Constants.ID)
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2));

        literalArgumentBuilder.then(
                Commands.literal("reloadQuotes")
                        .executes(commandContext -> {
                            if (Funcs.getState() == State.LOADING_QUOTES) {
                                commandContext.getSource().sendSuccess(
                                        Component.literal("Already reloading death quotes!"),
                                        true
                                );
                            } else {
                                boolean done = Funcs.loadQuotes(false);
                                commandContext.getSource().sendSuccess(
                                        Component.literal(
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

        commandDispatcher.register(literalArgumentBuilder);
    }
}
