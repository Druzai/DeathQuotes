package com.cazsius.deathquotes.utils;

import com.cazsius.deathquotes.DeathQuotes;
import com.cazsius.deathquotes.impl.LimitedSet;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import static com.cazsius.deathquotes.utils.Constants.*;

public class Funcs {
    private static final Logger LOGGER = LogManager.getLogger();
    private static String[] quotes = null;
    private static LimitedSet<Integer> quotesSet;
    private static final Random randomGenerator = new Random();
    private static State state = State.IDLE;

    public static State getState() {
        return state;
    }

    public static boolean copyQuotesToConfig() {
        Path sourceDirectory;
        try {
            URI uri = Objects.requireNonNull(Funcs.class.getClassLoader().getResource("assets/" + quotesFileName)).toURI();
            sourceDirectory = Paths.get(uri);
        } catch (Exception ex) {
            LOGGER.error("Couldn't find the file \"" + quotesFileName + "\" in jar!");
            return false;
        }
        Path targetDirectory = Paths.get(quotesPathAndFileName);
        try {
            Files.copy(sourceDirectory, targetDirectory, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            LOGGER.error("Couldn't copy the file \"" + quotesFileName + "\" from jar to \"config\" folder!");
            return false;
        }
        return true;
    }

    public static boolean quotesFileExistsInConfig() {
        return fileExists(quotesPathAndFileName);
    }

    public static boolean fileExists(String filePathAndName) {
        File fh = new File(filePathAndName);
        return fh.exists() && !fh.isDirectory();
    }

    public static boolean loadQuotes(boolean fromJar) {
        State previousState = state;
        state = State.LOADING_QUOTES;
        Path sourceDirectory;
        boolean encodingException = true;
        if (fromJar) {
            try {
                URI uri = Objects.requireNonNull(Funcs.class.getClassLoader().getResource("assets/" + quotesFileName)).toURI();
                sourceDirectory = Paths.get(uri);
            } catch (Exception ex) {
                LOGGER.error("Couldn't find the file \"" + quotesFileName + "\" in jar!");
                state = previousState;
                return false;
            }
        } else {
            sourceDirectory = Paths.get(quotesPathAndFileName);
        }
        List<Charset> charsets = List.of(StandardCharsets.UTF_8, StandardCharsets.US_ASCII, StandardCharsets.UTF_16);
        for (Charset charset : charsets) {
            try (Stream<String> lines = Files.lines(sourceDirectory, charset)) {
                quotes = lines.filter(s -> !s.isBlank()).map(String::trim).toArray(String[]::new);
                int percent = DeathQuotes.COMMON_CONFIG.getNonRepeatablePercent();
                int quotesNumber;
                switch (percent) {
                    case 0 -> quotesNumber = 0;
                    case 100 -> quotesNumber = quotes.length;
                    default -> quotesNumber = (int) Math.ceil((double) quotes.length / 100 * percent);
                }
                if (quotesNumber >= quotes.length){
                    quotesNumber = quotes.length - 1;
                }
                quotesSet = new LimitedSet<>(quotesNumber, DeathQuotes.COMMON_CONFIG.getClearListOfNonRepeatableQuotes());
            } catch (UncheckedIOException ex) {
                continue;
            } catch (IOException ex) {
                encodingException = false;
                break;
            }
            state = previousState;
            // Status - Ready
            LOGGER.info("Loaded death quotes!");
            LOGGER.info("Death quotes count - " + Funcs.getQuotesLength());
            return true;
        }
        state = previousState;
        LOGGER.error("Couldn't read quotes the file \"" + quotesFileName + "\" from " +
                (fromJar ? "jar" : "\"config\" folder") + (encodingException ? " because encoding wasn't \"UTF-8\"" : "") + "!");
        LOGGER.error("Death quotes won't work because there is no quotes available!");
        LOGGER.error("You can delete the file " + quotesFileName + " and restart Minecraft for default quotes! " +
                "Or edit that file and reload it in the game with command \"/deathquotes reloadQuotes\"!");
        return false;
    }

    public static int getQuotesLength() {
        return (quotes == null) ? 0 : quotes.length;
    }

    public static String getRandomQuote() {
        if (quotesSet.getSize() > 0) {
            final int maxIterations = quotesSet.getSize() * 2;
            for (int i = 0; i < maxIterations; i++) {
                int randomNumber = randomGenerator.nextInt(Funcs.getQuotesLength());
                if (quotesSet.contains(randomNumber)) continue;
                quotesSet.add(randomNumber);
                return quotes[randomNumber];
            }
            LOGGER.error(String.format("Searched for the fresh quote for too long (more than %s tries)!", maxIterations));
        }
        return quotes[randomGenerator.nextInt(Funcs.getQuotesLength())];
    }

    public static String handleQuote(String quote, Player player) {
        // Replace player name string if needed
        String replaceString = DeathQuotes.COMMON_CONFIG.getPlayerNameReplaceString();
        if (!replaceString.isBlank() && quote.contains(replaceString)) {
            quote = quote.replace(replaceString, player.getGameProfile().getName());
        }
        // Replace next line string if needed
        replaceString = DeathQuotes.COMMON_CONFIG.getNextLineReplaceString();
        if (!replaceString.isBlank() && quote.contains(replaceString)) {
            quote = quote.replace(replaceString, "\n");
            if (DeathQuotes.COMMON_CONFIG.getEnableTrimmingBeforeAndAfterNextLine()) {
                quote = quote.replaceAll("\s*\n\s*", "\n");
            }
        }
        // Add quotation marks if needed
        if (DeathQuotes.COMMON_CONFIG.getEnableQuotationMarks()) {
            quote = "\"" + quote + "\"";
        }
        return quote;
    }

    public static MutableComponent generateTellrawComponentForQuote(String quote) {
        MutableComponent tellrawComponent = Component.empty();
        final boolean enableItalics = DeathQuotes.COMMON_CONFIG.getEnableItalics();
        // Add clickable links and/or italics if needed
        if (DeathQuotes.COMMON_CONFIG.getEnableHttpLinkProcessing() && httpLinkPattern.matcher(quote).find()) {
            List<MutableComponent> textInBetween = Arrays
                    .stream(quote.split(httpLinkPattern.pattern()))
                    .map(string -> {
                        MutableComponent textComponent = Component.literal(string);
                        if (enableItalics) {
                            textComponent.withStyle(ChatFormatting.ITALIC);
                        }
                        return textComponent;
                    })
                    .toList();
            Matcher matcher = httpLinkPattern.matcher(quote);
            for (MutableComponent component : textInBetween) {
                tellrawComponent.append(component);
                if (matcher.find()) {
                    MutableComponent mutableComponent = Funcs.getUrlLinkComponent(matcher.group("link"));
                    if (enableItalics) {
                        mutableComponent.withStyle(ChatFormatting.ITALIC);
                    }
                    tellrawComponent.append(mutableComponent);
                }
            }
        } else {
            MutableComponent textComponent = Component.literal(quote);
            if (enableItalics) {
                textComponent.withStyle(ChatFormatting.ITALIC);
            }
            tellrawComponent.append(textComponent);
        }
        return tellrawComponent;
    }

    public static MutableComponent getUrlLinkComponent(String link) {
        return Component.literal(link)
                .setStyle(Style.EMPTY
                        .applyFormat(ChatFormatting.BLUE)
                        .applyFormat(ChatFormatting.UNDERLINE)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link)));
    }
}
