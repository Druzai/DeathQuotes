package com.cazsius.deathquotes.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CommonConfig {
    private static final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
    private static ModConfigSpec spec;
    private static ModConfigSpec.BooleanValue showDeathQuotesRegardlessOfGameRule;
    private static ModConfigSpec.IntValue nonRepeatablePercent;
    private static ModConfigSpec.BooleanValue clearListOfNonRepeatableQuotes;
    private static ModConfigSpec.BooleanValue enableQuotationMarks;
    private static ModConfigSpec.BooleanValue enableItalics;
    private static ModConfigSpec.BooleanValue enableHttpLinkProcessing;
    private static ModConfigSpec.ConfigValue<String> playerNameReplaceString;
    private static ModConfigSpec.ConfigValue<String> nextLineReplaceString;
    private static ModConfigSpec.BooleanValue enableTrimmingBeforeAndAfterNextLine;

    public static void build() {
        builder.push("deathQuotes");
        setMainOptions();
        setFormattingOptions();
        builder.pop();

        spec = builder.build();
    }

    private static void setMainOptions() {
        builder.push("mainOptions");
        showDeathQuotesRegardlessOfGameRule = builder
                .comment(ConfigComments.showDeathQuotesRegardlessOfGameRule)
                .define("showDeathQuotesRegardlessOfGameRule", true);
        nonRepeatablePercent = builder
                .comment(ConfigComments.nonRepeatablePercent)
                .defineInRange("nonRepeatablePercent", 5, 0, 100);
        clearListOfNonRepeatableQuotes = builder
                .comment(ConfigComments.clearListOfNonRepeatableQuotes)
                .define("clearListOfNonRepeatableQuotes", false);
        playerNameReplaceString = builder
                .comment(ConfigComments.playerNameReplaceString)
                .define("playerNameReplaceString", "${{player_name}}");
        nextLineReplaceString = builder
                .comment(ConfigComments.nextLineReplaceString)
                .define("nextLineReplaceString", "${{next_line}}");
        enableTrimmingBeforeAndAfterNextLine = builder
                .comment(ConfigComments.enableTrimmingBeforeAndAfterNextLine)
                .define("enableTrimmingBeforeAndAfterNextLine", false);
        builder.pop();
    }

    private static void setFormattingOptions() {
        builder.push("formattingOptions");
        enableQuotationMarks = builder
                .comment(ConfigComments.enableQuotationMarks)
                .define("enableQuotationMarks", true);
        enableItalics = builder
                .comment(ConfigComments.enableItalics)
                .define("enableItalics", false);
        enableHttpLinkProcessing = builder
                .comment(ConfigComments.enableHttpLinkProcessing)
                .define("enableHttpLinkProcessing", false);
        builder.pop();
    }

    public static ModConfigSpec getSpec() {
        return spec;
    }

    public static void pushChanges() {
        Settings.setShowDeathQuotesRegardlessOfGameRule(showDeathQuotesRegardlessOfGameRule.get());
        Settings.setNonRepeatablePercent(nonRepeatablePercent.get());
        Settings.setClearListOfNonRepeatableQuotes(clearListOfNonRepeatableQuotes.get());
        Settings.setEnableQuotationMarks(enableQuotationMarks.get());
        Settings.setEnableItalics(enableItalics.get());
        Settings.setEnableHttpLinkProcessing(enableHttpLinkProcessing.get());
        Settings.setPlayerNameReplaceString(playerNameReplaceString.get());
        Settings.setNextLineReplaceString(nextLineReplaceString.get());
        Settings.setEnableTrimmingBeforeAndAfterNextLine(enableTrimmingBeforeAndAfterNextLine.get());
    }
}
