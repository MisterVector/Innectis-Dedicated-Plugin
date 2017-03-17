package net.innectis.innplugin.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.innectis.innplugin.player.chat.ChatColor;
import org.bukkit.block.BlockFace;

/**
 * @author Hret
 *
 * Utilities that has to do with strings and other type of objects.
 * All methods in this class are tested and have been proven to work.
 * <p /> *
 * <b>Do not edit the way it returns the value without checking where its used. <b/>
 *
 */
public final class StringUtil {

    private StringUtil() {
    }

    /**
     * Checks if the string is null or if the string is empt.
     * @param str
     * @return true is the string is null or if its empty
     */
    public static boolean stringIsNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Formats the string and replaces all the objects with the corresponding location
     * @param str <br/>
     * The formatstring to enter the objects.
     * <p/>
     * Use {x} to show where theoh wait objects need to be placed.<br/>
     * The <b>x</b> will correspond with the index of the objects. (this is zero-based)
     *
     * @param objects <br/>
     * The Objects to fill in the format string.<br/>
     * Note: the toString() method is used to fill in the object!
     * @return true is the string is null or if its empty
     */
    public static String format(String format, Object... objects) {
        if (format == null) {
            throw new IllegalArgumentException("Format is null!");
        }

        for (int i = 0; i < objects.length; i++) {
            format = format.replace("{" + i + "}", objects[i] != null ? objects[i].toString() : "");
        }

        return format;
    }

    /**
     * Trim a string if it is longer than a certain length.
     *
     * @param str
     * @param len
     * @return
     */
    public static String trimLength(String str, int len) {
        if (str.length() > len) {
            return str.substring(0, len);
        }

        return str;
    }

    /**
     * Trims the string down to a certain length, appending with a specific
     * string if this is done.
     * @param str
     * @param len
     * @param append
     * @return
     */
    public static String trimLengthFancy(String str, int len, String append) {
        if (str.length() > len) {
            return trimLength(str, len - append.length()) + append;
        }
        return str;
    }

    /**
     * Join an array of strings into a string.
     *
     * @param str
     * @param delimiter
     * @param initialIndex
     * @param quote
     * @return
     */
    public static String joinQuotedString(String[] str, String delimiter, int initialIndex, String quote) {
        if (str.length == 0) {
            return "";
        }

        StringBuilder buffer = new StringBuilder(512);
        buffer.append(quote).append(str[initialIndex]).append(quote);
        for (int i = initialIndex + 1; i < str.length; ++i) {
            buffer.append(delimiter).append(quote).append(str[i]).append(quote);
        }
        return buffer.toString();
    }

    /**
     * This method will join an array of String/Objects.
     * It will make use of the <b>toString()<b/> method to get the stringvalue.
     * <p/>
     * <b>Note:</b>
     * This method will ignore (and skip) NULL values being passed to this object.
     *
     * @param array
     * The objects
     * @param delimiter
     * The text that needs to be in between each string.
     * @param initialIndex
     * The place where the method should start joining.
     * @return The objects in a joined string
     */
    public static String joinString(Object[] array, String delimiter, int initialIndex) {
        if (array == null || array.length == 0) {
            return "";
        }

        StringBuilder buffer = new StringBuilder(512);
        for (int i = initialIndex; i < array.length; i++) {
            if (array[i] != null) { // Ignore NULL
                buffer.append(delimiter).append(array[i].toString());
            }
        }
        return buffer.toString().substring(delimiter.length());
    }

    /**
     * This method will join an array of String/Objects.
     * It will make use of the <b>toString()<b/> method to get the stringvalue.
     * <p/>
     * <b>Note:</b>
     * This method will ignore (and skip) NULL values being passed to this object.
     *
     * @param array
     * The objects
     * @param delimiter
     * The text that needs to be in between each string
     * @return The objects in a joined string
     */
    public static String joinString(Object[] array, String delimiter) {
        return joinString(array, delimiter, 0);
    }

