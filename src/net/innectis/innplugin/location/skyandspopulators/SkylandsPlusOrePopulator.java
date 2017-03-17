package net.innectis.innplugin.location.skyandspopulators;

import java.util.Random;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.Blocks;
import net.minecraft.server.v1_11_R1.WorldGenMinable;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.generator.BlockPopulator;

public class SkylandsPlusOrePopulator extends BlockPopulator {

	private Random random;

	public SkylandsPlusOrePopulator(World world) {
		this.random = new Random(world.getSeed());
	}

	public void populate(World world, Random random, Chunk chunk){
		net.minecraft.server.v1_11_R1.World mcWorld = ((CraftWorld) world).getHandle();

		int x, y, z, i;

		int worldChunkX = chunk.getX() * 16;
		int worldChunkZ = chunk.getZ() * 16;

		for (i = 0; i < 10; ++i){
			x = worldChunkX + this.random.nextInt(16);
			z = worldChunkZ + this.random.nextInt(16);

			y = this.random.nextInt(128);

			(new WorldGenMinable(Blocks.GRAVEL.getBlockData(), 32)).generate(mcWorld, this.random, new BlockPosition(x, y, z));
		}

		for (i = 0; i < 20; ++i){
			x = worldChunkX + this.random.nextInt(16);
			z = worldChunkZ + this.random.nextInt(16);

			y = this.random.nextInt(128);

			(new WorldGenMinable(Blocks.COAL_ORE.getBlockData(), 16)).generate(mcWorld, this.random, new BlockPosition(x, y, z));
		}

		for (i = 0; i < 20; ++i){
			x = worldChunkX + this.random.nextInt(16);
			z = worldChunkZ + this.random.nextInt(16);

			y = this.random.nextInt((int) (64 * 0.8));

                	(new WorldGenMinable(Blocks.IRON_ORE.getBlockData(), 8)).generate(mcWorld, this.random, new BlockPosition(x, y, z));
		}

		for (i = 0; i < 3; ++i){
			x = worldChunkX + this.random.nextInt(16);
			z = worldChunkZ + this.random.nextInt(16);

			y = this.random.nextInt((int) (64 * 0.75));

			(new WorldGenMinable(Blocks.GOLD_ORE.getBlockData(), 8)).generate(mcWorld, this.random, new BlockPosition(x, y, z));
		}

		for (i = 0; i < 8; ++i){
			x = worldChunkX + this.random.nextInt(16);
			z = worldChunkZ + this.random.nextInt(16);

			y = this.random.nextInt((int) (64 * 0.7));

			(new WorldGenMinable(Blocks.REDSTONE_ORE.getBlockData(), 7)).generate(mcWorld, this.random, new BlockPosition(x, y, z));
		}

		for (i = 0; i < 2; ++i){
			x = worldChunkX + this.random.nextInt(16);
			z = worldChunkZ + this.random.nextInt(16);

			y = this.random.nextInt((int) (64 * 0.4));

			(new WorldGenMinable(Blocks.DIAMOND_ORE.getBlockData(), 7)).generate(mcWorld, this.random, new BlockPosition(x, y, z));
		}

		for (i = 0; i < 2; ++i){
			x = worldChunkX + this.random.nextInt(16);
			z = worldChunkZ + this.random.nextInt(16);

			y = this.random.nextInt((int) (64 * 0.5));

			(new WorldGenMinable(Blocks.LAPIS_ORE.getBlockData(), 6)).generate(mcWorld, this.random, new BlockPosition(x, y, z));
		}
	}

}
