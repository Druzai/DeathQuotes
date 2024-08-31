package com.cazsius.deathquotes.utils;

import com.cazsius.deathquotes.config.Settings;
import com.cazsius.deathquotes.impl.LimitedSet;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cazsius.deathquotes.utils.Constants.*;

public final class Funcs {
    private static String[] quotes = null;
    private static LimitedSet<Integer> quotesSet;
    private static final Random randomGenerator = new Random();
    private static State state = State.IDLE;
    private static InputStream assetInputStream = null;

    public static void setAssetInputStream(final InputStream assetInputStream) {
        Funcs.assetInputStream = assetInputStream;
    }

    public static State getState() {
        return state;
    }

    public static boolean copyQuotesToConfig() {
        Optional<InputStream> optionalInputStream = getQuotesFileDirFromJar();
        if (!optionalInputStream.isPresent()) {
            return false;
        }
        InputStream sourceInputStream = optionalInputStream.get();
        Path targetDirectory = Paths.get(quotesPathAndFileName);
        try {
            Files.copy(sourceInputStream, targetDirectory, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.error("Couldn't copy the file \"{}\" from jar to \"config\" folder!", quotesFileName);
            return false;
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

    public static Optional<InputStream> getQuotesFileDirFromJar() {
        if (assetInputStream != null) {
            return Optional.of(assetInputStream);
        } else {
            Logger.error("Couldn't find the file \"{}\" in jar!", quotesFileName);
            return Optional.empty();
        }
    }

    public static boolean isBlank(String string) {
        return string.isEmpty() ||
               string.chars().allMatch(c -> c == ' ' || c == '\t' || Character.isWhitespace(c));
    }

    public static boolean loadQuotes() {
        State previousState = state;
        state = State.LOADING_QUOTES;
        boolean encodingException = true;
        Path sourceDirectory = Paths.get(quotesPathAndFileName);
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
                quotes = lines.filter(s -> !isBlank(s)).map(String::trim).toArray(String[]::new);
                int percent = Settings.getNonRepeatablePercent();
                int quotesNumber;
                switch (percent) {
                    case 0: {
                        quotesNumber = 0;
                        break;
                    }
                    case 100: {
                        quotesNumber = quotes.length;
                        break;
                    }
                    default: {
                        quotesNumber = (int) Math.ceil((double) quotes.length / 100 * percent);
                        break;
                    }
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
                "Couldn't read quotes the file \"{}\" from {}{}!",
                quotesFileName,
                "\"config\" folder",
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

    public static String handleQuote(String quote, String playerName) {
        // Replace player name string if needed
        String replaceString = Settings.getPlayerNameReplaceString();
        if (!isBlank(replaceString) && quote.contains(replaceString)) {
            quote = quote.replace(replaceString, playerName);
        }
        // Replace next line string if needed
        replaceString = Settings.getNextLineReplaceString();
        if (!isBlank(replaceString) && quote.contains(replaceString)) {
            quote = quote.replace(replaceString, "\n");
            if (Settings.getEnableTrimmingBeforeAndAfterNextLine()) {
                quote = quote.replaceAll("[ \t]*\n[ \t]*", "\n");
            }
        }
        // Add quotation marks if needed
        if (Settings.getEnableQuotationMarks()) {
            quote = String.format("\"%s\"", quote);
        }
        return quote;
    }
}