    /**
     * This method will join an array of String/Objects.
     * It will make use of the <b>toString()<b/> method to get the stringvalue.
     * <p/>
     * <b>Note:</b>
     * This method will ignore (and skip) NULL values being passed to this object.
     *
     * @param collection
     * The objects
     * @param delimiter
     * The text that needs to be in between each string.
     * @param initialIndex
     * The place where the method should start joining.
     * @return The objects in a joined string
     */
    public static String joinString(Collection<?> collection, String delimiter, int initialIndex) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        return joinString(collection.toArray(), delimiter, initialIndex);
    }

    /**
     * Wraps the text entered according to a given wrap length value. The
     * resulting text will be wrapped as many times as necessary according
     * to its total length and the wrap value given as parameter
     * @param text
     * @param minWrapLength The minimym length for a string before it is wrapped. This
     * string will be wrapped if the next word in the sequence is equal or greater than
     * the minimum wrap length
     */
    public static List<String> wrapText(String text, int minWrapLength) {
        String[] parts = text.split(" ");
        List<String> messages = new ArrayList<String>();
        String tempMessage = "";
        ChatColor lastColor = null;

        for (String part : parts) {
            if (tempMessage.isEmpty()) {
                // If preserving the color from the last
                // message, do not add a space here
                if (lastColor != null) {
                    tempMessage = lastColor.toString();
                }

                tempMessage += part;
            } else {
                tempMessage += " " + part;
            }

            // Check for a text formatter to preserve the format of
            // any following messages
            for (int i = 0; i < part.length(); i++) {
                if (part.charAt(i) == '\u00A7' && i < part.length() - 1) {
                    String colorCode = part.substring(i + 1, i + 2);
                    lastColor = ChatColor.getByCode(colorCode);
                }
            }

            // If the length exceeds the max to wrap this text
            // then save this message and start fresh
            if (tempMessage.length() >= minWrapLength) {
                messages.add(tempMessage);
                tempMessage = "";
            }
        }

        if (!tempMessage.isEmpty()) {
            messages.add(tempMessage);
        }

        return messages;
    }

    /**
     * @author sk89q
     * <b> This is taken from WorldEdit <b/>
     *
     * <p>Find the Levenshtein distance between two Strings.</p>
     *
     * <p>This is the number of changes needed to change one String into
     * another, where each change is a single character modification (deletion,
     * insertion or substitution).</p>
     *
     * <p>The previous implementation of the Levenshtein distance algorithm
     * was from <a href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld.htm</a></p>
     *
     * <p>Chas Emerick has written an implementation in Java, which avoids an OutOfMemoryError
     * which can occur when my Java implementation is used with very large strings.<br>
     * This implementation of the Levenshtein distance algorithm
     * is from <a href="http://www.merriampark.com/ldjava.htm">http://www.merriampark.com/ldjava.htm</a></p>
     *
     * <pre>
     * StringUtil.getLevenshteinDistance(null, *)             = IllegalArgumentException
     * StringUtil.getLevenshteinDistance(*, null)             = IllegalArgumentException
     * StringUtil.getLevenshteinDistance("","")               = 0
     * StringUtil.getLevenshteinDistance("","a")              = 1
     * StringUtil.getLevenshteinDistance("aaapppp", "")       = 7
     * StringUtil.getLevenshteinDistance("frog", "fog")       = 1
     * StringUtil.getLevenshteinDistance("fly", "ant")        = 3
     * StringUtil.getLevenshteinDistance("elephant", "hippo") = 7
     * StringUtil.getLevenshteinDistance("hippo", "elephant") = 7
     * StringUtil.getLevenshteinDistance("hippo", "zzzzzzzz") = 8
     * StringUtil.getLevenshteinDistance("hello", "hallo")    = 1
     * </pre>
     *
     * @param s the first String, must not be null
     * @param t the second String, must not be null
     * @return result distance
     * @throws IllegalArgumentException if either String input <code>null</code>
     */
    public static int getLevenshteinDistance(String s, String t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        /*
         * The difference between this impl. and the previous is that, rather
         * than creating and retaining a matrix of size s.length()+1 by
         * t.length()+1, we maintain two single-dimensional arrays of length
         * s.length()+1. The first, d, is the 'current working' distance array
         * that maintains the newest distance cost counts as we iterate through
         * the characters of String s. Each time we increment the index of
         * String t we are comparing, d is copied to p, the second int[]. Doing
         * so allows us to retain the previous cost counts as required by the
         * algorithm (taking the minimum of the cost count to the left, up one,
         * and diagonally up and to the left of the current cost count being
         * calculated). (Note that the arrays aren't really copied anymore, just
         * switched...this is clearly much better than cloning an array or doing
         * a System.arraycopy() each time through the outer loop.)
         *
         * Effectively, the difference between the two implementations is this
         * one does not cause an out of memory condition when calculating the LD
         * over two very large strings.
         */

        int n = s.length(); // length of s
        int m = t.length(); // length of t

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        int p[] = new int[n + 1]; // 'previous' cost array, horizontally
        int d[] = new int[n + 1]; // cost array, horizontally
        int _d[]; // placeholder to assist in swapping p and d

        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t

        char t_j; // jth character of t

        int cost; // cost

        for (i = 0; i <= n; ++i) {
            p[i] = i;
        }

        for (j = 1; j <= m; ++j) {
            t_j = t.charAt(j - 1);
            d[0] = j;

            for (i = 1; i <= n; ++i) {
                cost = s.charAt(i - 1) == t_j ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left
                // and up +cost
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1]
                        + cost);
            }

            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // our last action in the above loop was to switch d and p, so p now
        // actually has the most recent cost counts
        return p[n];
    }

    /**
     * Checks if the given value matches any of the given triggers.
     * <p />
     * <b>* matches("test", "tes", "test")</b>: will return true <br/>
     * <b>* matches("test", "tes", "TEST")</b>: will return true <br/>
     * <b>* matches(null, "tes", null)</b>: will return true <br/>
     * <br/>
     * <b>* matches("test", "tes")</b>: will return false <br/>
     * <b>* matches("", "tes", "TEST")</b>: will return false <br/>
     * <b>* matches(null, "tes", "test")</b>: will return false <br/>
     * <p/>
     * Like shown in the examples, this method ignores the case of the values.
     * @param value
     * The value it should check for.
     * @param triggers
     * The values what should trigger an match.
     * @return
     * True if the value was matched by a trigger. Otherwise false.
     */
    public static boolean matches(String value, String... triggers) {
        for (String match : triggers) {
            if (value == null) {
                if (match == null) {
                    return true;
                }
            } else if (value.equalsIgnoreCase(match)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts a block face into its string representation with some exceptions.
     * Example: NORTH becomes North, SOUTH_WEST becomes Southwest, etc.
     * @param face
     * @return
     */
    public static String blockFaceToString(BlockFace face) {
        String faceString = face.name();
        faceString = faceString.replace("_", "").toLowerCase();

        // Make sure first character is uppercase
        char upper = Character.toUpperCase(faceString.charAt(0));

        return upper + faceString.substring(1);
    }

    /**
     * Converts a string to an array of strings
     * with a given length of each array element
     * @param str
     * @param charLength
     * @return
     */
    public static String[] stringToArray(String str, int charLength) {
        List<String> stringList = new ArrayList<String>();
        String[] parts = str.split(" ");
        String currentString = "";
        int count = 0;

        for (String part : parts) {
            if (!currentString.isEmpty()) {
                currentString += " ";
                count++;
            }

            currentString += part;
            count += part.length();

            if (count >= charLength) {
                stringList.add(currentString);
                currentString = "";
                count = 0;
            }
        }

        if (!currentString.isEmpty()) {
            stringList.add(currentString);
        }

        return stringList.toArray(new String[stringList.size()]);
    }
    
}
