package net.innectis.innplugin.player.tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.objects.pojo.ChestLog;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.owned.InnectisChest;
import net.innectis.innplugin.objects.pojo.BlockLog;
import net.innectis.innplugin.location.data.DBValueTracker;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.util.DateUtil;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

/**
 *
 * @author Hret
 */
public class InformationTool extends Tool {

    public InformationTool(IdpPlayer player, IdpMaterial materialInHand, Block block) {
        super(player, materialInHand, block);
    }

    public static IdpMaterial getItem() {
        return IdpMaterial.WOOD_SWORD;
    }

    @Override
    public boolean isAllowed() {
        return materialInHand == getItem() && player.hasPermission(Permission.admin_informationtool);
    }

    @Override
    public boolean onLeftClickBlock() {
        switch (player.getSession().getInformationToolType()) {
            case BlockInformation:
                if (player.hasPermission(Permission.admin_blockinformation)) {
                    showBlockInfo();
        } else {
                    player.printError("That's classified!");
        }
                break;
            case MultiBlockInformation:
                if (player.hasPermission(Permission.admin_blockinformation)) {
                    showMultiBlockInfo();
        } else {
                    player.printError("That's classified!");
        }
                break;
            case GroupBlockInformation:
                if (player.hasPermission(Permission.admin_blockinformation)) {
                    showGroupBlockInfo();
        } else {
                    player.printError("That's classified!");
        }
                break;
            case ChestInformation:
                if (block.getState() instanceof Chest) {
                    if (player.hasPermission(Permission.admin_chestinformation)) {
                        showChestInfo();
                    } else {
                        player.printError("That's classified!");
                    }
                    break;
                } else {
                    return false;
        }
            case BlockValues:
                if (player.hasPermission(Permission.admin_blockvalueinformation)) {
                    showBlockValues();
        } else {
                    player.printError("That's classified!");
        }
                break;
            default:
                player.printError("No Information Type selected. Cycle with right click.");
                break;
        }
        return true;

    }

    @Override
    public boolean onRightClickBlock() {
        switch (player.getSession().getInformationToolType()) {
            case BlockInformation:
                player.getSession().setInformationToolType(InformationToolType.MultiBlockInformation);
                player.print(ChatColor.AQUA, "Multiple block logs will be shown with the information tool.");
                break;
            case MultiBlockInformation:
                player.getSession().setInformationToolType(InformationToolType.GroupBlockInformation);
                player.print(ChatColor.AQUA, "Group block logs will be shown with the information tool.");
                break;
            case GroupBlockInformation:
                player.getSession().setInformationToolType(InformationToolType.ChestInformation);
                player.print(ChatColor.AQUA, "Chest logs will be shown with the information tool.");
                break;
            case ChestInformation:
                player.getSession().setInformationToolType(InformationToolType.BlockValues);
                player.print(ChatColor.AQUA, "Block values will be shown with the information tool.");
                return false;
            case BlockValues:
            default:
                player.getSession().setInformationToolType(InformationToolType.BlockInformation);
                player.print(ChatColor.AQUA, "Block logs will be shown with the information tool.");
                break;
        }
        return true;
    }

    @Override
    public boolean onLeftClickAir() {
        return false;
    }

    @Override
    public boolean onRightClickAir() {
        return onRightClickBlock();
    }

    private void showBlockInfo() {
        List<BlockLog> logs = BlockHandler.getBlockChangeLogs(block.getLocation(), 10);

        if (logs != null) {
            if (logs.size() > 0) {
                player.printInfo("Printing last block changes:");
                for (BlockLog log : logs) {
                    player.printInfo(log.getId() + ":" + log.getData()
                            + " - " + log.getUsername()
                            + " - " + log.getAction().toString()
                            + " - " + DateUtil.formatString(log.getDatetime(), DateUtil.FORMAT_FULL_DATE_TIME));
                }
            } else {
                player.printInfo("Nobody has changed this block");
            }
        } else {
            player.printError("An internal error has occured!");
        }
    }

