package com.cazsius.deathquotes.config;

import com.cazsius.deathquotes.utils.Logger;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.lang.reflect.Field;
import java.nio.file.Path;

public class CommonConfig {
    public static void updateChanges(CommentedFileConfig commentedFileConfig, Path configPath, boolean updateConfig) {
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
            Logger.debug("Saved TOML config file {}", configPath.toString());
        }
    }

    public static boolean correct(CommentedFileConfig commentedFileConfig) {
        // TODO: add handling...
        return true;
    }

    public static void convertConfigData(CommentedFileConfig commentedFileConfig, ConfigData configData) {
        Field[] fields = configData.getClass().getDeclaredFields();
        for (Field f : fields) {
            Object value;
            try {
                value = f.get(configData);
            } catch (IllegalAccessException e) {
                Logger.error("Couldn't access field \"{}\" of object \"{}\"! Skipping...", f.getName(), CommonConfig.class.getName());
                continue;
            }
            commentedFileConfig.set(f.getName(), value);
            ConfigComments
                    .getStringByName(f.getName())
                    .ifPresent(s -> commentedFileConfig.setComment(f.getName(), s));
        }
    }

    private static class ConfigData {
        private int nonRepeatablePercent = 5;
        private boolean clearListOfNonRepeatableQuotes = false;
        private String playerNameReplaceString = "${{player_name}}";
        private String nextLineReplaceString = "${{next_line}}";
        private boolean enableTrimmingBeforeAndAfterNextLine = false;
        private boolean enableQuotationMarks = true;
        private boolean enableItalics = false;
        private boolean enableHttpLinkProcessing = false;

        public int checkBounds() {
            if (nonRepeatablePercent < 0 || nonRepeatablePercent > 100) {
                nonRepeatablePercent = 5;
                return 1;
            }
            return 0;
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
