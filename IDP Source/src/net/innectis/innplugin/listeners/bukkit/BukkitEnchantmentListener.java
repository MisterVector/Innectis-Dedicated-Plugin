package net.innectis.innplugin.listeners.bukkit;

import java.util.Random;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.specialitem.SpecialItemType;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.listeners.InnBukkitListener;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

/**
 *
 * @author AlphaBlend
 */
public class BukkitEnchantmentListener implements InnBukkitListener {

    private static final int PARTY_BOW_CHANCE = 100;
    private static final int PARTY_BOW_EXP_COST = 30;
    private InnPlugin plugin;
    private Random random = new Random();

    public BukkitEnchantmentListener(InnPlugin instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPreEnchantItem(PrepareItemEnchantEvent event) {
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEnchantItem(EnchantItemEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getEnchanter());

        if (!player.getSession().isLoggedIn()) {
            event.setCancelled(true);
            return;
        }
        IdpItemStack stack = IdpItemStack.fromBukkitItemStack(event.getItem());

        // Random chance on level 30 enchant to apply a special IDP effect to bows.
        if (stack.getMaterial() == IdpMaterial.BOW && event.getExpLevelCost() == PARTY_BOW_EXP_COST) {
            if (random.nextInt(PARTY_BOW_CHANCE) == 1) {
                stack.getItemdata().addLore(ChatColor.GRAY + "Party I");
                stack.getItemdata().setSpecialItem(SpecialItemType.PARTY_BOW);
                event.getItem().setItemMeta(stack.toBukkitItemstack().getItemMeta());
            }
        }
    }

}
