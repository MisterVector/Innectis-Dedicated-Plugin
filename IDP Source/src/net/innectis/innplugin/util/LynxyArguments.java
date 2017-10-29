package net.innectis.innplugin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.IdpPlayer;
/**
 *
 * @author Lynxy
 *
 */
public class LynxyArguments {

    private List<String> actions = new ArrayList<String>();
    private List<String> options = new ArrayList<String>();
    private Map<String, String> arguments = new HashMap<String, String>();
    private final String MULTI_WORD_CHARACTER = "'";

    public LynxyArguments(String[] args) {
        String lastArgName = null;

        for (String arg : expandArguments(args)) {
            if (arg.length() > 0) {
                if (arg.substring(0, 1).equalsIgnoreCase("-")) {
                    if (lastArgName != null) {
                        options.add(lastArgName.toLowerCase());
                    }

                    lastArgName = arg.substring(1, arg.length());
                } else {
                    if (lastArgName == null) {
                        actions.add(arg);
                    } else {
                        arguments.put(lastArgName.toLowerCase(), arg);
                        lastArgName = null;
                    }
                }
            }
        }

        if (lastArgName != null) {
            options.add(lastArgName.toLowerCase());
        }
    }

    private boolean indexOutOfRange(int index) {
        return (index < 0 || index >= actions.size());
    }

    /**
     * Returns the number of actions
     */
    public int getActionSize() {
        return actions.size();
    }

    /**
     * Returns the number of options
     */
    public int getOptionSize() {
        return options.size();
    }

    /**
     * Returns the number of arguments
     */
    public int getArgumentSize() {
        return arguments.size();
    }

    /**
     * Combines all actions into one string
     */
    public String combineActions() {
        return combineActions(" ");
    }

    /**
     * Combines all actions into one string
     * @param startIndex the starting index of the action
     */
    public String combineActions(int startIndex) {
        return combineActions(startIndex, " ");
    }

    /**
     * Combines all actions into one string
     * @param delimiter the delimiter of the action
     */
    public String combineActions(String delimiter) {
        return combineActions(0, " ");
    }

    /**
     * Combines all actions into one string
     * @param startIndex the action index to start at
     * @param delimiter
     * @return
     */
    public String combineActions(int startIndex, String delimiter) {
        if (startIndex > actions.size() - 1) {
            throw new IllegalArgumentException("start index is greater than action count: " + actions.size());
        }

        String combined = "";

        for (int i = startIndex; i < actions.size(); i++) {
            String action = actions.get(i);

            if (!combined.isEmpty()) {
                combined += delimiter;
            }

            combined += action;
        }

        return combined;
    }

