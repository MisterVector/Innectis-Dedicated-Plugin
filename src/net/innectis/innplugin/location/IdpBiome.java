package net.innectis.innplugin.location;

import net.innectis.innplugin.location.worldgenerators.MapType;
import org.bukkit.block.Biome;

/**
 *
 * @author Hret
 *
 */
public enum IdpBiome {

    SWAMPLAND 			(  1,	Biome.SWAMPLAND,                MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    FOREST 			(  2,	Biome.FOREST,                   MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    TAIGA 			(  3,	Biome.TAIGA,                    MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    DESERT 			(  4,	Biome.DESERT,                   MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    PLAINS 			(  5,	Biome.PLAINS,   		MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    HELL 			(  6,	Biome.HELL), //, 		MapType.NETHER),
    SKY 			(  7,	Biome.SKY,              	MapType.AETHER),
    OCEAN 			(  8,	Biome.OCEAN,                    MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    RIVER 			(  9,	Biome.RIVER,                    MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    EXTREME_HILLS 		( 10,	Biome.EXTREME_HILLS,            MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    FROZEN_OCEAN 		( 11,	Biome.FROZEN_OCEAN,             MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    FROZEN_RIVER 		( 12,	Biome.FROZEN_RIVER,             MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    ICE_PLAINS 			( 13,	Biome.ICE_FLATS,                MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    ICE_MOUNTAINS 		( 14,	Biome.ICE_MOUNTAINS,    	MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    MUSHROOM_ISLAND             ( 15,	Biome.MUSHROOM_ISLAND,   	MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    MUSHROOM_SHORE 		( 16,	Biome.MUSHROOM_ISLAND_SHORE,	MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    BEACH 			( 17,	Biome.BEACHES,                  MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    DESERT_HILLS 		( 18,	Biome.DESERT_HILLS,     	MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    FOREST_HILLS 		( 19,	Biome.FOREST_HILLS,             MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    TAIGA_HILLS 		( 20,	Biome.TAIGA_HILLS,              MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    SMALL_MOUNTAINS             ( 21,	Biome.SMALLER_EXTREME_HILLS,  	MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    JUNGLE 			( 22,	Biome.JUNGLE,                   MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    JUNGLE_HILLS 		( 23,	Biome.JUNGLE_HILLS,             MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    DEEP_OCEAN                  ( 24,   Biome.DEEP_OCEAN,               MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    STONE_BEACH                 ( 25,   Biome.STONE_BEACH,              MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    COLD_BEACH                  ( 26,   Biome.COLD_BEACH,               MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    BIRCH_FOREST                ( 27,   Biome.BIRCH_FOREST,             MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    BIRCH_FOREST_HILLS          ( 28,   Biome.BIRCH_FOREST_HILLS,       MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    ROOFED_FOREST               ( 29,   Biome.ROOFED_FOREST,            MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    COLD_TAIGA                  ( 30,   Biome.TAIGA_COLD,               MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    COLD_TAIGA_HILLS            ( 31,   Biome.TAIGA_COLD_HILLS,         MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    MEGA_TAIGA                  ( 32,   Biome.REDWOOD_TAIGA,            MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    MEGA_TAIGA_HILLS            ( 33,   Biome.REDWOOD_TAIGA_HILLS,      MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    EXTREME_HILLS_PLUS          ( 34,   Biome.EXTREME_HILLS_WITH_TREES, MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    SAVANNA                     ( 35,   Biome.SAVANNA,                  MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    SAVANNA_PLATEAU             ( 36,   Biome.SAVANNA_ROCK,             MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    MESA                        ( 37,   Biome.MESA,                     MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    MESA_PLATEAU_FOREST         ( 38,   Biome.MESA_ROCK,                MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    MESA_PLATEAU                ( 39,   Biome.MESA_CLEAR_ROCK,          MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    SUNFLOWER_PLAINS            ( 40,   Biome.MUTATED_PLAINS,           MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    DESERT_MOUNTAINS            ( 41,   Biome.MUTATED_DESERT,           MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    FLOWER_FOREST               ( 42,   Biome.MUTATED_FOREST,           MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    TAIGA_MOUNTAINS             ( 43,   Biome.MUTATED_TAIGA,            MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    SWAMPLAND_MOUNTAINS         ( 44,   Biome.MUTATED_SWAMPLAND,        MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    ICE_PLAINS_SPIKES           ( 45,   Biome.MUTATED_ICE_FLATS,        MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    JUNGLE_MOUNTAINS            ( 46,   Biome.MUTATED_JUNGLE,           MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    JUNGLE_EDGE_MOUNTAINS       ( 47,   Biome.MUTATED_JUNGLE_EDGE,      MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    COLD_TAIGA_MOUNTAINS        ( 48,   Biome.MUTATED_TAIGA_COLD,       MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    SAVANNA_MOUNTAINS           ( 49,   Biome.MUTATED_SAVANNA,          MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    SAVANNA_PLATEAU_MOUNTAINS   ( 50,   Biome.MUTATED_SAVANNA_ROCK,     MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    MESA_BRYCE                  ( 51,   Biome.MUTATED_MESA,             MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    MESA_PLATEAU_FOREST_MOUNTAINS(52,   Biome.MUTATED_MESA_ROCK,        MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    MESA_PLATEAU_MOUNTAINS      ( 53,   Biome.MUTATED_MESA_CLEAR_ROCK,  MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    BIRCH_FOREST_MOUNTAINS      ( 54,   Biome.MUTATED_BIRCH_FOREST,     MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    BIRCH_FOREST_HILLS_MOUNTAINS( 55,   Biome.MUTATED_BIRCH_FOREST_HILLS,MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    ROOFED_FOREST_MOUNTAINS     ( 56,   Biome.MUTATED_ROOFED_FOREST,    MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    MEGA_SPRUCE_TAIGA           ( 57,   Biome.MUTATED_REDWOOD_TAIGA,    MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    EXTREME_HILLS_MOUNTAINS     ( 58,   Biome.MUTATED_EXTREME_HILLS,    MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    EXTREME_HILLS_PLUS_MOUNTAINS( 59,   Biome.MUTATED_EXTREME_HILLS_WITH_TREES,MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD),
    MEGA_SPRUCE_TAIGA_HILLS     ( 60,   Biome.MUTATED_REDWOOD_TAIGA_HILLS,MapType.DEFAULT, MapType.EVENTWORLD, MapType.PIXELWORLD);

    private final int id;
    private final Biome biome;
    private final MapType[] maptypes;

    private IdpBiome(int id, Biome biome, MapType ... maptypes) {
        this.id = id;
        this.biome = biome;
        this.maptypes = maptypes;
    }

    /**
     * Returns the (IDP) ID of the biome
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the bukkit biome
     * @return
     */
    public Biome getBukkitBiome() {
        return biome;
    }

    /**
     * Returns an array of maptypes where this biome is allowed
     * @return
     */
    public MapType[] getMaptypes() {
        return maptypes;
    }

    /**
     * Checks if the biome is allowed on the given world
     * @param world
     * @return
     */
    public boolean isWorldAllowed(IdpWorld world){
        for (MapType type : getMaptypes()) {
            if (type == world.getSettings().getMaptype()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Looks up the biome.
     * Returns the biome that matches the lookupstring or null.
     * @param lookupstring - can be the name or the ID
     */
    public static IdpBiome lookup(String lookupstring){
        int id = -1;

        // Check if the lookup is an int
        try {
            id  =Integer.parseInt(lookupstring);
        } catch (NumberFormatException nfe){
            // Do nothing
        }

        // lookup
        for (IdpBiome biome : values()) {
            if (id > 0){
                if (biome.getId() == id) {
                    return biome;
                }
            } else {
                if (biome.name().equalsIgnoreCase(lookupstring)) {
                    return biome;
                }
            }
        }

        // Not found
        return null;
    }

}
