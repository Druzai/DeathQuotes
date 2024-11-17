package com.cazsius.deathquotes.config;

import com.cazsius.deathquotes.utils.Logger;

import java.lang.reflect.Field;
import java.util.Optional;

public final class ConfigComments {
    public static final String showDeathQuotesRegardlessOfGameRule = """
            Specifies whether death quotes should be displayed based on the value of gamerule "showDeathMessages" or not, default is TRUE
            If FALSE - death quotes will be displayed only if the value of gamerule "showDeathMessages" set to "true"
            If TRUE - death quotes will be displayed regardless of the value of gamerule "showDeathMessages\"""";
    public static final String nonRepeatablePercent = """
            Specifies the percentage of non-repeatable quotes
            100 - each quote is guaranteed to appear, but quotes will be repeated in a circle if option "clearListOfNonRepeatableQuotes" is set to FALSE
            0 - each quote might repeat frequently (i.e. old deathquotes mod behaviour)
            
            If you change it in runtime - reload quotes from file "deathquotes.txt"!""";
    public static final String clearListOfNonRepeatableQuotes = """
            Specifies should the mod clear the list of non-repeatable quotes if percent stated above has been reached, default is FALSE
            If FALSE - mod will keep the list and replace the oldest quote in that list with a new one
            If TRUE - mod will clear the list, so the first few quotes will be completely random
            
            If you set high percent, you might need to set this option to TRUE!
            If you change it in runtime - reload quotes from file "deathquotes.txt"!""";
    public static final String playerNameReplaceString = """
            String to replace with the player's name in the death message.
            Empty string or string only with whitespaces will disable this feature!
            
            Example: '${{player_name}} didn't make it.'""";
    public static final String nextLineReplaceString = """
            String to replace with the next line symbol in the death message.
            Empty string or string only with whitespaces will disable this feature!
            
            It can be used in dialogs for example:
            In deathquotes.txt:
            - Did you do it?${{next_line}}- Yes...${{next_line}}- What did it cost?${{next_line}}- Everything...
            In game:
            - Did you do it?
            - Yes...
            - What did it cost?
            - Everything...""";
    public static final String enableTrimmingBeforeAndAfterNextLine =
            "Remove spaces and tabulation symbols before and after symbol specified in option \"nextLineReplaceString\" above.";
    public static final String enableQuotationMarks =
            "Specifies whether death messages should be in quotation marks, default is TRUE";
    public static final String enableItalics =
            "Specifies whether death messages should be italicized, default is FALSE";
    public static final String enableHttpLinkProcessing = """
            Specifies whether http(s) links in death messages should be clickable, default is FALSE
            
            Note: It's better to separate links with spaces for better recognition!
            Example: 'Quote with the links: "https://www.google.com/", https://www.youtube.com/watch?v=dQw4w9WgXcQ !'""";

    public static Optional<String> getStringByName(String stringToFind) {
        Field[] fields = ConfigComments.class.getDeclaredFields();
        for (Field f : fields) {
            if (f.getName().equals(stringToFind)) {
                try {
                    return Optional.ofNullable((String) f.get(ConfigComments.class));
                } catch (IllegalAccessException ex) {
                    Logger.error("Couldn't access field \"{}\" of object \"{}\"!", f.getName(), ConfigComments.class.getName());
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }
}