    /**
     * Given a list of options, returns true if any of the options exist
     * @param option
     */
    public boolean hasOption(String... option) {
        for (String opt : option) {
            if (options.contains(opt.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether or not any of the specified arguments exist
     * @param argument
     * @return
     */
    public boolean hasArgument(String... argument) {
        for (String arg : argument) {
            if (arguments.containsKey(arg.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a String for the ACTION at <index> (zero-based)
     * @param index
     */
    public String getString(int index) {
        if (indexOutOfRange(index)) {
            return null;
        }
        return actions.get(index);
    }

    /**
     * Returns a String for the ARGUMENT for any arg in list <args>
     * @param args
     */
    public String getString(String... args) {
        for (String arg : args) {
            if (arguments.containsKey(arg.toLowerCase())) {
                return arguments.get(arg.toLowerCase());
            }
        }
        return null;
    }

    /**
     * Returns a Boolean for the ACTION at <index> (zero-based)
     * @param index
     */
    public Boolean getBoolean(int index) {
        if (indexOutOfRange(index)) {
            return null;
        }
        return StringUtil.matches(actions.get(index), "true", "1", "on", "yes");
    }

    /**
     * Returns a Boolean for the ARGUMENT for any arg in list <args>
     * @param args
     */
    public Boolean getBoolean(String... args) {
        for (String arg : args) {
            if (arguments.containsKey(arg.toLowerCase())) {
                return StringUtil.matches(arguments.get(arg.toLowerCase()), "true", "1", "on", "yes");
            }
        }
        return null;
    }

    /**
     * Returns an Integer for the ACTION at <index> (zero-based)
     * @param index
     */
    public Integer getInt(int index) throws NumberFormatException {
        if (indexOutOfRange(index)) {
            return null;
        }
        return Integer.parseInt(actions.get(index));
    }

    /**
     * Returns an Integer for the ARGUMENT for any arg in list <args>
     * @param index
     */
    public Integer getInt(String... args) throws NumberFormatException {
        for (String arg : args) {
            if (arguments.containsKey(arg.toLowerCase())) {
                return Integer.parseInt(arguments.get(arg.toLowerCase()));
            }
        }
        return null;
    }

    /**
     * Returns an Integer for the ACTION at <index> (zero-based). If nothing found, or invalid, will return <defaultTo>
     * @param index
     */
    public Integer getIntDefault(int index, int defaultTo) {
        if (indexOutOfRange(index)) {
            return defaultTo;
        }
        try {
            return Integer.parseInt(actions.get(index));
        } catch (NumberFormatException ex) {
            return defaultTo;
        }
    }

    /**
     * Returns an Integer for the ARGUMENT for any arg in list <args>. If nothing found, or invalid, will return <defaultTo>
     * @param index
     */
    public Integer getIntDefault(int defaultTo, String... args) {
        for (String arg : args) {
            if (arguments.containsKey(arg.toLowerCase())) {
                try {
                    return Integer.parseInt(arguments.get(arg.toLowerCase()));
                } catch (NumberFormatException ex) {
                    return defaultTo;
                }
            }
        }
        return defaultTo;
    }

    /**
     * Returns an IdpPlayer for the ACTION at <index> (zero-based)
     * @param index
     */
    public IdpPlayer getPlayer(int index) throws NumberFormatException {
        if (indexOutOfRange(index)) {
            return null;
        }
        return InnPlugin.getPlugin().getPlayer(getString(index), false);
    }

    /**
     * Returns an IdpPlayer for the ARGUMENT for any arg in list <args>
     * @param index
     */
    public IdpPlayer getPlayer(String... args) throws NumberFormatException {
        for (String arg : args) {
            if (arguments.containsKey(arg.toLowerCase())) {
                return InnPlugin.getPlugin().getPlayer(arguments.get(arg.toLowerCase()), false);
            }
        }
        return null;
    }

    /**
     * Returns an IdpMaterial for the ACTION at <index> (zero-based)
     * @param index
     */
    public IdpMaterial getMaterial(int index) throws NumberFormatException {
        if (indexOutOfRange(index)) {
            return null;
        }
        try {
            return IdpMaterial.fromString(getString(index));
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Returns an IdpMaterial for the ARGUMENT for any arg in list <args>
     * @param index
     */
    public IdpMaterial getMaterial(String... args) throws NumberFormatException {
        for (String arg : args) {
            if (arguments.containsKey(arg.toLowerCase())) {
                try {
                    return IdpMaterial.fromString(arguments.get(arg.toLowerCase()));
                } catch (Exception ex) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Returns an InnectisLot for the ACTION at <index> (zero-based)
     * @param index
     */
    public InnectisLot getLot(int index) throws NumberFormatException {
        if (indexOutOfRange(index)) {
            return null;
        }
        try {
            return LotHandler.getLot(getInt(index));
        } catch (NumberFormatException ex){
            return LotHandler.getLot(getString(index));
        }
    }

    /**
     * Returns an InnectisLot for the ARGUMENT for any arg in list <args>
     * @param index
     */
    public InnectisLot getLot(String... args) throws NumberFormatException {
        for (String arg : args) {
            if (arguments.containsKey(arg.toLowerCase())) {
                try {
                    return LotHandler.getLot(getInt(arguments.get(arg.toLowerCase())));
                } catch (NumberFormatException ex){
                    return LotHandler.getLot(getString(arguments.get(arg.toLowerCase())));
                }
            }
        }
        return null;
    }

    /**
     * Tries to concatenate strings that use the quote character
     * along with all other arguments into one array. Unterminated
     * strings will result in an improper array
     * @param args
     * @return
     */
    private String[] expandArguments(String[] args) {
        List<String> tempStrings = new ArrayList<String>();
        String currentString = "";
        boolean addMore = false;

        for (String arg : args) {
            if (addMore) {
                currentString += " " + arg;

                if (arg.endsWith(MULTI_WORD_CHARACTER) && !arg.endsWith("\\" + MULTI_WORD_CHARACTER)) {
                    currentString = currentString.substring(0, currentString.length() - 1);
                    tempStrings.add(currentString);
                    currentString = "";
                    addMore = false;
                }
            } else {
                if (arg.startsWith(MULTI_WORD_CHARACTER)) {
                    arg = arg.substring(1);
                    currentString = arg;
                    addMore = true;
                } else {
                    tempStrings.add(arg);
                }
            }
        }

        return tempStrings.toArray(new String[tempStrings.size()]);
    }

}
