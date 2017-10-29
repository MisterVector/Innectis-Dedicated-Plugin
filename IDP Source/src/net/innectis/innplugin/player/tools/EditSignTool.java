package net.innectis.innplugin.player.tools;

import net.innectis.innplugin.objects.EditSignWand;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisChest;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

/**
 * Tool for quickly editing signs
 *
 * @author AlphaBlend
 */
public class EditSignTool extends Tool {

    public EditSignTool(IdpPlayer player, IdpMaterial materialInHand, Block block) {
        super(player, materialInHand, block);
    }

    public static IdpMaterial getItem() {
        return IdpMaterial.PAPER;
    }

    @Override
    public boolean isAllowed() {
        return true;
    }

    @Override
    public boolean onLeftClickAir() {
        return false;
    }

    @Override
    public boolean onLeftClickBlock() {
        EditSignWand wand = player.getSession().getEditSignWand();

        // if player has nothing in their edit wand
        if (wand.isEmpty()) {
            return false;
        }

        // make sure the block clicked on is a sign
        if (!(block.getState() instanceof Sign)) {
            return false;
        }

        InnectisLot lot = LotHandler.getLot(block.getLocation(), true);

        if (lot == null || lot.canPlayerAccess(player.getName())
                || player.hasPermission(Permission.special_edit_any_sign)) {
            Sign sign = (Sign) block.getState();
            Location loc = sign.getLocation();
            Block chestBlock = loc.subtract(0, 1, 0).getBlock();

            if (chestBlock.getState() instanceof Chest) {
                InnectisChest ichest = ChestHandler.getChest(chestBlock.getLocation());

                if (ichest != null && !(player.getName().equalsIgnoreCase(ichest.getOwner())
                        || player.hasPermission(Permission.special_chestshop_override))) {
                    player.printError("Unable to paste text on a sign over a chest you don't own.");
                    return false;
                }
            }

            int changes = 0;

            for (int i = 0; i < 4; i++) {
                String line = wand.getLine(i);

                // Don't parse if the line is empty
                if (line == null) {
                    continue;
                }

                // Only parse potential sign colors if the line is not blank
                if (!line.equals("")) {
                    // Parse sign colours
                    line = ChatColor.parseSignColor(line, player);
                }

                sign.setLine(i, line);
                changes++;
            }

            if (changes > 0) {
                sign.update();
                player.printInfo("Edited the sign with the edit sign wand!");
            }
        }

        return true;
    }

    @Override
    public boolean onRightClickAir() {
        return false;
    }

    @Override
    public boolean onRightClickBlock() {
        // Make sure the block is a sign first
        if (!(block.getState() instanceof Sign)) {
            return false;
        }

        Location loc = block.getLocation();
        InnectisLot signLot = LotHandler.getLot(loc);

        // Disallow edit sign wand where not allowed
        if (signLot != null && !signLot.canPlayerAccess(player.getName())
                && !player.hasPermission(Permission.special_edit_any_sign)) {
            player.printError("Cannot use edit sign wand here!");
            return true;
        }


        EditSignWand wand = player.getSession().getEditSignWand();
        Sign sign = (Sign) block.getState();
        String[] lines = sign.getLines();

        for (int i = 0; i < lines.length; i++) {
            wand.setLine(i, lines[i]);
        }

        player.printInfo("Copied the lines of the sign to your edit sign wand!");

        return true;
    }

}
