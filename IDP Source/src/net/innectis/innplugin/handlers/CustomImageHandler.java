package net.innectis.innplugin.handlers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.CustomMapRenderer;
import org.bukkit.Server;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**
 *
 * @author AlphaNlend
 */
public class CustomImageHandler {

    /**
     * Loads and initializes all custom maps
     * @param server
     * @return
     */
    public static int loadCustomMapImages(Server server) {
        int count = 0;

        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM custom_map_images;");
            set = statement.executeQuery();

            while (set.next()) {
                short id = set.getShort("map_id");
                InputStream stream = set.getBlob("image_data").getBinaryStream();
                BufferedImage image = ImageIO.read(stream);

                MapView view = server.getMap(id);

                for (MapRenderer r : view.getRenderers()) {
                    view.removeRenderer(r);
                }

                view.addRenderer(new CustomMapRenderer(image));
                count++;
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load custom map images!", ex);
        } catch (IOException ex) {
            InnPlugin.logError("Unable to load custom map images!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
            DBManager.closeResultSet(set);
        }

        return count;
    }

    /**
     * Saves a map image to the database
     * @param id
     * @param image
     */
    public static void saveMapImageToDatabase(short id, BufferedImage image, String ext) {
        PreparedStatement statement = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, ext.substring(1), baos);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

            statement = DBManager.prepareStatement("INSERT INTO custom_map_images VALUES (?, ?);");
            statement.setShort(1, id);
            statement.setBlob(2, bais);
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save map image!", ex);
        } catch (IOException ex) {
            InnPlugin.logError("Unable to save map image!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

}
