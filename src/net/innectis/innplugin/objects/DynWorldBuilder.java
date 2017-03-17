package net.innectis.innplugin.objects;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.IdpRuntimeException;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.EnderChestContents.EnderContentsType;
import net.innectis.innplugin.location.IdpDynamicWorldSettings;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.location.worldgenerators.MapType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.chat.ChatInjector;
import net.innectis.innplugin.player.InventoryType;

/**
 *
 * @author Hret
 *
 * Simple class that uses the ChatInjector to create dynamic worlds.
 * <p/>
 * TODO: add comments and such....
 */
public class DynWorldBuilder extends ChatInjector {

    private BuildSteps currentstep;
    private String dataname;
    private String name;
    private MapType maptype;
    private EnderChestContents.EnderContentsType endertype;
    private InventoryType inventorytype;
    private boolean inventorycleared = false;
    private IdpWorldType worldtype;
    //
    private int worldsize = Integer.MAX_VALUE;
    private int unloadTime = 60;
    //
    private boolean noCommands = false;
    private boolean noWe = false;
    private boolean hardcoremode = false;
    private boolean noBuild = false;
    private boolean noHunger = false;
    private boolean tntAllowed = false;

    public DynWorldBuilder(String name, String dataname) {
        this.name = name;
        this.dataname = dataname;
        this.currentstep = BuildSteps.MAPTYPE;
    }

    private static enum BuildSteps {

        MAPTYPE,
        INVENTORYTYPE,
        INVENTORY_EXTRA,
        ENDERTYPE,
        WORLDTYPE,
        //
        WORLDSIZE,
        UNLOADTIME,
        MODES,
        //
        FINISHBUILDING
    }

    @Override
    public void onChat(IdpCommandSender sender, String message) {
        // Generic kill message
        if (message.equalsIgnoreCase("^c")) {
            removeInjector(sender);
            sender.printError("Stopped creating a dynamic world...");
            return;
        }

        switch (currentstep) {
            case MAPTYPE:
                setMaptype(sender, message);
                break;
            case INVENTORYTYPE:
                setInventorytype(sender, message);
                break;
            case INVENTORY_EXTRA:
                setInventorysave(sender, message);
                break;
            case ENDERTYPE:
                setEndertype(sender, message);
                break;
            case WORLDTYPE:
                setWorldtype(sender, message);
                break;
            case WORLDSIZE:
                setWorldsize(sender, message);
                break;
            case UNLOADTIME:
                setUnloadTime(sender, message);
                break;
            case MODES:
                setModes(sender, message);
                break;
        }
    }

    private void nextStep() {
        switch (currentstep) {
            case MAPTYPE:
                currentstep = BuildSteps.INVENTORYTYPE;
                break;
            case INVENTORYTYPE:
                switch (inventorytype) {
                    case MEMORY1:
                    case MEMORY2:
                    case MEMORY3:
                    case MEMORY4:
                    case MEMORY5:
                        currentstep = BuildSteps.INVENTORY_EXTRA;
                        break;
                    default:
                        currentstep = BuildSteps.ENDERTYPE;
                        break;
                }
                break;
            case INVENTORY_EXTRA:
                currentstep = BuildSteps.ENDERTYPE;
                break;
            case ENDERTYPE:
                currentstep = BuildSteps.WORLDTYPE;
                break;
            case WORLDTYPE:
                currentstep = BuildSteps.WORLDSIZE;
                break;
            case WORLDSIZE:
                currentstep = BuildSteps.UNLOADTIME;
                break;
            case UNLOADTIME:
                currentstep = BuildSteps.MODES;
                break;
            case MODES:
                currentstep = BuildSteps.FINISHBUILDING;
                break;
        }
    }

