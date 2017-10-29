package net.innectis.innplugin.player.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.innectis.innplugin.Configuration;

/**
 *
 * Class that will contain each message and filter it
 * according to specific conditions
 *
 * @author AlphaBlend
 */
public class ChatMessage {

    private String rawMessage;
    private final ChatColor msgColour;
    private String unfilteredMessage = null;
    private String filteredMessage = null;
    private String unfilteredUnmarkedMessage = null;
    private Boolean hasCussing = null;

    /**
     * Makes a new chatobject
     *
     * @param msg
     */
    public ChatMessage(String msg) {
        this(msg, ChatColor.WHITE);
    }

    /**
     * Makes a new chatobject
     *
     * @param msg
     * @param msgColor
     */
    public ChatMessage(String msg, ChatColor msgColor) {
        this.rawMessage = msg.trim(); //.replace("[", "").replace("]", "");
        this.msgColour = msgColor;
    }

    public String getCensoredMessage() {
        return getCensoredMessage(false);
    }

    public String getUncensoredMessage() {
        return getUncensoredMessage(false);
    }

    public String getUncensoredUnmarkedMessage() {
        return getUncensoredUnmarkedMessage(false);
    }

    /**
     * Returns the message where all filtered words are replaced by a default
     * string
     *
     * @return
     */
    public String getCensoredMessage(boolean isDistorted) {
        if (filteredMessage == null) {
            processRawString();
        }

        if (isDistorted) {
            Random random = new Random();
            int start = random.nextInt(filteredMessage.length());
            int end = start + (random.nextInt(filteredMessage.length() - start));
            return filteredMessage.substring(0, start) + ChatColor.EFFECT_MAGIC + filteredMessage.substring(start, end) + msgColour + filteredMessage.substring(end);
        }

        return filteredMessage;
    }

    /**
     * Returns the message where all filtered words are show in red
     *
     * @return
     */
    public String getUncensoredMessage(boolean isDistorted) {
        if (unfilteredMessage == null) {
            processRawString();
        }

        if (isDistorted) {
            Random random = new Random();
            int start = random.nextInt(unfilteredMessage.length());
            int end = start + (random.nextInt(unfilteredMessage.length() - start));
            return unfilteredMessage.substring(0, start) + ChatColor.EFFECT_MAGIC + unfilteredMessage.substring(start, end) + msgColour + unfilteredMessage.substring(end);
        }

        return unfilteredMessage;
    }

    /**
     * Returns the message where all filtered words are not filtered
     * or marked in any way
     *
     * @return
     */
    public String getUncensoredUnmarkedMessage(boolean isDistorted) {
        if (unfilteredUnmarkedMessage == null) {
            processRawString();
        }

        if (isDistorted) {
            Random random = new Random();
            int start = random.nextInt(unfilteredUnmarkedMessage.length());
            int end = start + (random.nextInt(unfilteredUnmarkedMessage.length() - start));
            return unfilteredUnmarkedMessage.substring(0, start) + ChatColor.EFFECT_MAGIC + unfilteredUnmarkedMessage.substring(start, end) + msgColour + unfilteredUnmarkedMessage.substring(end);
        }

        return unfilteredUnmarkedMessage;
    }

    /**
     * Filters the raw string in both a hidden and highlighted form
     * check accepted chars -> a-z, A-Z, 0-9 & _
     */
    private void processRawString() {
        if (Configuration.isBanFilterEmpty()) {
            filteredMessage = rawMessage;
            unfilteredMessage = rawMessage;
            hasCussing = false;
            return;
        }

        String[] splitMessage = rawMessage.split(" ");
        List<String> cycledWords = new ArrayList<String>(16);
        List<String> processedBannedWords = new ArrayList<String>(4);

        StringBuilder sbFilter = new StringBuilder(16);
        StringBuilder sbNoFilter = new StringBuilder(16);
        StringBuilder sbNoFilterUnmarked = new StringBuilder(16);

        for (String word : splitMessage) {
            String singleWord = stripchars(word);
            boolean filtered = false;

            if (!singleWord.isEmpty()) {
                Set<String> bannedWords = Configuration.getBannedWordsByLength(singleWord.length());

                // Make sure there are banned words for this word length
                if (bannedWords != null) {
                    String singleWordLowerCase = singleWord.toLowerCase();

                    // Make sure not to process a word already processed
                    if (!cycledWords.contains(singleWord)) {
                        cycledWords.add(singleWord);

                        if (bannedWords.contains(singleWordLowerCase)) {
                            filtered = true;
                            processedBannedWords.add(singleWordLowerCase);
                        }
                    } else {
                        filtered = processedBannedWords.contains(singleWordLowerCase);
                    }
                }
            }

            if (filtered) {
                sbFilter.append(word.replace(singleWord, Configuration.CHAT_CENSOR_REPLACEMENT));
                sbNoFilter.append(word.replace(singleWord, ChatColor.RED + singleWord + msgColour));
            } else {
                sbNoFilter.append(word);
                sbFilter.append(word);
            }

            sbNoFilterUnmarked.append(word);

            sbFilter.append(" ");
            sbNoFilter.append(" ");
            sbNoFilterUnmarked.append(" ");
        }

        filteredMessage = sbFilter.toString().trim();
        unfilteredMessage = sbNoFilter.toString().trim();
        unfilteredUnmarkedMessage = sbNoFilterUnmarked.toString().trim();
        hasCussing = (processedBannedWords.size() > 0);
    }

    private String stripchars(String message) {
        StringBuilder sb = new StringBuilder(32);
        for (char ch : message.toCharArray()) {
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || (ch == ' ')) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public boolean hasCussing() {
        if (hasCussing == null) {
            processRawString();
        }

        return hasCussing;
    }
    
}
