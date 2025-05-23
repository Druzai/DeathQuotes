package com.cazsius.deathquotes.utils;

import com.cazsius.deathquotes.config.Settings;
import com.cazsius.deathquotes.impl.LimitedSet;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cazsius.deathquotes.utils.Constants.*;

public final class Funcs {
    private static String[] quotes = null;
    private static LimitedSet<Integer> quotesSet;
    private static final XoroshiroRandomSource randomGenerator = new XoroshiroRandomSource(
            3447679086515839964L ^ System.nanoTime()
    ); // 3447679086515839964L = 8682522807148012L * 1181783497276652981L
    private static State state = State.IDLE;

    public static State getState() {
        return state;
    }

    public static boolean copyQuotesToConfig() {
        Optional<InputStream> optionalInputStream = getQuotesFileInputStreamFromJar();
        if (optionalInputStream.isEmpty()) {
            return false;
        }
        Path targetDirectory = Paths.get(quotesPathAndFileName);
        try {
            Files.copy(optionalInputStream.get(), targetDirectory, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.error("Couldn't copy the file \"{}\" from jar to \"config\" folder!", quotesFileName);
            return false;
        }
        try {
            optionalInputStream.get().close();
        } catch (IOException ex) {
            Logger.error(ex.getMessage());
        }
        return true;
    }

    public static boolean quotesFileExistsInConfig() {
        return fileExists(quotesPathAndFileName);
    }

    public static boolean configDirExists() {
        return folderExists(quotesConfigPath);
    }

    public static boolean fileExists(String filePathAndName) {
        File fh = new File(filePathAndName);
        return fh.exists() && !fh.isDirectory();
    }

    public static boolean folderExists(String folderPath) {
        File fh = new File(folderPath);
        return fh.exists() && fh.isDirectory();
    }

    public static boolean createConfigDir() {
        try {
            Files.createDirectories(Paths.get(quotesConfigPath));
            return true;
        } catch (Exception ex) {
            Logger.error("Couldn't create \"config\" folder in root directory!");
            return false;
        }
    }

    private static Optional<InputStream> getQuotesFileInputStreamFromJar() {
        try {
            InputStream is = Funcs.class.getClassLoader().getResourceAsStream(quotesAssetPathAndFileName);
            if (is == null)
                throw new IOException("Couldn't find asset \"" + quotesFileName + "\"");
            else
                return Optional.of(is);
        } catch (Exception ex) {
            Logger.error("Couldn't find the file \"{}\" in jar!", quotesFileName);
            return Optional.empty();
        }
    }

    public static boolean loadQuotes() {
        State previousState = state;
        state = State.LOADING_QUOTES;
        Path sourceDirectory = Paths.get(quotesPathAndFileName);
        boolean encodingException = true;
        List<Charset> charsets = new ArrayList<>();
        charsets.add(StandardCharsets.UTF_8);
        if (!Charset.defaultCharset().equals(StandardCharsets.UTF_8)) {
            charsets.add(Charset.defaultCharset());
        }
        try {
            // Getting old default encoding for files created on old versions of Windows (Windows 7 and earlier)
            // And as a compatibility with old versions of "deathquotes" mod, where file "deathquotes.txt" was encoded using ANSI
            String oldSystemEncoding = System.getProperty("sun.jnu.encoding");
            Charset oldSystemCharset = Charset.forName(oldSystemEncoding);
            if (!oldSystemCharset.equals(StandardCharsets.UTF_8)) {
                charsets.add(oldSystemCharset);
            }
        } catch (SecurityException | IllegalArgumentException ex) {
            Logger.warn("Couldn't get encoding value from jvm property \"sun.jnu.encoding\"!", ex);
        }
        charsets.add(StandardCharsets.UTF_16);
        for (Charset charset : charsets) {
            try (Stream<String> lines = Files.lines(sourceDirectory, charset)) {
                quotes = lines.filter(s -> !s.isBlank()).map(String::trim).toArray(String[]::new);
                int percent = Settings.getNonRepeatablePercent();
                int quotesNumber;
                switch (percent) {
                    case 0 -> quotesNumber = 0;
                    case 100 -> quotesNumber = quotes.length;
                    default -> quotesNumber = (int) Math.ceil((double) quotes.length / 100 * percent);
                }
                if (quotesNumber >= quotes.length) {
                    quotesNumber = quotes.length - 1;
                }
                quotesSet = new LimitedSet<>(quotesNumber, Settings.getClearListOfNonRepeatableQuotes());
            } catch (UncheckedIOException ex) {
                continue;
            } catch (IOException ex) {
                encodingException = false;
                break;
            }
            state = previousState;
            // Status - Ready
            Logger.info("Loaded death quotes!");
            Logger.info("Death quotes count - {}", Funcs.getQuotesLength());
            return true;
        }
        state = previousState;
        Logger.error(
                "Couldn't read quotes the file \"{}\" from \"config\" folder{}!",
                quotesFileName,
                encodingException ?
                        String.format(
                                " because encoding wasn't in the list of supported encodings: %s",
                                charsets.stream().map(Charset::displayName).collect(Collectors.joining(", "))
                        )
                        : ""
        );
        Logger.error("Death quotes won't work because there is no quotes available!");
        Logger.error(
                "You can delete the file {} and restart Minecraft for default quotes! " +
                "Or edit that file and reload it in the game with command \"/deathquotes reloadQuotes\"!",
                quotesFileName
        );
        return false;
    }

    public static void handlePlayerDeath(Player player) {
        // If no quotes in the array
        if (Funcs.getQuotesLength() == 0) {
            Logger.error(
                    "The file {} contains no quotes. Delete it and restart for default quotes. " +
                    "Or edit that file and reload it in the game with command \"/deathquotes reloadQuotes\"!",
                    quotesFileName
            );
            player.sendSystemMessage(Component.literal("The file " + quotesFileName + " contains no quotes. Check Minecraft logs!"));
            return;
        }
        // Getting quote
        String quote = Funcs.getRandomQuote();
        // Generating "tellraw" component for quote
        quote = Funcs.handleQuote(quote, player);
        Component tellrawComponent = Funcs.generateTellrawComponentForQuote(quote);
        // Send quote only to players
        for (ServerPlayer serverPlayer : player.getServer().getPlayerList().getPlayers()) {
            serverPlayer.sendSystemMessage(tellrawComponent);
        }
    }

    public static int getQuotesLength() {
        return (quotes == null) ? 0 : quotes.length;
    }

    public static String getRandomQuote() {
        if (quotesSet.getSize() > 0) {
            final int maxIterations = quotesSet.getSize() * 2;
            int randomNumber;
            for (int i = 0; i < maxIterations; i++) {
                randomNumber = randomGenerator.nextInt(Funcs.getQuotesLength());
                if (quotesSet.contains(randomNumber)) continue;
                quotesSet.add(randomNumber);
                return quotes[randomNumber];
            }
            Logger.warn(
                    "Searched for the fresh unrepeated quote for too long (more than {} tries)! Getting a random one...",
                    maxIterations
            );
        }
        return quotes[randomGenerator.nextInt(Funcs.getQuotesLength())];
    }

    public static String handleQuote(String quote, Player player) {
        // Replace player name string if needed
        String replaceString = Settings.getPlayerNameReplaceString();
        if (!replaceString.isBlank() && quote.contains(replaceString)) {
            quote = quote.replace(replaceString, player.getGameProfile().getName());
        }
        // Replace next line string if needed
        replaceString = Settings.getNextLineReplaceString();
        if (!replaceString.isBlank() && quote.contains(replaceString)) {
            quote = quote.replace(replaceString, "\n");
            if (Settings.getEnableTrimmingBeforeAndAfterNextLine()) {
                quote = quote.replaceAll("\\s*\\n\\s*", "\n");
            }
        }
        // Add quotation marks if needed
        if (Settings.getEnableQuotationMarks()) {
            quote = String.format("\"%s\"", quote);
        }
        return quote;
    }

    public static MutableComponent generateTellrawComponentForQuote(String quote) {
        MutableComponent tellrawComponent = Component.empty();
        final boolean enableItalics = Settings.getEnableItalics();
        // Add clickable links and/or italics if needed
        if (Settings.getEnableHttpLinkProcessing() && httpLinkPattern.matcher(quote).find()) {
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
