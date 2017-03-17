package net.innectis.innplugin.objects;

import java.awt.Image;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**
 * A custom renderer that draws an image onto a map
 *
 * @author AlphaBlend
 */
public class CustomMapRenderer extends MapRenderer {

    private Image image;
    private boolean first = true;

    public CustomMapRenderer(Image image) {
        this.image = image;
    }

    @Override
    public void render(MapView mv, MapCanvas mc, Player player) {
        if (first) {
            mc.drawImage(0, 0, image);
            first = false;
        }
    }

}
