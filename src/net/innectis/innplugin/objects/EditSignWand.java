
package net.innectis.innplugin.objects;

import java.util.HashMap;

/**
 * Holds the line contents for a single edit sign wand
 *
 * @author AlphaBlend
 */
public class EditSignWand {

    private HashMap<Integer, String> lines = new HashMap<Integer, String>();

    public EditSignWand() {}

    /**
     * Sets up the line contents of this edit sign wand
     * @param lineNo
     * @param line
     */
    public void setLine(int lineNo, String line) {
        lines.put(lineNo, line);
    }

    /**
     * Gets the text by line number
     *
     * @param lineNo
     * @return
     */
    public String getLine(int lineNo) {
        return lines.get(lineNo);
    }

    /**
     * Gets the size of this edit sign wand
     * @return
     */
    public int size() {
        return lines.size();
    }

    /**
     * Gets if this wand is empty
     * @return
     */
    public boolean isEmpty() {
        return size() == 0;
    }

}
