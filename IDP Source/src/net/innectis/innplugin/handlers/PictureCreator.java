package net.innectis.innplugin.handlers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpMaterial;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 *
 * @author Hret
 *
 * This class allows you to load pictures into minecraft, where they are converted to closest wool colours
 */
public final class PictureCreator {

    private String[] woolColours = new String[16];
    private HashMap<String, Integer> colourCache = new HashMap<String, Integer>();

    public PictureCreator() {
        // Set colours hex's
        woolColours[0] = "FFFFFF";  // White
        woolColours[1] = "FF9900";  // Orange
        woolColours[2] = "FF33CC";  // Magneta
        woolColours[3] = "3399FF";  // Teal
        woolColours[4] = "FFFF00";  // Yellow
        woolColours[5] = "00CC00";  // Lime
        woolColours[6] = "FF9999";  // Pink
        woolColours[7] = "333333";  // Dark gray
        woolColours[8] = "AAAAAA";  // Light gray
        woolColours[9] = "336666";  // Cyan
        woolColours[10] = "660099"; // Purple
        woolColours[11] = "0000FF"; // Blue
        woolColours[12] = "663300"; // Brown
        woolColours[13] = "003300"; // Green
        woolColours[14] = "990000"; // Red
        woolColours[15] = "000000"; // Black
    }

    public void start(String file, Location location, Vector direction) throws IOException {
        File f = new File(Configuration.PATH_DATAFOLDER + File.separator + "images" + File.separator + file);

        if (!f.exists()) {
            InnPlugin.logError("FNF " + f.getAbsolutePath());
            return;
        }
        BufferedImage image = ImageIO.read(f);

        if (image.getWidth() > 250 && image.getHeight() > 250) {
            return;
        }

        int c, red, green, blue;
        StringBuilder sb;
        int max = image.getHeight();

        for (int y = 1; y <= max; y++) {
            sb = new StringBuilder();

            for (int x = 0; x < image.getWidth(); x++) {
                c = image.getRGB(x, (max - y));
                red = (c & 0x00ff0000) >> 16;
                green = (c & 0x0000ff00) >> 8;
                blue = c & 0x000000ff;

                String s = toHex(red) + toHex(green) + toHex(blue);

                sb.append(s).append(",");
            }

            setBlocks(location, getDataValues(sb.toString()), direction);
            location.setY(y);
        }
    }

    private static String toHex(int i) {
        String str = Integer.toHexString(i);
        if (str.length() == 1) {
            str = "f" + str;
        }
        return str;
    }

    private void setBlocks(Location loc, int[] types, Vector dir) {
        World world = loc.getWorld();

        for (int i = 0; i < types.length; i++) {
            loc.add(dir);
            Block block = world.getBlockAt(loc);
            BlockHandler.setBlock(block, IdpMaterial.WOOL_WHITE, (byte) types[i]);
        }

    }

    private int[] getDataValues(String str) {
        String[] hexStrings = str.split(",");
        int[] blocks = new int[hexStrings.length];

        for (int i = 0; i < hexStrings.length; i++) {
            blocks[i] = getWoolColor(hexStrings[i]);
        }

        return blocks;
    }

    /**
     * Looks for the wool colour that is the closest match to the hex string
     * @param hex
     * @return
     */
    public int getWoolColor(String hex) {
        // Check chache
        if (colourCache.containsKey(hex)) {
            return colourCache.get(hex);
        }

        int bestPick = 0; // The data value of the best pick for the colour
        double bestAvg = Integer.MAX_VALUE;
        String colourStr;
        int r1, g1, b1;
        int r2, g2, b2;
        double avg;
        r2 = Integer.parseInt(hex.substring(0, 1), 16);
        g2 = Integer.parseInt(hex.substring(2, 3), 16);
        b2 = Integer.parseInt(hex.substring(4, 5), 16);

        for (int i = 0; i < woolColours.length; i++) {
            colourStr = woolColours[i];
            r1 = Integer.parseInt(colourStr.substring(0, 1), 16);
            g1 = Integer.parseInt(colourStr.substring(2, 3), 16);
            b1 = Integer.parseInt(colourStr.substring(4, 5), 16);

            avg = ColourDistance(r1, g1, b1, r2, g2, b2);

            if (avg < bestAvg) {
                bestAvg = avg;
                bestPick = i;
            }
        }

        colourCache.put(hex, bestPick);

        return bestPick;
    }

    private double ColourDistance(int r1, int g1, int b1, int r2, int g2, int b2) {
        long rmean = (r1 + r2) / 2;
        long r = r1 - r2;
        long g = g1 - g2;
        long b = b1 - b2;
        return Math.sqrt((((512 + rmean) * r * r) >> 8) + 4 * g * g + (((767 - rmean) * b * b) >> 8));
    }

}
