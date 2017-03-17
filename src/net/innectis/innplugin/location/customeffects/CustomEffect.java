package net.innectis.innplugin.location.customeffects;

import org.bukkit.Location;

/**
 *
 * @author Nosliw
 */
public abstract class CustomEffect extends Location {

    public CustomEffect(Location location) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public abstract void execute();

}
