package net.innectis.innplugin.util;

/**
 *
 * @author Hret
 *
 * This is an handler object to handle smart arguments
 * Regulare space-seperated arguments also works, but when put between ' it will make that an single arguement with spaces.
 * It also allows easy access to objects that could be constructed from the arguments
 */
public class SmartArguments extends Arguments<Integer> {

    public SmartArguments(String[] args) {
        super(args);
    }

    /**
     * Gets the array smart of arguments
     */
    @Override
    public String[] getArguments() {
        return getDelimitedArgs();
    }

    /**
     * Amount of arguments
     * @return
     */
    @Override
    public int size() {
        return getDelimitedArgs().length;
    }

    /**
     * Returns the string on the given index (zero-based)
     * @param key
     * @return
     */
    @Override
    public String getString(Integer... key) {
        for (int i : key) {
            if (i < 0 || i >= size()) {
                continue;
            }
            return getDelimitedArgs()[i];
        }
        return null;
    }

    /**
     * Returns all of the strings combined as a string.
     * They will be sperated by spaces
     * @param initialIndex
     * @return String matching the arguements combined
     */
    public String getJoinedStrings(int initialIndex) {
        return StringUtil.joinString(getArguments(), " ", initialIndex);
    }
    
}
