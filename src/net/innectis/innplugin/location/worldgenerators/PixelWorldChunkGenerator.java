package net.innectis.innplugin.location.worldgenerators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

/**
 * @author Lynxy
 */
public class PixelWorldChunkGenerator extends ChunkGenerator {

    private int size = 2;

    public PixelWorldChunkGenerator() {
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return new ArrayList<BlockPopulator>();
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    public int xyzToByte(int x, int y, int z) {
        return (x * 16 + z) * 256 + y;
    }

    @SuppressWarnings("deprecation")
    @Override
    public byte[] generate(World world, Random random, int i, int i1) {
        byte[] result = new byte[65536];
        Material mat;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if (y == 0) {
                        mat = Material.BEDROCK;
                    } else if (y == size - 1) {
                        mat = Material.GRASS;
                    } else {
                        mat = Material.STONE;
                    }
                    result[xyzToByte(x, y, z)] = (byte) mat.getId();
                }
            }
        }
        return result;
    }

}
