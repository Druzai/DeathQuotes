package com.cazsius.deathquotes.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    private final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private final ForgeConfigSpec spec;
    private ForgeConfigSpec.IntValue nonRepeatablePercent;
    private ForgeConfigSpec.BooleanValue clearListOfNonRepeatableQuotes;
    private ForgeConfigSpec.BooleanValue enableQuotationMarks;
    private ForgeConfigSpec.BooleanValue enableItalics;
    private ForgeConfigSpec.BooleanValue enableHttpLinkProcessing;
    private ForgeConfigSpec.ConfigValue<String> playerNameReplaceString;
    private ForgeConfigSpec.ConfigValue<String> nextLineReplaceString;
    private ForgeConfigSpec.BooleanValue enableTrimmingBeforeAndAfterNextLine;

    public CommonConfig() {
        builder.push("deathQuotes");
        setMainOptions();
        setFormattingOptions();
        builder.pop();

        spec = builder.build();
    }

    private void setMainOptions() {
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

    private void setFormattingOptions() {
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

    public ForgeConfigSpec getSpec() {
        return spec;
    }

    public int getNonRepeatablePercent() {
        return nonRepeatablePercent.get();
    }

    public void setNonRepeatablePercent(int nonRepeatablePercent) {
        this.nonRepeatablePercent.set(nonRepeatablePercent);
    }

    public boolean getClearListOfNonRepeatableQuotes() {
        return clearListOfNonRepeatableQuotes.get();
    }

    public void setClearListOfNonRepeatableQuotes(boolean clearListOfNonRepeatableQuotes) {
        this.clearListOfNonRepeatableQuotes.set(clearListOfNonRepeatableQuotes);
    }

    public boolean getEnableQuotationMarks() {
        return enableQuotationMarks.get();
    }

    public void setEnableQuotationMarks(boolean enableQuotationMarks) {
        this.enableQuotationMarks.set(enableQuotationMarks);
    }

    public boolean getEnableItalics() {
        return enableItalics.get();
    }

    public void setEnableItalics(boolean enableItalics) {
        this.enableItalics.set(enableItalics);
    }

    public boolean getEnableHttpLinkProcessing() {
        return enableHttpLinkProcessing.get();
    }

    public void setEnableHttpLinkProcessing(boolean enableHttpLinkProcessing) {
        this.enableHttpLinkProcessing.set(enableHttpLinkProcessing);
    }

    public String getPlayerNameReplaceString() {
        return playerNameReplaceString.get();
    }

    public void setPlayerNameReplaceString(String playerNameReplaceString) {
        this.playerNameReplaceString.set(playerNameReplaceString);
    }

    public String getNextLineReplaceString() {
        return nextLineReplaceString.get();
    }

    public void setNextLineReplaceString(String nextLineReplaceString) {
        this.nextLineReplaceString.set(nextLineReplaceString);
    }

    public boolean getEnableTrimmingBeforeAndAfterNextLine() {
        return enableTrimmingBeforeAndAfterNextLine.get();
    }

    public void setEnableTrimmingBeforeAndAfterNextLine(boolean enableTrimmingBeforeAndAfterNextLine) {
        this.enableTrimmingBeforeAndAfterNextLine.set(enableTrimmingBeforeAndAfterNextLine);
    }
}
