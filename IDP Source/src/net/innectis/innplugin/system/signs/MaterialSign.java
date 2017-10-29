package net.innectis.innplugin.system.signs;

import net.innectis.innplugin.items.IdpMaterial;

/**
 * An abstract class describing a sign with a material associated with it
 *
 * @author AlphaBlend
 */
public abstract class MaterialSign {

    private IdpMaterial material;

    public MaterialSign(IdpMaterial material) {
        this.material = material;
    }

    /**
     * Returns the material associated with this sign
     * @return
     */
    public IdpMaterial getMaterial() {
        return material;
    }

    /**
     * Gets the type of wall sign this represents
     * @return
     */
    public abstract WallSignType getType();
    
}
