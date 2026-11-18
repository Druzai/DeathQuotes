package com.cazsius.deathquotes.config;

import com.cazsius.deathquotes.utils.Logger;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.serde.ObjectDeserializer;
import com.electronwill.nightconfig.core.serde.ObjectSerializer;
import com.electronwill.nightconfig.core.serde.annotations.SerdeComment;

public class CommonConfig {
    private static ConfigSpec spec;
    private static final ObjectDeserializer deserializer = ObjectDeserializer.standard();
    private static final ObjectSerializer serializer = ObjectSerializer.standard();

    public static void updateChanges(CommentedFileConfig commentedFileConfig, boolean updateConfig) {
        ConfigData configData;
        boolean toSave;
        if (commentedFileConfig.isEmpty()) {
            configData = new ConfigData();
            toSave = true;
        } else {
            try {
                toSave = correct(commentedFileConfig) > 0;
                configData = deserializer.deserializeFields(commentedFileConfig, ConfigData::new);
            } catch (Exception ex) {
                Logger.error("Couldn't convert config \"{}\" to object! Falling back to using default values!", commentedFileConfig.getFile().getName(), ex);
                configData = new ConfigData();
                toSave = true;
            }
        }
        configData.pushChanges();
        if (updateConfig || toSave) {
            serializer.serializeFields(configData, commentedFileConfig);
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

    private static class ConfigData {
        private final DeathQuotes deathQuotes = new DeathQuotes();

        public void pushChanges() {
            MainOptions mainOptions = deathQuotes.mainOptions;
            FormattingOptions formattingOptions = deathQuotes.formattingOptions;

            Settings.setShowDeathQuotesRegardlessOfGameRule(mainOptions.showDeathQuotesRegardlessOfGameRule);
            Settings.setNonRepeatablePercent(mainOptions.nonRepeatablePercent);
            Settings.setClearListOfNonRepeatableQuotes(mainOptions.clearListOfNonRepeatableQuotes);
            Settings.setEnableQuotationMarks(formattingOptions.enableQuotationMarks);
            Settings.setEnableItalics(formattingOptions.enableItalics);
            Settings.setEnableHttpLinkProcessing(formattingOptions.enableHttpLinkProcessing);
            Settings.setPlayerNameReplaceString(mainOptions.playerNameReplaceString);
            Settings.setNextLineReplaceString(mainOptions.nextLineReplaceString);
            Settings.setEnableTrimmingBeforeAndAfterNextLine(mainOptions.enableTrimmingBeforeAndAfterNextLine);
        }
    }

    private static class DeathQuotes {
        private final MainOptions mainOptions = new MainOptions();
        private final FormattingOptions formattingOptions = new FormattingOptions();
    }

    private static class MainOptions {
        @SerdeComment(ConfigComments.showDeathQuotesRegardlessOfGameRule)
        private boolean showDeathQuotesRegardlessOfGameRule = true;
        @SerdeComment(ConfigComments.nonRepeatablePercent)
        private int nonRepeatablePercent = 5;
        @SerdeComment(ConfigComments.clearListOfNonRepeatableQuotes)
        private boolean clearListOfNonRepeatableQuotes = false;
        @SerdeComment(ConfigComments.playerNameReplaceString)
        private String playerNameReplaceString = "${{player_name}}";
        @SerdeComment(ConfigComments.nextLineReplaceString)
        private String nextLineReplaceString = "${{next_line}}";
        @SerdeComment(ConfigComments.enableTrimmingBeforeAndAfterNextLine)
        private boolean enableTrimmingBeforeAndAfterNextLine = false;
    }

    private static class FormattingOptions {
        @SerdeComment(ConfigComments.enableQuotationMarks)
        private boolean enableQuotationMarks = true;
        @SerdeComment(ConfigComments.enableItalics)
        private boolean enableItalics = false;
        @SerdeComment(ConfigComments.enableHttpLinkProcessing)
        private boolean enableHttpLinkProcessing = false;
    }
}
