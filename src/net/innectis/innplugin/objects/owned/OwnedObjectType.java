package net.innectis.innplugin.objects.owned;

/**
 * An Enum listing the IDs of each owned
 * object type
 *
 * @author AlphaBlend
 */
public enum OwnedObjectType {

    BOOKCASE("bookcase"),
    CHEST("chest"),
    DOOR("door"),
    LOT("lot"),
    SWITCH("switch"),
    TRAPDOOR("trapdoor"),
    WAYPOINT("waypoint");

    private String typeName;

    private OwnedObjectType(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Gets the name of this owned object type
     * @return
     */
    public String getName() {
        return typeName;
    }
    
}