    private void showMultiBlockInfo() {
        List<BlockLog> logs;
        int range = 1;
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {

                    if (x == 0 && y == 0 && z == 0) {
                        player.printInfo(ChatColor.BLUE + "Printing last target block changes:");
                    } else {
                        player.printInfoFormat(ChatColor.AQUA + "Printing last relative ({0},{1},{2} block changes:", x, y, z);
                    }
                    logs = BlockHandler.getBlockChangeLogs(block.getLocation(), 3);

                    if (logs != null) {
                        if (logs.size() > 0) {
                            for (BlockLog log : logs) {
                                player.printInfoFormat("{0}:{1} - {2} - {3} - {4}",
                                        log.getId(),
                                        log.getData(),
                                        log.getUsername(),
                                        log.getAction().toString(),
                                        DateUtil.formatString(log.getDatetime(), DateUtil.FORMAT_FULL_DATE_TIME));
                            }
                        } else {
                            player.printInfo("Nobody has changed this block");
                        }

                    } else {

                        player.printError("An internal error has occured!");
                    }


                }
            }
        }
    }

    private void showGroupBlockInfo() {
        List<BlockLog> logs;
        int range = 2;
        List<BlockLog> fullLogs = new ArrayList<BlockLog>();
        boolean canbeadded = true;

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    logs = BlockHandler.getBlockChangeLogs(block.getLocation(), 3);
                    if (logs != null && logs.size() > 0) {
                        for (BlockLog cblog : logs) {
                            canbeadded = true;
                            for (BlockLog flog : fullLogs) {
                                if (cblog.getUsername().equals(flog.getUsername())) {
                                    if (cblog.getDatetime().getTime() < flog.getDatetime().getTime()) {
                                        fullLogs.remove(flog);
                                    } else {
                                        canbeadded = false;
                                    }
                                    break;
                                } else {
                                    canbeadded = true;
                                }
                            }
                            if (canbeadded) {
                                fullLogs.add(cblog);
                            }
                        }
                    }
                }
            }
        }
        logs = null;

        if (fullLogs.size() > 0) {
            player.printInfoFormat("Printing last block change (per player) in area ({0}x{0}):", range);
            BlockLog log;
            for (int i = 0; i < Math.min(10, fullLogs.size()); i++) {
                log = fullLogs.get(i);
                player.printInfoFormat("{0}:{1} - {2} - {3} - {4}",
                        log.getId(),
                        log.getData(),
                        log.getUsername(),
                        log.getAction().toString(),
                        DateUtil.formatString(log.getDatetime(), DateUtil.FORMAT_FULL_DATE_TIME));
            }
        } else {
            player.printInfo("Nobody has changed these blocks");
        }
    }

    private void showChestInfo() {
        InnectisChest chest = ChestHandler.getChest(block.getLocation());
        if (chest == null) {
            player.printError("An internal error has occured! Chest not found!");
            return;
        }
        List<ChestLog> logs = chest.getAccessLogs(10);
        if (logs != null) {
            if (logs.size() > 0) {
                player.printInfo("Printing last chest access:");
                for (ChestLog log : logs) {
                    player.printInfo(log.getLogid()
                            + " - " + log.getUsername()
                            + " - " + DateUtil.formatString(log.getDate(), DateUtil.FORMAT_FULL_DATE_TIME));
                }
            } else {
                player.printInfo("Nobody has opened this chest");
            }
        } else {
            player.printError("An internal error has occured!");
        }
    }

    private void showBlockValues() {
        try {
            IdpBlockData data = BlockHandler.getIdpBlockData(block.getLocation());

            // Get the values map in a hacky way.
            // This to prevent opening the field up.
            Field valuesField = data.getClass().getDeclaredField("_values");
            valuesField.setAccessible(true);
            List<DBValueTracker<String, String>> values = (List<DBValueTracker<String, String>>) valuesField.get(data);

            player.printInfo("Printing out block data values:");
            boolean hasvalue = false;
            for (DBValueTracker tracker : values) {
                hasvalue = true;
                player.printInfo(tracker.getKey() + " - " + tracker.getValue());
            }

            if (!hasvalue) {
                player.printInfo("Block has no values..");
            }

            return;

        } catch (IllegalArgumentException ex) {
            InnPlugin.logError("Error getting blockvalues " + ex.getLocalizedMessage(), ex);
        } catch (IllegalAccessException ex) {
            InnPlugin.logError("Error getting blockvalues " + ex.getLocalizedMessage(), ex);
        } catch (NoSuchFieldException ex) {
            InnPlugin.logError("Error getting blockvalues " + ex.getLocalizedMessage(), ex);
        } catch (SecurityException ex) {
            InnPlugin.logError("Error getting blockvalues " + ex.getLocalizedMessage(), ex);
        }
        player.printError("An internal error has occured!");
    }

    /** Enum to specify what the information tool should list */
    public enum InformationToolType {

        MultiBlockInformation,
        GroupBlockInformation,
        BlockInformation,
        ChestInformation,
        BlockValues
    }

}
