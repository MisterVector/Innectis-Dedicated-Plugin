package net.innectis.innplugin.location.worldgenerators;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.World.Environment;
import org.bukkit.generator.ChunkGenerator;

/**
 *
 * @author Hret
 *
 * This enum is used to get the enviorment and chunkgenerator for a world.
 * The default chunkgenerator is shown as 'null'
 */
public enum MapType {

    DEFAULT(null, Environment.NORMAL),
    PIXELWORLD(PixelWorldChunkGenerator.class.getName(), Environment.NORMAL),
    EVENTWORLD(EventWorldChunkCreator.class.getName(), Environment.NORMAL),
    NETHER(null, Environment.NETHER),
    AETHER(SkylandsPlusChunkGenerator.class.getName(), Environment.NORMAL),
    THE_END(null, Environment.THE_END);
    /**
     * The chunkgenerator.
     * For the default generator, this value will be null.
     */
    private final String generatorClassPath;
    /**
     * The Environment
     */
    private final Environment environment;

    private MapType(String generatorClassPath, Environment environment) {
        this.generatorClassPath = generatorClassPath;
        this.environment = environment;
    }

    /***
     * Gets the chunkgenerator, or null if there it should use the default generator
     * @return
     */
    public ChunkGenerator getChunkGenerator() {
        if (generatorClassPath != null && !generatorClassPath.isEmpty()) {
            try {
                return (ChunkGenerator) Class.forName(generatorClassPath).newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(MapType.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(MapType.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MapType.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     * @return the environment
     */
    public Environment getEnvironment() {
        return environment;
    }

}