package com.cazsius.deathquotes.config;

public final class Settings {
    private static int nonRepeatablePercent;
    private static boolean clearListOfNonRepeatableQuotes;
    private static boolean enableQuotationMarks;
    private static boolean enableItalics;
    private static boolean enableHttpLinkProcessing;
    private static String playerNameReplaceString;
    private static String nextLineReplaceString;
    private static boolean enableTrimmingBeforeAndAfterNextLine;

    public static int getNonRepeatablePercent() {
        return nonRepeatablePercent;
    }

    public static void setNonRepeatablePercent(int nonRepeatablePercent) {
        Settings.nonRepeatablePercent = nonRepeatablePercent;
    }

    public static boolean getClearListOfNonRepeatableQuotes() {
        return clearListOfNonRepeatableQuotes;
    }

    public static void setClearListOfNonRepeatableQuotes(boolean clearListOfNonRepeatableQuotes) {
        Settings.clearListOfNonRepeatableQuotes = clearListOfNonRepeatableQuotes;
    }

    public static boolean getEnableQuotationMarks() {
        return enableQuotationMarks;
    }

    public static void setEnableQuotationMarks(boolean enableQuotationMarks) {
        Settings.enableQuotationMarks = enableQuotationMarks;
    }

    public static boolean getEnableItalics() {
        return enableItalics;
    }

    public static void setEnableItalics(boolean enableItalics) {
        Settings.enableItalics = enableItalics;
    }

    public static boolean getEnableHttpLinkProcessing() {
        return enableHttpLinkProcessing;
    }

    public static void setEnableHttpLinkProcessing(boolean enableHttpLinkProcessing) {
        Settings.enableHttpLinkProcessing = enableHttpLinkProcessing;
    }

    public static String getPlayerNameReplaceString() {
        return playerNameReplaceString;
    }

    public static void setPlayerNameReplaceString(String playerNameReplaceString) {
        Settings.playerNameReplaceString = playerNameReplaceString;
    }

    public static String getNextLineReplaceString() {
        return nextLineReplaceString;
    }

    public static void setNextLineReplaceString(String nextLineReplaceString) {
        Settings.nextLineReplaceString = nextLineReplaceString;
    }

    public static boolean getEnableTrimmingBeforeAndAfterNextLine() {
        return enableTrimmingBeforeAndAfterNextLine;
    }

    public static void setEnableTrimmingBeforeAndAfterNextLine(boolean enableTrimmingBeforeAndAfterNextLine) {
        Settings.enableTrimmingBeforeAndAfterNextLine = enableTrimmingBeforeAndAfterNextLine;
    }
}
