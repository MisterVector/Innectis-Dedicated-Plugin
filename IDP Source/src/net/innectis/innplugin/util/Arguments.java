package net.innectis.innplugin.util;

import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.tinywe.blockcounters.MaterialSelector;

/**
 *
 * @author Hret
 *
 * Argument type superclass. This is used to make arguments so that they work
 * with Generics to allow other things as key's
 */
abstract class Arguments<T> {

    private static final String delimiter = "'";
    /**
     * The source arguments
     */
    private String[] sourceArguments;
    private String[] delimitedArgs;

    protected Arguments(String[] sourceArguments) {
        this.sourceArguments = sourceArguments;
        findDelimitedArgs(sourceArguments);
    }

    /**
     * Checks if the size of the argument list is in the sizes. <br/>
     * Meaning:<br/> If you give 1,2,3 as parameters.<br/> It will return true
     * if the size() method returns 1,2 or 3.<br/> Else it will return false
     * <br/> <br/> <i> This example can also be written as:<br/> if (size() == 1
     * || size() == 2 || size() == 3)<br/> return true;<br/> else <br/> return
     * false;<br/> </i><br/>
     *
     * @param sizes
     * @return True if size is in the list
     */
    public boolean checkArgumentAmount(int... sizes) {
        for (int i : sizes) {
            if (i == size()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retuns the source string array
     *
     * @return array
     */
    protected String[] getSourceArguments() {
        return sourceArguments;
    }

    /**
     * Gets the array of delimited arguments
     */
    protected String[] getDelimitedArgs() {
        return delimitedArgs;
    }

    // <editor-fold defaultstate="collapsed" desc="Getting methods">
    /**
     * Returns a boolean that is on the given spot in the arguments. If the
     * value 'true' can't be discovered, false is returned.<br /> <b> This will
     * accept 'on', 'yes', 'true' and '1' as values</b>
     *
     * @param key
     * @return the boolean
     */
    public boolean getBoolean(T... key) {
        try {
            String str = getString(key);
            if (str == null) {
                return false;
            }
            if (str.equalsIgnoreCase("on") || str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("true") || str.equalsIgnoreCase("1")) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the string on the given place as an Integer. If the value is not
     * an integer it will throw an NotANumberException
     *
     * @param key
     * @return the number
     * @throws NotANumberException
     */
    public int getInt(T... key) throws NotANumberException {
        String value = getString(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            throw new NotANumberException("Input is not an integer! (" + value + ")");
        }
    }

    /**
     * *
     *
     * Returns the string on the given place as an Integer. If the value is not
     * an integer it will return the default value instead.
     *
     * @param key
     * @param defaultValue
     * @return the value or the default value
     */
    public int getIntDefaultTo(int defaultValue, T... key) {
        try {
            return Integer.parseInt(getString(key));
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * Returns the string on the given place as an Integer. It will be formatted
     * against the given radix. If the value is not an integer it will throw an
     * NotANumberException
     *
     * @param key
     * @param radix
     * @return the number
     * @throws NotANumberException
     */
    public int getIntRadix(int radix, T... key) throws NotANumberException {
        String value = getString(key);
        try {
            return Integer.parseInt(value, radix);
        } catch (NumberFormatException nfe) {
            throw new NotANumberException("Input is not an integer! (" + value + ")");
        }
    }

    /**
     * Returns the material value that is on the given spot in the arguments. If
     * the item is not material, or the material is not found. Null is returned.
     *
     * @param key
     * @return the Material or null
     */
    public IdpMaterial getMaterial(T... key) {
        try {
            return IdpMaterial.fromString(getString(key));
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Returns the MaterialSelector that is on the given spot in the arguments.
     * If the item is not material, or the material is not found. Null is
     * returned.
     *
     * @param key
     * @return the MaterialSelector or null
     */
    public MaterialSelector getMaterialSelector(T... key) {
        try {
            return MaterialSelector.fromString(getString(key));
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Gets the player with the name at the given index. This accepts partial
     * names.
     *
     * @param key
     * @return the player or null.
     */
    public IdpPlayer getPlayer(T... key) {
        String name = getString(key);
        return (name == null) ? null : InnPlugin.getPlugin().getPlayer(name, false);
    }

    /**
     * Gets the player with the name at the given index. This accepts partial
     * names.
     *
     * @param key
     * @return the player or null.
     */
    public IdpPlayer[] getPlayerList(T... key) {
        String[] names = getString(key).split(",");
        IdpPlayer[] players = new IdpPlayer[names.length];
        for (int i = 0; i < names.length; i++) {
            if (!names[i].isEmpty()) {
                players[i] = InnPlugin.getPlugin().getPlayer(names[i], false);
            }
        }
        return players;
    }

    /**
     * Gets the player with the name at the given index. This does not accept
     * partial names.
     *
     * @param key
     * @return the player or null.
     */
    public IdpPlayer getPlayerExact(T... key) {
        String name = getString(key);
        return (name == null) ? null : InnPlugin.getPlugin().getPlayer(name, true);
    }
    // </editor-fold>

    /**
     * Returns the argument list
     *
     * @return
     */
    public abstract String[] getArguments();

    /**
     * Returns the Argument that is at the given index
     *
     * @param key
     * @return The string at the index, or null the index >= size
     */
    public abstract String getString(T... key);

    /**
     * Amount of arguments
     *
     * @return
     */
    public abstract int size();

    /**
     * This method checks the args for the 'delimited' arguments. <br/> The
     * method accepts escaped delimiters, like -> <b>\'</b>.
     *
     * @param args
     */
    private void findDelimitedArgs(String[] args) {
        String[] newArgs = new String[args.length];
        int currarg = -1;
        boolean startTag = false;

        for (String str : args) {
            if (startTag) {
                if (str.endsWith(delimiter) && !str.endsWith("\\" + delimiter)) { // Allow Escaped Delimiters
                    startTag = false;
                    newArgs[currarg] += " "; // dont forget space!
                    if (str.length() > 2) {
                        newArgs[currarg] += str.substring(0, str.length() - 1).replace("\\" + delimiter, delimiter);
                    }
                } else {
                    newArgs[currarg] += " " + str.replace("\\" + delimiter, delimiter); // dont forget space!
                }
            } else {
                currarg++;
                if (str.startsWith(delimiter)) {
                    startTag = true;
                    newArgs[currarg] = str.substring(1).replace("\\" + delimiter, delimiter);
                } else {
                    newArgs[currarg] = str;
                }
            }
        }
        if (startTag) {
            if (newArgs[currarg].endsWith(delimiter) && !newArgs[currarg].endsWith("\\" + delimiter)) {
                // Remove the last delimiter
                newArgs[currarg] = newArgs[currarg].substring(0, newArgs[currarg].length() - 1);
            }
        }
        currarg++;
        delimitedArgs = new String[currarg];
        System.arraycopy(newArgs, 0, delimitedArgs, 0, currarg);
    }
    
}
