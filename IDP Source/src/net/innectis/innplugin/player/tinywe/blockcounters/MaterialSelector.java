package net.innectis.innplugin.player.tinywe.blockcounters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.innectis.innplugin.items.IdpMaterial;

/**
 *
 * @author Hret
 *
 *
 * Material to set a selection of materials for easy checking in TinyWe
 *
 */
public final class MaterialSelector {

    private List<IdpMaterial> materialList;
    private boolean excludeMode;

    public MaterialSelector() {
        materialList = new ArrayList<IdpMaterial>();
        excludeMode = false;
    }

    /**
     * Set the mode of the list<br />
     * true - means it will exclude material from the list<br />
     * false - means it will only include material from the list
     * @param excludeList
     */
    public void setMode(boolean mode) {
        excludeMode = mode;
    }

    /**
     * Shows the mode of the selector<br />
     * true - means it will exclude material from the list<br />
     * false - means it will only include material from the list
     * @return
     */
    public boolean isExcludeMode() {
        return excludeMode;
    }

    /**
     * Returns the list
     * @return List of materials that are included or excluded
     */
    public List<IdpMaterial> getMaterials() {
        return materialList;
    }

    /** *
     * Adds the given materials to the list
     * @param materials
     */
    public void addMaterials(IdpMaterial... materials) {
        materialList.addAll(Arrays.asList(materials));
    }

    /**
     * Checks if the given material is selected for the selector
     * @param targetMat
     * @return
     */
    public boolean materialSelected(IdpMaterial targetMat) {
        boolean match = false;
        for (IdpMaterial mat : materialList) {
            if (mat == targetMat) {
                match = true;
                break;
            }
        }
        return ((!match && excludeMode) || (match && !excludeMode));
    }

    /**
     * Constructs a materialselector from a string.<br />
     * To make a material selector to use exclude mode put an '<b>!</b>' in front of it.<br />
     * The materials are in the normal prefix delimited by '<b>;</b>' or '<b>,</b>' <br />
     * Example: <b>1;17:2</b> -> stone and birch logs<br />
     * Example: <b>!1;17:2</b> -> evetything except stone and birch logs<br />
     * @param source
     * @return
     */
    public static MaterialSelector fromString(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        MaterialSelector selector = new MaterialSelector();
        if (source.startsWith("!")) {
            selector.setMode(true);
            source = source.substring(1);
        }
        IdpMaterial mat;
        String delimiter = source.contains(";") ? ";" : ",";
        for (String str : source.split(delimiter)) {
            mat = IdpMaterial.fromString(str);
            if (mat != null) {
                selector.addMaterials(mat);
            }
        }
        return selector;
    }

    @Override
    public String toString() {
        if (materialList == null) {
            return "Empty MaterialSelector";
        }
        String str = "MaterialSelector(";
        for (IdpMaterial mat : materialList) {
            str += mat.toString() + ",";
        }
        str = str.substring(0, str.length() - 2);
        return str + ")";
    }

}