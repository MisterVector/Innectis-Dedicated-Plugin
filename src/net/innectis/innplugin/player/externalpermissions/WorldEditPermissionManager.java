package net.innectis.innplugin.player.externalpermissions;

import java.util.ArrayList;
import java.util.List;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Hret
 */
public class WorldEditPermissionManager extends PluginPermissionManager {

    @Override
    public String getPluginName() {
        return "WorldEdit";
    }

    @Override
    public boolean givePermissions(IdpPlayer player) {
        Plugin plug = getPlugin();

        if (plug == null) {
            return false;
        }

        List<String[]> permGroups = new ArrayList<String[]>(10);

        if (player.hasPermission(Permission.external_worldedit_brush)) {
            permGroups.add(getBrushPerms());
        }
        if (player.hasPermission(Permission.external_worldedit_clipboard)) {
            permGroups.add(getClipboard());
        }
        if (player.hasPermission(Permission.external_worldedit_areamanipulation)) {
            permGroups.add(getAreaManipulation());
        }
        if (player.hasPermission(Permission.external_worldedit_storing)) {
            permGroups.add(getStoring());
        }
        if (player.hasPermission(Permission.external_worldedit_generation)) {
            permGroups.add(getGeneration());
        }
        if (player.hasPermission(Permission.external_worldedit_admin)) {
            permGroups.add(getAdmin());
        }
        if (player.hasPermission(Permission.external_worldedit_analysis)) {
            permGroups.add(getAnalysis());
        }
        if (player.hasPermission(Permission.external_worldedit_superpick)) {
            permGroups.add(getSuperPick());
        }
        if (player.hasPermission(Permission.external_worldedit_navigation)) {
            permGroups.add(getNavigation());
        }
        if (player.hasPermission(Permission.external_worldedit_fixcommands)) {
            permGroups.add(getFixCommands());
        }
        if (player.hasPermission(Permission.external_worldedit_tools)) {
            permGroups.add(getTools());
        }
        if (player.hasPermission(Permission.external_worldedit_selection)) {
            permGroups.add(getSelection());
        }
        if (player.hasPermission(Permission.external_worldedit_remove)) {
            permGroups.add(getRemove());
        }

        // Check if there are any perms
        if (!permGroups.isEmpty()) {

            if (permGroups.size() == GROUPSIZE) {
                setPerm(player, plug, getAll(), true);
            } else {
                for (String[] permgroup : permGroups) {
                    for (String perm : permgroup) {
                        setPerm(player, plug, perm, true);
                    }
                }
            }

        }
        return true;
    }

    private String getAll() {
        return "worldedit.*";
    }
    /** The amount of groups the permissions has. (without all) */
    private static final int GROUPSIZE = 13;

    private String[] getBrushPerms() {
        return new String[]{
                    "worldedit.brush.clipboard",
                    "worldedit.brush.cylinder",
                    "worldedit.brush.ex",
                    "worldedit.brush.gravity",
                    "worldedit.brush.options.mask",
                    "worldedit.brush.options.material",
                    "worldedit.brush.options.range",
                    "worldedit.brush.options.size",
                    "worldedit.brush.smooth",
                    "worldedit.brush.sphere",};

    }

    private String[] getClipboard() {
        return new String[]{
                    "worldedit.history.clear",
                    "worldedit.history.redo",
                    "worldedit.history.undo",
                    "worldedit.clipboard.clear",
                    "worldedit.clipboard.copy",
                    "worldedit.clipboard.cut",
                    "worldedit.clipboard.flip",
                    "worldedit.clipboard.paste",
                    "worldedit.clipboard.rotate",
                    "worldedit.fill",
                    "worldedit.fill.recursive",};

    }

    private String[] getAreaManipulation() {
        return new String[]{
                    "worldedit.fast",
                    "worldedit.generation.cylinder",
                    "worldedit.generation.forest",
                    "worldedit.generation.pumpkins",
                    "worldedit.generation.pyramid",
                    "worldedit.generation.shape",
                    "worldedit.generation.sphere",
                    "worldedit.region.deform",
                    "worldedit.region.faces",
                    "worldedit.region.hollow",
                    "worldedit.region.move",
                    "worldedit.region.naturalize",
                    "worldedit.region.overlay",
                    "worldedit.region.replace",
                    "worldedit.region.set",
                    "worldedit.region.smooth",
                    "worldedit.region.stack",
                    "worldedit.region.walls",
                    "worldedit.green",};
    }

    private String[] getStoring() {
        return new String[]{
                    "worldedit.clipboard.load",
                    "worldedit.clipboard.save",
                    "worldedit.schematic.formats",
                    "worldedit.schematic.list",
                    "worldedit.schematic.load",
                    "worldedit.schematic.save",
                    "worldedit.snapshots.list",
                    "worldedit.snapshots.restore",};

    }

    private String[] getGeneration() {
        return new String[]{
                    "worldedit.biome",
                    "worldedit.delchunks",
                    "worldedit.regen",};

    }

    private String[] getAdmin() {
        return new String[]{
                    "worldedit.scripting.execute",
                    "worldedit.global-mask",
                    "worldedit.limit",
                    "worldedit.butcher",
                    "worldedit.reload",};
    }

    private String[] getAnalysis() {
        return new String[]{
                    "worldedit.analysis.count",
                    "worldedit.analysis.distr",
                    "worldedit.listchunks",
                    "worldedit.chunkinfo",};
    }

    private String[] getSuperPick() {
        return new String[]{
                    "worldedit.superpickaxe",
                    "worldedit.superpickaxe.area",
                    "worldedit.superpickaxe.recursive",};
    }

    private String[] getNavigation() {
        return new String[]{
                    "worldedit.navigation.ascend",
                    "worldedit.navigation.ceiling",
                    "worldedit.navigation.descend",
                    "worldedit.navigation.unstuck",
                    "worldedit.navigation.up",};
    }

    private String[] getFixCommands() {
        return new String[]{
                    "worldedit.drain",
                    "worldedit.extinguish",
                    "worldedit.fixlava",
                    "worldedit.fixwater",
                    "worldedit.snow",
                    "worldedit.thaw",};
    }

    private String[] getTools() {
        return new String[]{
                    "worldedit.tool.data-cycler",
                    "worldedit.tool.deltree",
                    "worldedit.tool.farwand",
                    "worldedit.tool.flood-fill",
                    "worldedit.tool.info",
                    "worldedit.tool.lrbuild",
                    "worldedit.tool.replacer",
                    "worldedit.tool.tree",
                    "worldedit.navigation.thru.tool",
                    "worldedit.navigation.jumpto.tool",};
    }

    private String[] getSelection() {
        return new String[]{
                    "worldedit.wand",
                    "worldedit.wand.toggle",
                    "worldedit.selection.chunk",
                    "worldedit.selection.contract",
                    "worldedit.selection.expand",
                    "worldedit.selection.hpos",
                    "worldedit.selection.inset",
                    "worldedit.selection.outset",
                    "worldedit.selection.pos",
                    "worldedit.selection.shift",
                    "worldedit.selection.size",};
    }

    private String[] getRemove() {
        return new String[]{
                    "worldedit.remove",
                    "worldedit.removeabove",
                    "worldedit.removebelow",
                    "worldedit.removenear",
                    "worldedit.replacenear",};
    }
    
}
