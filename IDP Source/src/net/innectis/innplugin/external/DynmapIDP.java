package net.innectis.innplugin.external;

import java.util.HashMap;
import java.util.Map;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.PlayerGroup;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

/**
 * API for dynmap functionality
 *
 * @author Lynxy
 */
public class DynmapIDP {

    private final String INFOWINDOW = "<div class=\"infowindow\">	<span style=\"font-size:125%;font-weight:bold;\">%codeplaceholder%</span><br /> Lot #<span style=\"font-weight:bold;\">%id%</span> %name%<br />	Size: %size%<br />	Members: %members%<br />	Flags: %flags%<br />	Last owner edit: %lastowneredit%<br />    Last member edit: %lastmemberedit%</div>";
    private InnPlugin plugin;
    private DynmapAPI dynmapPlugin;
    private MarkerAPI markerapi;
    private MarkerSet set;
    private Map<String, AreaMarker> resareas = new HashMap<String, AreaMarker>();

    public DynmapIDP(InnPlugin plugin, Plugin bukkitPlugin) {
        this.plugin = plugin;
        dynmapPlugin = (DynmapAPI) bukkitPlugin;
    }

    public boolean setupDynmap() {
        markerapi = dynmapPlugin.getMarkerAPI();

        if (markerapi == null) {
            plugin.logError("Error loading dynmap marker API!");
            return false;
        }

        set = markerapi.getMarkerSet("idp.markerset");

        if (set == null) {
            set = markerapi.createMarkerSet("idp.markerset", "Lots", null, false);
        } else {
            set.setMarkerSetLabel("Lots");
        }

        if (set == null) {
            plugin.logError("Error creating marker set");
            return false;
        }

        set.setLayerPriority(15);
        set.setHideByDefault(false);

        // Add the task
        plugin.getTaskManager().addTask(new DynmapUpdateTask(plugin, this));
        return true;
    }

    public void update() {
        Map<String, AreaMarker> newmap = new HashMap<String, AreaMarker>(); /* Build new map */
        Map<String, Marker> newmark = new HashMap<String, Marker>(); /* Build new map */
        handleLotRecursive(LotHandler.getMainLot(), newmap, newmark);

        /* Now, review old map - anything left is gone */
        for(AreaMarker oldm : resareas.values()) {
            oldm.deleteMarker();
        }
        /* And replace with new map */
        resareas = newmap;
    }

    private void handleLotRecursive(InnectisLot lot, Map<String, AreaMarker> newmap, Map<String, Marker> newmark) {
        handleLot(lot, newmap, newmark);
        for (InnectisLot sublot : lot.getSublots()) {
            handleLotRecursive(sublot, newmap, newmark);
        }
    }

    private void handleLot(InnectisLot lot, Map<String, AreaMarker> newmap, Map<String, Marker> newmark) {
        if (lot.getHidden()) {
            return;
        }

        double x[] = new double[2];
        double z[] = new double[2];

        x[0] = lot.getLowestX();
        x[1] = lot.getHighestX();

        z[0] = lot.getLowestZ();
        z[1] = lot.getHighestZ();

        String ownercolor = PlayerGroup.getGroupOfPlayerById(lot.getOwnerCredentials().getUniqueId()).getPrefix().getTextColor().getHTMLColor();
        String desc = formatInfoWindow(lot, ownercolor);
        String markerid = "lot_" + lot.getId();
        AreaMarker m = resareas.remove(markerid); /* Existing area? */
        if(m == null) {
            m = set.createAreaMarker(markerid, lot.getOwner(), false, lot.getWorld().getName(), x, z, false);
            if(m == null) {
                return;
            }
        } else {
            m.setCornerLocations(x, z); /* Replace corner locations */
            m.setLabel(lot.getOwner());   /* Update label */
        }
        //if(use3d) { /* If 3D? */
        //    m.setRangeY(l1.getY()+1.0, l0.getY());
        //}
        m.setDescription(desc); /* Set popup */

        /* Set line and fill properties */
        m.setLineStyle(3, 0.7, Integer.parseInt(ownercolor, 16)); //0x4444FF
        m.setFillStyle(0.2, lot.isFlagSet(LotFlagType.PVP) ? 0xed1c24 : 0x6dcff6);

        /* Add to map */
        newmap.put(markerid, m);
    }

    private String formatInfoWindow(InnectisLot lot, String ownercolor) {
        String v = "<div class=\"regioninfo\">" + INFOWINDOW + "</div>";

        // Handle assignable lots differently
        if (lot.isAssignable()) {
            v = v.replace("%codeplaceholder%", "<span style=\"color:black;\">Assignable Lot</span>");
        } else {
            String lotNumber = lot.getLotNumber() == 1 ? "1st" : (lot.getLotNumber() == 2 ? "2nd" : (lot.getLotNumber() == 3 ? "3rd" : lot.getLotNumber() + "th"));

            v = v.replace("%codeplaceholder%", "<span style=\"color:" + ownercolor + ";\">" + lot.getOwner() + "</span>'s " + lotNumber + " Lot");
        }

        v = v.replace("%id%", lot.getId() + "");
        v = v.replace("%name%", lot.getLotName().equalsIgnoreCase("") ? "" : "(" + lot.getLotName() + ")");
        v = v.replace("%size%", (Math.abs(lot.getHighestX()- lot.getLowestX()) + 1) + "x" + (Math.abs(lot.getHighestZ()- lot.getLowestZ()) + 1));
        v = v.replace("%members%", lot.getMembersString(null, null, null, null));
        v = v.replace("%flags%", lot.getFlagsString(null, null));
        v = v.replace("%lastowneredit%", lot.getLastOwnerEditString());
        v = v.replace("%lastmemberedit%", lot.getLastMemberEditString());
        return v;
    }

}
