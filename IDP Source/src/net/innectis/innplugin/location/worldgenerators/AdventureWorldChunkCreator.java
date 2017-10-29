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
public class AdventureWorldChunkCreator extends ChunkGenerator {

    public AdventureWorldChunkCreator() {
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
        for (int y = 0; y < 129; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if (y == 0 || y == 128) {
                        mat = Material.BEDROCK;
                    } else {
                        mat = Material.STONE;
                    }
                    result[xyzToByte(x, y, z)] = (byte) mat.getId();
                }
            }
        }

        Random generation = new Random((i * i + i1 / 2) * random.nextInt(10));
        switch (generation.nextInt(3)) {
            case 0:
                for (int y = 59; y < 66; y++) {
                    for (int x = 6; x < 12; x++) {
                        for (int z = 0; z < 16; z++) {
                            if (y == 59) {
                                result[xyzToByte(x, y, z)] = 5;
                            } else if (y == 60 && (x < 7 || x > 10)) {
                                result[xyzToByte(x, y, z)] = 98;
                            } else if (y == 65 && (x == 8 || x == 9) && z % 4 == 0) {
                                result[xyzToByte(x, y, z)] = 11;
                            } else if (y == 64 && (x == 8 || x == 9) && z % 4 == 0) {
                                result[xyzToByte(x, y, z)] = 101;
                            } else if (y < 64 && x > 6 && x < 11) {
                                result[xyzToByte(x, y, z)] = 0;
                            }
                        }
                    }
                }
                break;
            case 1:
                for (int y = 59; y < 66; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 6; z < 12; z++) {
                            if (y == 59) {
                                result[xyzToByte(x, y, z)] = 5;
                            } else if (y == 60 && (z < 7 || z > 10)) {
                                result[xyzToByte(x, y, z)] = 98;
                            } else if (y == 65 && (z == 8 || z == 9) && x % 4 == 0) {
                                result[xyzToByte(x, y, z)] = 11;
                            } else if (y == 64 && (z == 8 || z == 9) && x % 4 == 0) {
                                result[xyzToByte(x, y, z)] = 101;
                            } else if (y < 64 && z > 6 && z < 11) {
                                result[xyzToByte(x, y, z)] = 0;
                            }
                        }
                    }
                }
                break;
            case 2:
                for (int y = 59; y < 66; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            if (y == 59 && ((x > 6 && x < 11) || (z > 6 && z < 11))) {
                                result[xyzToByte(x, y, z)] = 5;
                            } else if (y < 61 && !((x > 6 && x < 11) || (z > 6 && z < 11))) {
                                result[xyzToByte(x, y, z)] = 98;
                            } else if (y == 65 && ((((z == 8 || z == 9) && x % 4 == 0)) || ((x == 8 || x == 9) && z % 4 == 0))) {
                                result[xyzToByte(x, y, z)] = 11;
                            } else if (y == 64 && ((((z == 8 || z == 9) && x % 4 == 0)) || ((x == 8 || x == 9) && z % 4 == 0))) {
                                result[xyzToByte(x, y, z)] = 101;
                            } else if (y < 64 && ((x > 6 && x < 11) || (z > 6 && z < 11))) {
                                result[xyzToByte(x, y, z)] = 0;
                            }
                        }
                    }
                }
                break;
        }

        return result;
    }

}
