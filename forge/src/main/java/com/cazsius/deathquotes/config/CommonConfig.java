package com.cazsius.deathquotes.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class CommonConfig {
    private static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private static ForgeConfigSpec spec;
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
        nonRepeatablePercent = builder
                .comment("""
                        Specifies the percentage of non-repeatable quotes
                        100 - each quote is guaranteed to appear, but quotes will be repeated in a circle if option "clearListOfNonRepeatableQuotes" is set to FALSE
                        0 - each quote might repeat frequently
                                                
                        If you change it in runtime - reload quotes from file "deathquotes.txt"!""")
                .defineInRange("nonRepeatablePercent", 5, 0, 100);
        clearListOfNonRepeatableQuotes = builder
                .comment("""
                        Specifies should the mod clear the list of non-repeatable quotes if percent stated above has been reached, default is FALSE
                        If FALSE - mod will keep the list and replace the oldest quote in that list with a new one
                        If TRUE - mod will clear the list, so the first few quotes will be completely random
                                                
                        If you set high percent, you might need to set this option to TRUE!
                        If you change it in runtime - reload quotes from file "deathquotes.txt"!""")
                .define("clearListOfNonRepeatableQuotes", false);
        playerNameReplaceString = builder
                .comment("""
                        String to replace with the player's name in the death message.
                        Empty string or string only with whitespaces will disable the feature!
                                                
                        Example: '${{player_name}} didn't make it.'""")
                .define("playerNameReplaceString", "${{player_name}}");
        nextLineReplaceString = builder
                .comment("""
                        String to replace with the next line symbol in the death message.
                        Empty string or string only with whitespaces will disable the feature!
                                                
                        It can be used in dialogs for example:
                        In deathquotes.txt:
                        - Did you do it?${{next_line}}- Yes...${{next_line}}- What did it cost?${{next_line}}- Everything...
                        In game:
                        - Did you do it?
                        - Yes...
                        - What did it cost?
                        - Everything...""")
                .define("nextLineReplaceString", "${{next_line}}");
        enableTrimmingBeforeAndAfterNextLine = builder
                .comment("Remove spaces and tabulation symbols before and after symbol specified in option \"nextLineReplaceString\" above.")
                .define("enableTrimmingBeforeAndAfterNextLine", false);
        builder.pop();
    }

    private static void setFormattingOptions() {
        builder.push("formattingOptions");
        enableQuotationMarks = builder
                .comment("Specifies whether death messages should be in quotation marks, default is TRUE")
                .define("enableQuotationMarks", true);
        enableItalics = builder
                .comment("Specifies whether death messages should be italicized, default is FALSE")
                .define("enableItalics", false);
        enableHttpLinkProcessing = builder
                .comment("""
                        Specifies whether http(s) links in death messages should be clickable, default is FALSE
                        Note: It's better to separate links with spaces for better recognition!
                        Example: 'Quote with the links: "https://www.google.com/", https://www.youtube.com/watch?v=dQw4w9WgXcQ !'""")
                .define("enableHttpLinkProcessing", false);
        builder.pop();
    }

    public static ForgeConfigSpec getSpec() {
        return spec;
    }

    public static void pushChanges() {
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