    public void printInfo(IdpCommandSender sender) {
        sender.printError("----------------------------------");
        switch (currentstep) {
            case MAPTYPE: {
                sender.printInfo("Give the MapType, allowed values:");
                String str = "";
                for (MapType type : MapType.values()) {
                    str += type.name() + ", ";
                }
                sender.printInfo(str.substring(0, str.length() - 2));
                sender.printInfo("Or type '" + ChatColor.AQUA + "X'" + ChatColor.GREEN + " for default!");
                break;
            }
            case INVENTORYTYPE: {
                sender.printInfo("Give the InventoryType, allowed values:");
                String str = "";
                for (InventoryType type : InventoryType.values()) {
                    str += type.name() + ", ";
                }
                sender.printInfo(str.substring(0, str.length() - 2));
                sender.printInfo("Or type '" + ChatColor.AQUA + "X'" + ChatColor.GREEN + " for default!");
                break;
            }
            case INVENTORY_EXTRA:
                sender.printInfo("Should the inventory be cleared? ");
                sender.printInfo("Type '" + ChatColor.AQUA + "Y'" + ChatColor.GREEN + " to clear!");
                break;
            case ENDERTYPE: {
                sender.printInfo("Give the EnderContentsType, allowed values:");
                String str = "";
                for (EnderContentsType type : EnderContentsType.values()) {
                    str += type.name() + ", ";
                }
                sender.printInfo(str.substring(0, str.length() - 2));
                sender.printInfo("Or type '" + ChatColor.AQUA + "X'" + ChatColor.GREEN + " for default!");
                break;
            }
            case WORLDTYPE: {
                sender.printInfo("Give the IdpWorldType, allowed values:");
                String str = "";
                for (IdpWorldType type : IdpWorldType.values()) {
                    str += type.name() + ", ";
                }
                sender.printInfo(str.substring(0, str.length() - 2));
                sender.printInfo("Or type '" + ChatColor.AQUA + "X'" + ChatColor.GREEN + " for default!");
                break;
            }
            case WORLDSIZE:
                sender.printInfo("Give the size of the world or '" + ChatColor.AQUA + "X'" + ChatColor.GREEN + " for default");
                break;
            case UNLOADTIME:
                sender.printInfo("Give the time the world must be inactive in order to get unloaded");
                sender.printInfo("Or '" + ChatColor.AQUA + "X'" + ChatColor.GREEN + " for default");
                break;
            case MODES:
                sender.printInfo("Supply the modes (ncnw => nocmd and nowe):");
                sender.printInfo(ChatColor.AQUA + "nc", ": No commands");
                sender.printInfo(ChatColor.AQUA + "nw", ": No TinyWE");
                sender.printInfo(ChatColor.AQUA + "hq", ": No Hardcode mode (no respawn)");
                sender.printInfo(ChatColor.AQUA + "nb", ": No build");
                sender.printInfo(ChatColor.AQUA + "nh", ": No hunger");
                sender.printInfo(ChatColor.AQUA + "td", ": Allow TNT to do damage");

                sender.printInfo("Or " + ChatColor.AQUA + "x", " for default");
                break;
            case FINISHBUILDING:
                loadWorld(sender);
                break;
        }
    }

    private void setInventorysave(IdpCommandSender sender, String message) {
        switch (inventorytype) {
            case MEMORY1:
            case MEMORY2:
            case MEMORY3:
            case MEMORY4:
            case MEMORY5:
                inventorycleared = message.equalsIgnoreCase("Y");
                break;
        }

        if (inventorycleared) {
            PreparedStatement statement = null;

            try {
                statement = DBManager.prepareStatement(" DELETE FROM player_inventory WHERE inventorytype = ? ");

                statement.setInt(1, inventorytype.getId());
                statement.executeUpdate();
                sender.printInfo("Inventories for '" + inventorytype + "' are cleared!");
            } catch (SQLException ex) {
                InnPlugin.logError("Error on clearing inventories!", ex);
                sender.printError("Cannot clear inventories!");
            } finally {
                DBManager.closePreparedStatement(statement);
            }
        }

        nextStep();
        printInfo(sender);
    }

    private void setInventorytype(IdpCommandSender sender, String message) {
        if (message.equalsIgnoreCase("x")) {
            inventorytype = InventoryType.MEMORY1;
        } else {
            try {
                inventorytype = InventoryType.valueOf(message.toUpperCase());
            } catch (IllegalArgumentException iae) {
                sender.printError("Invalid inventory type!");
                return;
            }
        }
        nextStep();
        printInfo(sender);
    }

    private void setMaptype(IdpCommandSender sender, String message) {
        if (message.equalsIgnoreCase("x")) {
            maptype = MapType.DEFAULT;
        } else {
            try {
                maptype = MapType.valueOf(message.toUpperCase());
            } catch (IllegalArgumentException iae) {
                sender.printError("Map type invalid!");
                return;
            }
        }
        nextStep();
        printInfo(sender);
    }

