package com.cazsius.deathquotes.utils;

import java.util.regex.Pattern;

public class Constants {
    public static final String ID = "deathquotes";
    public static final String quotesFileName = "deathquotes.txt";
    public static final String quotesAssetPathAndFileName = "assets/" + quotesFileName;
    public static final String quotesConfigPath = "./config/";
    public static final String quotesPathAndFileName = quotesConfigPath + quotesFileName;
    public static final Pattern httpLinkPattern = Pattern.compile("(?<link>https?://(?:[a-zA-Z]|[0-9]|[#-_@.&+]|[!*(),]|%[0-9a-fA-F][0-9a-fA-F])+)");
}
