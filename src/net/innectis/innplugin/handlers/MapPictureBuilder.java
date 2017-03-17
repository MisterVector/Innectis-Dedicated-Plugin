package net.innectis.innplugin.handlers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.innectis.innplugin.objects.CustomMapRenderer;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**
 * A class that can construct an image out of map items.
 * The image is created from the top-left down to the
 * bottom-right corner of where it was created
 *
 * @author AlphaBlend
 */
public class MapPictureBuilder {

    private Server server;
    private World world;
    private BufferedImage image;
    private Block originBlock;
    private BlockFace frameFacingFace;

    public MapPictureBuilder(Server server, World world, BufferedImage image, Block originBlock, BlockFace facing) {
        this.server = server;
        this.world = world;
        this.image = image;
        this.originBlock = originBlock;
        this.frameFacingFace = facing;
    }

    /**
     * Attempts to create an image based on the source image
     * @param ext
     * @return null if successful, otherwise contains an error message
     */
    public String createMapImages(String ext) {
        int height = image.getHeight();
        int width = image.getWidth();

        // This boolean represents if the picture is not a multiple of 128x128
        boolean pictureOddSize = false;

        int heightOffset = 0;
        int widthOffset = 0;

        int heightBlocks = (height / 128);

        if (height % 128 > 0) {
            heightBlocks++;
            heightOffset = ((heightBlocks * 128) - height) / 2;
            pictureOddSize = true;
        }

        int widthBlocks = (width / 128);

        if (width % 128 > 0) {
            widthBlocks++;
            widthOffset = ((widthBlocks * 128) - width) / 2;
            pictureOddSize = true;
        }

        Block[][] blocks = getBlockPath(heightBlocks, widthBlocks);

        // Not a proper block path, so return null
        if (blocks == null) {
            return "The image requires a " + widthBlocks + "x" + heightBlocks + " area.";
        }

        // This picture is not a perfect multiple of 128x128 so
        // center this image so that it is a multiple of 128x128
        if (pictureOddSize) {
            BufferedImage tempImage = image;
            image = new BufferedImage(widthBlocks * 128, heightBlocks * 128, tempImage.getType());
            Graphics2D g = image.createGraphics();
            g.drawImage(tempImage, widthOffset, heightOffset, null);
            g.dispose();
        }

        BufferedImage[][] images = getSplitImages(heightBlocks, widthBlocks);

        // Create the image from all the maps
        for (int i = 0; i < heightBlocks; i++) {
            for (int j = 0; j < widthBlocks; j++) {
                BufferedImage tempImage = images[i][j];
                MapView view = server.createMap(world);

                for (MapRenderer r : view.getRenderers()) {
                    view.removeRenderer(r);
                }

                view.addRenderer(new CustomMapRenderer(tempImage));

                Block block = blocks[i][j];
                short id = view.getId();

                ItemFrame itemFrame = (ItemFrame) world.spawnEntity(block.getLocation(), EntityType.ITEM_FRAME);
                IdpItemStack stack = new IdpItemStack(IdpMaterial.MAP, 1, id);
                itemFrame.setItem(stack.toBukkitItemstack());
                itemFrame.setFacingDirection(frameFacingFace);

                CustomImageHandler.saveMapImageToDatabase(id, tempImage, ext);
            }
        }

        return null;
    }

    /**
     * Gets the blocks that will hold all the maps
     * @param height
     * @param width
     * @return
     */
    private Block[][] getBlockPath(int height, int width) {
        Block[][] blocks = new Block[height][width];
        Block baseBlock = originBlock.getRelative(BlockFace.SELF);
        BlockFace nextFace = null;

        switch (frameFacingFace) {
            case NORTH:
                nextFace = BlockFace.WEST;
                break;
            case EAST:
                nextFace = BlockFace.NORTH;
                break;
            case SOUTH:
                nextFace = BlockFace.EAST;
                break;
            case WEST:
                nextFace = BlockFace.SOUTH;
                break;
        }

        for (int i = 0; i < height; i++) {
            // Go to the block below
            if (i > 0) {
                baseBlock = baseBlock.getRelative(BlockFace.DOWN);
            }

            Block testBlock = baseBlock;

            for (int j = 0; j < width; j++) {
                // Go to the block below
                if (j > 0) {
                    testBlock = testBlock.getRelative(nextFace);
                }

                IdpMaterial mat = IdpMaterial.fromBlock(testBlock);

                // Cannot put maps on air!
                if (!mat.isSolid()) {
                    return null;
                }

                Block supportBlock = testBlock.getRelative(frameFacingFace);
                IdpMaterial mat2 = IdpMaterial.fromBlock(supportBlock);

                // Support block can only be air
                if (mat2 != IdpMaterial.AIR) {
                    return null;
                }

                blocks[i][j] = supportBlock;
            }
        }

        return blocks;
    }

    /**
     * Splits up the image into sub-images based on how many are needed
     * and returns the result as a 2D array
     * @param height
     * @param width
     * @return
     */
    private BufferedImage[][] getSplitImages(int height, int width) {
        BufferedImage[][] images = new BufferedImage[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                images[i][j] = image.getSubimage(j * 128, i * 128, 128, 128);
            }
        }

        return images;
    }

}
