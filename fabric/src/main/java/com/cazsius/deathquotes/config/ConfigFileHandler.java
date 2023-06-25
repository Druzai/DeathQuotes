package com.cazsius.deathquotes.config;

import com.cazsius.deathquotes.utils.Logger;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.cazsius.deathquotes.utils.Constants.ID;
import static com.cazsius.deathquotes.utils.Constants.quotesConfigPath;

public class ConfigFileHandler {
    private static final String configFileName = String.format("%s.toml", ID);
    private static final Path configPath = Path.of(quotesConfigPath).resolve(configFileName);
    private static CommentedFileConfig configData;

    public static void readConfig() {
        configData = CommentedFileConfig
                .builder(configPath)
                .sync()
                .preserveInsertionOrder()
                .autosave()
                .onFileNotFound(ConfigFileHandler::setupConfigFile)
                .writingMode(WritingMode.REPLACE)
                .build();
        Logger.debug("Built TOML config for {}", configPath.toString());
        try {
            configData.load();
            CommonConfig.updateChanges(configData, configPath, false);
        } catch (ParsingException ex) {
            throw new RuntimeException("Couldn't load config file", ex);
        }
        Logger.debug("Loaded TOML config file {}", configPath.toString());
        try {
            FileWatcher.defaultInstance().addWatch(configPath, new ConfigWatcher(configData, Thread.currentThread().getContextClassLoader()));
            Logger.debug("Watching TOML config file {} for changes", configPath.toString());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't watch config file", e);
        }
    }

    public static void unload() {
        try {
            FileWatcher.defaultInstance().removeWatch(configPath);
        } catch (RuntimeException e) {
            Logger.error("Failed to remove config {} from file watcher!", configPath.toString(), e);
        }
        if (configData != null) {
            configData.close();
        }
    }

    private static boolean setupConfigFile(final Path file, final ConfigFormat<?> conf) throws IOException {
        Files.createDirectories(file.getParent());
        if (Files.exists(configPath)) {
            Logger.info("Loading default config file from path {}", configPath.toString());
            Files.copy(configPath, file);
        } else {
            Files.createFile(file);
            conf.initEmptyFile(file);
        }
        return true;
    }

    private static class ConfigWatcher implements Runnable {
        private final CommentedFileConfig commentedFileConfig;
        private final ClassLoader realClassLoader;

        ConfigWatcher(final CommentedFileConfig commentedFileConfig, final ClassLoader classLoader) {
            this.commentedFileConfig = commentedFileConfig;
            this.realClassLoader = classLoader;
        }

        @Override
        public void run() {
            // Force the regular classloader onto the special thread
            Thread.currentThread().setContextClassLoader(realClassLoader);
            try {
                commentedFileConfig.load();
            } catch (ParsingException ex) {
                throw new RuntimeException("Couldn't load config file", ex);
            }
            Logger.debug("Config file {} changed, updating values", configPath.toString());
            CommonConfig.updateChanges(commentedFileConfig, configPath, true);
        }
    }
}
