package net.year4000.ettcapsule;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

public class ETTCapsuleGenerator extends ChunkGenerator{
	public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, ChunkGenerator.BiomeGrid biomeGrid){
		world.setKeepSpawnInMemory(false);
		world.setSpawnLocation(14, 90, 7);

		for (int x = 0; x < 16; x++){
			for (int z = 0; z < 16; z++){
				world.setBiome(chunkX * 16 + x, chunkZ * 16 + z, Biome.SKY);
			}
		}
		return new byte[world.getMaxHeight() / 16][];
	}
}
