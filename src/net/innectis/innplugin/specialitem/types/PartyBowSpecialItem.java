package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;

/**
 * A special item that is used on a bow
 * that fires rockets
 *
 * @author Nosliw
 */
public class PartyBowSpecialItem extends AbstractSpecialItem {

    @Override
    public void addLoreName(IdpItemStack itemstack) {
        // Make this look like an enchanment!
        itemstack.getItemdata().addLore(ChatColor.GRAY + "Party I");
    }

    @Override
    public boolean canApplyTo(IdpItemStack stack) {
        switch (stack.getMaterial()) {
            case BOW:
                return true;
        }

        return false;
    }

    @Override
    public boolean onBowShoot(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Entity projectile) {
        if (item.getMaterial() == IdpMaterial.BOW) {
            plugin.getTaskManager().addTask(new PartyBowTask(projectile, 10));
        }

        return false;
    }

    private class PartyBowTask extends LimitedTask {
        private Arrow arrow;

        public PartyBowTask(Entity arrow, int amount) {
            super(RunBehaviour.SYNCED, 100, amount);
            this.arrow = (Arrow) arrow;
        }

        public void run() {
            if (!arrow.isDead()) {
                BlockHandler.launchRandomFirework(arrow.getLocation());
            }
        }
    }

}
