package net.innectis.innplugin.objects.owned;

/**
 * A class that holds a tag and whether or not
 * that tag is public
 *
 * @author AlphaBlend
 */
public class LotTag {

    private String tag;
    private boolean publicTag;

    public LotTag(String tag, boolean publicTag) {
        this.tag = tag.toLowerCase();
        this.publicTag = publicTag;
    }

    /**
     * Gets the owned object tag
     * @return
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets this owned object tag
     * @param tag
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Gets if this tag is public
     * @return
     */
    public boolean isPublic() {
        return publicTag;
    }

    /**
     * Sets whether this tag is public
     * @param publicTag
     */
    public void setPublic(boolean publicTag) {
        this.publicTag = publicTag;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LotTag)) {
            return false;
        }

        LotTag innObj = (LotTag) obj;

        return innObj.getTag().equalsIgnoreCase(this.tag);
    }

    @Override
    public int hashCode() {
        return this.tag.hashCode();
    }

    /**
     * Clones this object and returns a new lot tag
     * @return
     */
    public LotTag clone() {
        return new LotTag(tag, publicTag);
    }

}
