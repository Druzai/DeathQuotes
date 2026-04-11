package com.cazsius.deathquotes.config;

import com.cazsius.deathquotes.utils.Logger;

import java.lang.reflect.Field;
import java.util.Optional;

public final class ConfigComments {
    public static final String showDeathQuotesRegardlessOfGameRule = """
            Specifies whether death quotes should ignore the vanilla gamerule 'show_death_messages'.
            Default: true
            
            If false: Death quotes are only displayed if the gamerule 'show_death_messages' is set to true.
            If true: Death quotes are always displayed, regardless of the gamerule setting.""";
    public static final String nonRepeatablePercent = """
            Specifies the chance (0-100%) that a new quote will be unique (non-repeating).
            Default: 5
            
            100: Every quote is guaranteed to be unique until the quote pool is exhausted.
                 Once exhausted, quotes will cycle if 'clearListOfNonRepeatableQuotes' is false.
            0: Quotes are completely random and may repeat frequently (legacy mod behavior).
            
            Note: If you change this value at runtime, you must reload quotes from 'deathquotes.txt'!""";
    public static final String clearListOfNonRepeatableQuotes = """
            Specifies whether to clear the history of shown quotes once the quote pool is exhausted.
            Default: false
            
            If false: The mod keeps the history and replaces the oldest quote with the new one (works like FIFO queue).
            If true: The history is cleared, allowing previously shown quotes to appear again sooner.
            
            Tip: If you use a high 'nonRepeatablePercent', you may want to set this to true.
            Note: If you change this value at runtime, you must reload quotes from 'deathquotes.txt'!""";
    public static final String playerNameReplaceString = """
            The placeholder string used to insert the player's name into the death message.
            An empty or whitespace-only string will disable this feature!
            
            Example: '${{player_name}} didn't make it.'""";
    public static final String nextLineReplaceString = """
            The placeholder string used to insert a newline character in the death message.
            An empty or whitespace-only string will disable this feature!
            
            It can be used in dialogs for example:
            In deathquotes.txt:
            - Did you do it?${{next_line}}- Yes...${{next_line}}- What did it cost?${{next_line}}- Everything...
            In game:
            - Did you do it?
            - Yes...
            - What did it cost?
            - Everything...""";
    public static final String enableTrimmingBeforeAndAfterNextLine =
            "Removes leading/trailing spaces and tabs around the newline placeholder.";
    public static final String enableQuotationMarks = """
            Wraps the death message in quotation marks.
            Default: true""";
    public static final String enableItalics = """
            Renders the death message in italics.
            Default: false
            """;
    public static final String enableHttpLinkProcessing = """
            Makes HTTP/HTTPS links in the death message clickable.
            Default: false
            
            Tip: Surround links with spaces or quotation marks to ensure they are detected correctly.
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
