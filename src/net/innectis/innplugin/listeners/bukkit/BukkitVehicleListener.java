package net.innectis.innplugin.listeners.bukkit;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.listeners.InnBukkitListener;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.handlers.OwnedEntityHandler;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

/**
 *
 * @author AlphaBlend
 */
public class BukkitVehicleListener implements InnBukkitListener {

    private InnPlugin plugin;

    public BukkitVehicleListener(InnPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        Vehicle vehicle = event.getVehicle();
        OwnedEntityHandler.removeOwnedEntity(vehicle.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleDamageEvent(VehicleDamageEvent event) {
        if (event.getAttacker() instanceof Player) {
            IdpPlayer player = plugin.getPlayer((Player) event.getAttacker());
            InnectisLot lot = LotHandler.getLot(event.getVehicle().getLocation(), true);

            if (lot != null && lot.isFlagSet(LotFlagType.RESTRICTVEHICLES)
                    && !lot.containsMember(player.getName())
                    && !player.hasPermission(Permission.entity_vehicle_damage_override)) {
                player.printError("This vehicle remains sturdy!");
                event.setCancelled(true);
            }
        }
    }

}
