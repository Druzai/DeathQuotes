package com.cazsius.deathquotes.config;

import com.cazsius.deathquotes.utils.Logger;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommonConfig {
    private static ConfigSpec spec;

    public static void updateChanges(CommentedFileConfig commentedFileConfig, boolean updateConfig) {
        ConfigData configData;
        boolean toSave;
        if (commentedFileConfig.isEmpty()) {
            configData = new ConfigData();
            toSave = true;
        } else {
            try {
                toSave = correct(commentedFileConfig) > 0;
                configData = new ObjectConverter().toObject(commentedFileConfig, ConfigData::new);
            } catch (Exception ex) {
                Logger.error("Couldn't convert config \"{}\" to object! Falling back to using default values!", commentedFileConfig.getFile().getName(), ex);
                configData = new ConfigData();
                toSave = true;
            }
        }
        configData.pushChanges();
        if (updateConfig || toSave) {
            convertConfigData(commentedFileConfig, configData);
            commentedFileConfig.save();
            Logger.debug("Saved TOML config file {}", commentedFileConfig.getFile().getPath());
        }
    }

    public static int correct(CommentedConfig commentedFileConfig) {
        return getConfigSpec().correct(commentedFileConfig);
    }

    private static ConfigSpec getConfigSpec() {
        if (spec == null) {
            spec = new ConfigSpec();
            spec.define("deathQuotes.mainOptions.showDeathQuotesRegardlessOfGameRule", true);
            spec.defineInRange("deathQuotes.mainOptions.nonRepeatablePercent", 5, 0, 100);
            spec.define("deathQuotes.mainOptions.clearListOfNonRepeatableQuotes", false);
            spec.define("deathQuotes.mainOptions.playerNameReplaceString", "${{player_name}}");
            spec.define("deathQuotes.mainOptions.nextLineReplaceString", "${{next_line}}");
            spec.define("deathQuotes.mainOptions.enableTrimmingBeforeAndAfterNextLine", false);
            spec.define("deathQuotes.formattingOptions.enableQuotationMarks", true);
            spec.define("deathQuotes.formattingOptions.enableItalics", false);
            spec.define("deathQuotes.formattingOptions.enableHttpLinkProcessing", false);
        }
        return spec;
    }

    public static void convertConfigData(CommentedConfig commentedFileConfig, Object configClass) {
        for (Field f : configClass.getClass().getDeclaredFields()) {
            Object value;
            try {
                value = f.get(configClass);
            } catch (IllegalAccessException e) {
                Logger.error("Couldn't access field \"{}\" of object \"{}\"! Skipping...", f.getName(), configClass.getClass().getName());
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
            return Arrays.stream(fieldPath.value().split("\\.")).collect(Collectors.toList());
        }
        return Collections.singletonList(field.getName());
    }

    private static class ConfigData {
        @Path("deathQuotes.mainOptions.showDeathQuotesRegardlessOfGameRule")
        public boolean showDeathQuotesRegardlessOfGameRule = true;
        @Path("deathQuotes.mainOptions.nonRepeatablePercent")
        public int nonRepeatablePercent = 5;
        @Path("deathQuotes.mainOptions.clearListOfNonRepeatableQuotes")
        public boolean clearListOfNonRepeatableQuotes = false;
        @Path("deathQuotes.mainOptions.playerNameReplaceString")
        public String playerNameReplaceString = "${{player_name}}";
        @Path("deathQuotes.mainOptions.nextLineReplaceString")
        public String nextLineReplaceString = "${{next_line}}";
        @Path("deathQuotes.mainOptions.enableTrimmingBeforeAndAfterNextLine")
        public boolean enableTrimmingBeforeAndAfterNextLine = false;
        @Path("deathQuotes.formattingOptions.enableQuotationMarks")
        public boolean enableQuotationMarks = true;
        @Path("deathQuotes.formattingOptions.enableItalics")
        public boolean enableItalics = false;
        @Path("deathQuotes.formattingOptions.enableHttpLinkProcessing")
        public boolean enableHttpLinkProcessing = false;

        public void pushChanges() {
            Settings.setShowDeathQuotesRegardlessOfGameRule(showDeathQuotesRegardlessOfGameRule);
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
