package net.innectis.innplugin.player.tools;

import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerSettings;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;

/**
 *
 * @author Hret
 */
public abstract class Tool {

    protected Block block;
    protected IdpPlayer player;
    protected IdpMaterial materialInHand;

    public Tool(IdpPlayer player, IdpMaterial materialInHand, Block block) {
        this.block = block;
        this.materialInHand = materialInHand;
        this.player = player;
    }

    public abstract boolean isAllowed();

    public boolean doAction(Action action) {
        switch (action) {
            case LEFT_CLICK_AIR:
                return onLeftClickAir();
            case RIGHT_CLICK_AIR:
                return onRightClickAir();
            case LEFT_CLICK_BLOCK:
                return onLeftClickBlock();
            case RIGHT_CLICK_BLOCK:
                return onRightClickBlock();
        }
        return false;
    }

    public abstract boolean onLeftClickBlock();

    public abstract boolean onRightClickBlock();

    public abstract boolean onLeftClickAir();

    public abstract boolean onRightClickAir();

    public static Tool getTool(IdpPlayer player, IdpMaterial materialInHand, Block block) {
        if (materialInHand == WEAxeTool.getItem()
                && player.getSession().hasSetting(PlayerSettings.TWWAND)) {
            return new WEAxeTool(player, materialInHand, block);
        } else if (materialInHand == InformationTool.getItem()) {
            return new InformationTool(player, materialInHand, block);
        } else if (materialInHand == TeleportTool.getItem()) {
            return new TeleportTool(player, materialInHand, block);
        } else if (materialInHand == EditSignTool.getItem()) {
            return new EditSignTool(player, materialInHand, block);
        } else if (materialInHand == MiningStickTool.getItem()) {
            return new MiningStickTool(player, materialInHand, block);
        }

        return null;
    }

}
