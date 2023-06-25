package com.cazsius.deathquotes.config;

import com.cazsius.deathquotes.utils.Logger;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommonConfig {
    public static void updateChanges(CommentedFileConfig commentedFileConfig, boolean updateConfig) {
        ConfigData configData;
        boolean boundsChanged = false;
        boolean setDefaultValues = false;
        if (commentedFileConfig.isEmpty()) {
            configData = new ConfigData();
        } else {
            try {
                configData = new ObjectConverter().toObject(commentedFileConfig, ConfigData::new);
                boundsChanged = configData.checkBounds() != 0;
            } catch (Exception ex) {
                Logger.error("Couldn't convert config \"{}\" to object! Falling back to using default values!", commentedFileConfig.getFile().getName(), ex);
                configData = new ConfigData();
                setDefaultValues = true;
            }
        }
        configData.pushChanges();
        if (!updateConfig || boundsChanged || setDefaultValues) {
            convertConfigData(commentedFileConfig, configData);
            commentedFileConfig.save();
            Logger.debug("Saved TOML config file {}", commentedFileConfig.getFile().getPath());
        }
    }

    public static boolean correct(CommentedFileConfig commentedFileConfig) {
        // TODO: add handling...
        return true;
    }

    public static void convertConfigData(CommentedConfig commentedFileConfig, Object configClass) {
        for (Field f : configClass.getClass().getDeclaredFields()) {
            Object value;
            try {
                value = f.get(configClass);
            } catch (IllegalAccessException e) {
                Logger.error("Couldn't access field \"{}\" of object \"{}\"! Skipping...", f.getName(), CommonConfig.class.getName());
                continue;
            }
            List<String> path = getPath(f);
            ConfigFormat<?> format = commentedFileConfig.configFormat();
            if (!f.isAnnotationPresent(Path.class) || !format.supportsType(value.getClass())) {
                CommentedConfig converted = commentedFileConfig.createSubConfig();
                convertConfigData(converted, value);
                commentedFileConfig.set(path, converted);
            } else {
                commentedFileConfig.set(path, value);
                ConfigComments
                        .getStringByName(f.getName())
                        .ifPresent(s -> commentedFileConfig.setComment(path, s));
            }
        }
    }

    private static List<String> getPath(Field field) {
        Path fieldPath = field.getDeclaredAnnotation(Path.class);
        if (fieldPath != null) {
            return Arrays.stream(fieldPath.value().split("\\.")).toList();
        }
        return Collections.singletonList(field.getName());
    }

    private static class ConfigData {
        @Path("deathQuotes.mainOptions.nonRepeatablePercent")
        private int nonRepeatablePercent = 5;
        @Path("deathQuotes.mainOptions.clearListOfNonRepeatableQuotes")
        private boolean clearListOfNonRepeatableQuotes = false;
        @Path("deathQuotes.mainOptions.playerNameReplaceString")
        private String playerNameReplaceString = "${{player_name}}";
        @Path("deathQuotes.mainOptions.nextLineReplaceString")
        private String nextLineReplaceString = "${{next_line}}";
        @Path("deathQuotes.mainOptions.enableTrimmingBeforeAndAfterNextLine")
        private boolean enableTrimmingBeforeAndAfterNextLine = false;
        @Path("deathQuotes.formattingOptions.enableQuotationMarks")
        private boolean enableQuotationMarks = true;
        @Path("deathQuotes.formattingOptions.enableItalics")
        private boolean enableItalics = false;
        @Path("deathQuotes.formattingOptions.enableHttpLinkProcessing")
        private boolean enableHttpLinkProcessing = false;

        public int checkBounds() {
            int changes = 0;
            if (nonRepeatablePercent < 0 || nonRepeatablePercent > 100) {
                nonRepeatablePercent = 5;
                changes++;
            }
            return changes;
        }

        public void pushChanges() {
            Settings.setNonRepeatablePercent(nonRepeatablePercent);
            Settings.setClearListOfNonRepeatableQuotes(clearListOfNonRepeatableQuotes);
            Settings.setEnableQuotationMarks(enableQuotationMarks);
            Settings.setEnableItalics(enableItalics);
            Settings.setEnableHttpLinkProcessing(enableHttpLinkProcessing);
            Settings.setPlayerNameReplaceString(playerNameReplaceString);
            Settings.setNextLineReplaceString(nextLineReplaceString);
            Settings.setEnableTrimmingBeforeAndAfterNextLine(enableTrimmingBeforeAndAfterNextLine);
        }
    }
}
