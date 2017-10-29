package net.innectis.innplugin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Hret
 *
 * Extendsion of Arguments to make it possible to parameterize commands.
 *
 * This is now a adjusted version of Lynxy's CommandArguments.
 * This having the exception of also working together with smartargs and the methods it provides.
 *
 */
public class ParameterArguments extends Arguments<Object> {

    /** Make a variable that will define an empty value. */
    private static final String EMPTY_VAL = java.util.UUID.randomUUID().toString();
    private List<String> actions = new ArrayList<String>();
    private Map<String, String> arguments = new HashMap<String, String>();

    public ParameterArguments(String[] args) {
        super(args);
        contructParameterArguments(super.getDelimitedArgs());
    }

    /**
     * Constructs the command args
     * @param args
     */
    private void contructParameterArguments(String[] args) {
        String lastArgName = null;

        for (String arg : args) {
            if (arg.substring(0, 1).equalsIgnoreCase("-")) {
                if (lastArgName != null) {
                    arguments.put(lastArgName.toLowerCase(), arg);
                }
                lastArgName = arg.substring(1, arg.length());
            } else {
                if (lastArgName == null) {
                    actions.add(arg);
                } else {
                    arguments.put(lastArgName.toLowerCase(), arg.replace("\\-", "-"));
                    lastArgName = null;
                }
            }
        }

        if (lastArgName != null) {
            arguments.put(lastArgName.toLowerCase(), EMPTY_VAL);
        }
    }

    /**
     * Returns the number of actions
     */
    @Override
    public int size() {
        return actions.size();
    }

    /**
     * This method will check if the option is given in the method. <br/>
     * Note: this method will not difference between arguments or options. <br/>
     * That means that every argument is also defined as an option
     * (it might occur that the values are options).
     *
     * @param key to look for
     * @return true if option is given
     */
    public boolean hasOption(String... keys) {
        for (String key : keys) {
            if (arguments.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the string at the given key.
     * <p/>
     * Giving an integer will return the action. <br/>
     * For instance: <b>/cmd test test2 -a test4</b> <br/>
     * Both <b>test</b> as <b>test2</b> are actions.<br/>
     * Passing 0 to this method will return <b>test</b>.
     * <p/>
     * Giving a string will return the argument. </br>
     * In the same as as the actions above the <b>-a</b> lists an action. <br/>
     * When <b>a</b> is given to this method it will return the first thing
     * behind the trigger. In this case it will return '<b>test4</b>'.
     *
     * @param key
     * @return the string value for the given key
     */
    @Override
    public String getString(Object... key) {
        for (Object obj : key) {
            // if int then its an action
            if (obj instanceof Integer) {
                try {
                    int index = Integer.parseInt(obj.toString());
                    return getAction(index);
                } catch (NumberFormatException nfe) {
                    // Do nothing
                }
            } else {
                String strkey = obj.toString().toLowerCase();

                // Return the argument
                if (arguments.containsKey(strkey)) {
                    String value = arguments.get(strkey);
                    // Dont return something your sure of is an option.
                    if (!value.equals(EMPTY_VAL)) {
                        return value;
                    }
                }

            }
        }
        return null;
    }

    /**
     * Returns the 'raw' smart args
     * @return
     */
    @Override
    public String[] getArguments() {
        return getDelimitedArgs();
    }

    /**
     * Returns true if action index (zero-based) matches any of <match>
     * @param index
     * @param match
     */
    public boolean actionMatches(int index, String... match) {
        String action = getAction(index);
        if (action == null) {
            return false;
        }
        for (String m : match) {
            if (action.equalsIgnoreCase(m)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an action based off index (zero-based)
     * @param index
     */
    private String getAction(int index) {
        if (index < 0 || index >= actions.size()) {
            return null;
        }
        return actions.get(index);
    }
    
}
