package net.innectis.innplugin.listeners.bukkit;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * A listener for all weather-related events
 *
 * @author AlphaBlend
 */
public class BukkitWeatherListener implements Listener {

    private InnPlugin plugin;

    public BukkitWeatherListener(InnPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            IdpWorld world = IdpWorldFactory.getWorld(event.getWorld().getName());

            // Do not allow these worlds to receive weather
            switch (world.getActingWorldType()) {
                case AETHER:
                case EVENTWORLD:
                case CREATIVEWORLD:
                    event.setCancelled(true);
                    return;
            }
        }

        for (Player bukkitPlayer : event.getWorld().getPlayers()) {
            InnectisLot lot = LotHandler.getLot(bukkitPlayer.getLocation());
            if (lot != null && (lot.isFlagSet(LotFlagType.ETERNALWEATHER) || lot.isFlagSet(LotFlagType.NOWEATHER))) {
                boolean shouldRain = lot.isFlagSet(LotFlagType.ETERNALWEATHER);
                if (shouldRain != event.toWeatherState()) {
                    bukkitPlayer.setPlayerWeather(shouldRain ? WeatherType.DOWNFALL : WeatherType.CLEAR);
                }
            }
        }
    }

}
