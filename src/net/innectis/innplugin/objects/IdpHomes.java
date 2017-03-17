package net.innectis.innplugin.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;

/**
 * A class that manages a player's homes
 *
 * @author AlphaBlend
 */
public class IdpHomes {

    private Map<Integer, IdpHome> homes = new HashMap<Integer, IdpHome>();

    public IdpHomes() {

    }

    public IdpHomes(Map<Integer, IdpHome> homes) {
        this.homes = homes;
    }

    /**
     * Adds a home by index to the list of homes
     * @param idx
     * @param home
     */
    public void addHome(int idx, IdpHome home) {
        homes.put(idx, home);
    }

    /**
     * Adds a new home with the player's ID, home ID, home name, and location
     * @param playerId
     * @param homeId
     * @param homeName
     * @param loc
     * @return true if the home was updated, false otherwise
     */
    public boolean addHome(UUID playerId, int homeId, String homeName, Location loc) {
        IdpHome home = getHome(homeName);

        // If home exists by name, update its location
        if (home != null) {
            home.setLocation(loc);
            home.save();

            return true;
        }

        home = getHome(homeId);

        // If home exists by ID, update its location
        if (home != null) {
            // Change the name of this home as well, it could be blank too
            home.setName(homeName);

            home.setLocation(loc);
            home.save();

            return true;
        }

        home = new IdpHome(playerId, homeId, homeName, loc);
        home.save();
        homes.put(homeId, home);

        return false;
    }

    /**
     * Deletes a home by its index
     * @param idx
     * @return the home object if deleted successfully
     */
    public IdpHome deleteHome(int idx) {
        IdpHome home = homes.remove(idx);

        if (home != null) {
            home.delete();
            resyncHomes();

            return home;
        } else {
            return null;
        }
    }

    /**
     * Deletes a home by its name
     * @param name
     * @return the home object if deleted successfully
     */
    public IdpHome deleteHome(String name) {
        for (Iterator<Integer> it = homes.keySet().iterator(); it.hasNext();) {
            int num = it.next();
            IdpHome home = homes.get(num);

            if (home.getName().equalsIgnoreCase(name)) {
                home.delete();
                it.remove();
                resyncHomes();

                return home;
            }
        }

        return null;
    }

    /**
     * Gets a home by its index
     * @param idx
     * @return
     */
    public IdpHome getHome(int idx) {
        return homes.get(idx);
    }

    /**
     * Gets a home by its name
     * @param name
     * @return
     */
    public IdpHome getHome(String name) {
        for (IdpHome home : homes.values()) {
            String homeName = home.getName();

            // Do not process homes with blank names
            if (homeName == null || homeName.isEmpty()) {
                continue;
            }

            if (homeName.equalsIgnoreCase(name)) {
                return home;
            }
        }

        return null;
    }

    /**
     * Gets the home count
     * @return
     */
    public int getHomeCount() {
        return homes.size();
    }

    /**
     * Returns a collection of all homes
     * @return
     */
    public Collection<IdpHome> getHomes() {
        return Collections.unmodifiableCollection(homes.values());
    }

    /**
     * Resyncs all the homes to have a contiguous index
     */
    private void resyncHomes() {
        List<IdpHome> tempHomes = new ArrayList<IdpHome>(homes.values());
        homes.clear();

        int idx = 1;

        for (IdpHome home : tempHomes) {
            home.setId(idx);
            home.save();
            homes.put(idx, home);
            idx++;
        }
    }

}
