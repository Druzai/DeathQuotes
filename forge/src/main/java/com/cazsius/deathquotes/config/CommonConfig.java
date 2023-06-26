package com.cazsius.deathquotes.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class CommonConfig {
    private static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private static ForgeConfigSpec spec;
    private static ForgeConfigSpec.BooleanValue showDeathQuotesRegardlessOfGameRule;
    private static ForgeConfigSpec.IntValue nonRepeatablePercent;
    private static ForgeConfigSpec.BooleanValue clearListOfNonRepeatableQuotes;
    private static ForgeConfigSpec.BooleanValue enableQuotationMarks;
    private static ForgeConfigSpec.BooleanValue enableItalics;
    private static ForgeConfigSpec.BooleanValue enableHttpLinkProcessing;
    private static ForgeConfigSpec.ConfigValue<String> playerNameReplaceString;
    private static ForgeConfigSpec.ConfigValue<String> nextLineReplaceString;
    private static ForgeConfigSpec.BooleanValue enableTrimmingBeforeAndAfterNextLine;

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

    public static ForgeConfigSpec getSpec() {
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
