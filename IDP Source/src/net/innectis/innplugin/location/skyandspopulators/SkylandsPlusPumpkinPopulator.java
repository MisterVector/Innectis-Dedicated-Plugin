package net.innectis.innplugin.location.skyandspopulators;

import java.util.Random;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.WorldGenPumpkin;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.generator.BlockPopulator;

public class SkylandsPlusPumpkinPopulator extends BlockPopulator {

	private Random random;

	public SkylandsPlusPumpkinPopulator(World world) {
		this.random = new Random(world.getSeed());
	}

	public void populate(World world, Random random, Chunk chunk){
		net.minecraft.server.v1_12_R1.World mcWorld = ((CraftWorld) world).getHandle();

		int x, y, z;

		int worldChunkX = chunk.getX() * 16;
		int worldChunkZ = chunk.getZ() * 16;

		if (this.random.nextInt(100) == 0){
			x = worldChunkX + this.random.nextInt(16) + 8;
			z = worldChunkZ + this.random.nextInt(16) + 8;

			y = world.getHighestBlockYAt(x, z);

			(new WorldGenPumpkin()).generate(mcWorld, this.random, new BlockPosition(x, y, z));
		}
	}

}