    private void setEndertype(IdpCommandSender sender, String message) {
        if (message.equalsIgnoreCase("x")) {
            endertype = EnderChestContents.EnderContentsType.NONE;
        } else {
            try {
                endertype = EnderChestContents.EnderContentsType.valueOf(message.toUpperCase());
            } catch (IllegalArgumentException iae) {
                sender.printError("Invalid enderchest type!");
                return;
            }
        }
        nextStep();
        printInfo(sender);
    }

    private void setWorldtype(IdpCommandSender sender, String message) {
        if (message.equalsIgnoreCase("x")) {
            worldtype = IdpWorldType.DYNAMIC;
        } else {
            try {
                worldtype = IdpWorldType.valueOf(message.toUpperCase());
            } catch (IllegalArgumentException iae) {
                sender.printError("Worldtype invalid!");
                return;
            }
        }
        nextStep();
        printInfo(sender);
    }

    private void setWorldsize(IdpCommandSender sender, String message) {
        if (message.equalsIgnoreCase("x")) {
            worldsize = Integer.MAX_VALUE;
        } else {
            try {
                int size = Integer.parseInt(message);
                if (size < 100) {
                    sender.printError("Minimum size is 100.");
                    return;
                }
                worldsize = size;
            } catch (NumberFormatException nfe) {
                sender.printError("Unknown size!");
                return;
            }
        }
        nextStep();
        printInfo(sender);
    }

    private void setUnloadTime(IdpCommandSender sender, String message) {
        if (message.equalsIgnoreCase("x")) {
            unloadTime = 60;
        } else {
            try {
                int size = Integer.parseInt(message);
                if (size < 10) {
                    sender.printError("Minimum duration is 10.");
                    return;
                }
                unloadTime = size;
            } catch (NumberFormatException nfe) {
                sender.printError("Unknown time string!");
                return;
            }
        }
        nextStep();
        printInfo(sender);
    }

    private void setModes(IdpCommandSender sender, String message) {
        message = message.toLowerCase();

        noCommands = message.contains("nc");
        noWe = message.contains("nw");
        hardcoremode = message.contains("hq");
        noBuild = message.contains("nb");
        noHunger = message.contains("nh");
        tntAllowed = message.contains("td");

        nextStep();
        printInfo(sender);
    }

    private void loadWorld(IdpCommandSender sender) {
        // Remove the injector
        removeInjector(sender);

        IdpDynamicWorldSettings settings = new IdpDynamicWorldSettings(
                name, maptype, inventorytype, endertype, worldsize, unloadTime, worldtype,
                !noCommands, !noWe, hardcoremode, !noBuild, !noHunger, tntAllowed);
        try {
            sender.printInfo("Loading world....");
            IdpWorldFactory.registerDynamicWorld(dataname, settings);

            // Print settings to player
            sender.printInfo("World '" + ChatColor.AQUA + dataname, "' loaded!");
            sender.printInfo("Map type: " + ChatColor.AQUA + maptype);
            sender.printInfo("Inventory type: " + ChatColor.AQUA + inventorytype);
            sender.printInfo("Ender type: " + ChatColor.AQUA + endertype);
            sender.printInfo("Worldsize: " + ChatColor.AQUA + worldsize);
            sender.printInfo("Unload time: " + ChatColor.AQUA + unloadTime);
            sender.printInfo(" ---< SETTINGS >--- ");
            sender.printInfo("Commands: " + getOnOffmsg(!noCommands));
            sender.printInfo("TinyWE: " + getOnOffmsg(!noWe));
            sender.printInfo("Harcore: " + getOnOffmsg(hardcoremode));
            sender.printInfo("Building: " + getOnOffmsg(!noBuild));
            sender.printInfo("Hunger: " + getOnOffmsg(!noHunger));
            sender.printInfo("TNT autodetonate: " + getOnOffmsg(tntAllowed));

            if (hardcoremode) {
                sender.printError("Hardcore mode not yet supported");
            }

        } catch (IdpRuntimeException ex) {
            sender.printError("Could not register world: " + ex.getMessage());
        }
    }

    private static String getOnOffmsg(boolean ison) {
        return ison ? ChatColor.AQUA + "ON" : ChatColor.RED + "OFF";
    }

}
