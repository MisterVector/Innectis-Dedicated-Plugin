package net.innectis.innplugin.external;

import java.io.File;
import net.innectis.innplugin.external.api.VotifierIDP;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.external.api.alternate.WorldEditIDPAlternative;
import net.innectis.innplugin.external.api.NoCheatPlusIDP;
import net.innectis.innplugin.external.api.WorldEditIDP;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * A manager for all external libraries
 *
 * @author AlphaBlend
 */
public class ExternalLibraryManager {

    private WeakReference<YamlConfiguration> configuration;
    // Associate's a library with its API object
    private HashMap<LibraryType, IExternalLibrary> libraryObjects = new HashMap<LibraryType, IExternalLibrary>();
    // Holds a reference to the dynmap object
    private DynmapIDP dynmap;

    public ExternalLibraryManager() {
    }

    /**
     * Attemps to set up the dynmap plugin
     * @param plugin
     * @return true if successful, false otherwise
     */
    public boolean setupDynmap(InnPlugin plugin, Plugin bukkitPlugin) {
        dynmap = new DynmapIDP(plugin, bukkitPlugin);
        return dynmap.setupDynmap();
    }

    /**
     * Attempts to register the API objects for each external library that
     * has an API in which we use
     */
    public void registerExternalLibraries(InnPlugin plugin) {
        // Attempt to register the votifier API object
        Plugin bukkitPlugin;
        for (LibraryType library : LibraryType.values()) {

            // Get name and version
            String pluginname = getConfigValue(library.getName() + ".name");
            String pluginversion = getConfigValue(library.getName() + ".version");

            // Load the plugin
            bukkitPlugin = plugin.getServer().getPluginManager().getPlugin(pluginname);
            boolean found = (bukkitPlugin != null);

            // Check if we need to check the version
            if (found && !StringUtil.stringIsNullOrEmpty(pluginversion)) {
                String ymlversion = bukkitPlugin.getDescription().getVersion();
                // Check if the version is the same
                if (!pluginversion.equals(ymlversion)) {
                    plugin.logError("Unexpected library version: " + ymlversion + " for " + pluginname + " expected " + pluginversion);

                    // Check the configuration if we can skip it.
                    String skipstring = getConfigValue(library.getName() + ".skipwrongversion");
                    if (!StringUtil.stringIsNullOrEmpty(skipstring) && skipstring.equalsIgnoreCase("true")) {

                        // Clear the refence, behave as we did not find the plugin
                        bukkitPlugin = null;
                    }
                }
            }

            boolean dependenciesValid = true;

            // Check for dependencies
            if (found) {
                dependenciesValid = checkDependencies(library, plugin);
            } else {
                plugin.logError(library.getName() + " is missing. Substitute will be used! (if any)");
            }



            IExternalLibrary api = null;

            // Use either the normal API or the substitute API, depending on if the plugin was available or not
            switch (library) {
                case NOCHEATPLUS:
                    if (found && dependenciesValid) {
                        api = new NoCheatPlusIDP(bukkitPlugin);
                    }
                    break;
                case WORLDEDIT:
                    if (found && dependenciesValid) {
                        api = new WorldEditIDP(bukkitPlugin);
                    } else {
                        api = new WorldEditIDPAlternative();
                    }
                    break;
                case VOTIFIER:
                    if (found && dependenciesValid) {
                        api = new VotifierIDP(plugin, bukkitPlugin);
                    }
            }

            if (api != null) {
                try {
                    // Initialize the plugin
                    api.initialize();

                    libraryObjects.put(library, api);
                } catch (LibraryInitalizationError lie) {
                    InnPlugin.logError("Could not initialize plugin: " + pluginname, lie);
                }
            }
        }

        configuration = null;
    }

    /**
     * This will get the correct plugin name according to the libraries.yml configuration file.
     * The file itself is located in the default package of the IDP.
     * @param key
     * @return
     */
    private String getConfigValue(String key) {
        YamlConfiguration config = null;
        // Check if we still got a reference
        if (configuration == null || configuration.get() == null) {
            // No reference, reload
            InputStream stream = ExternalLibraryManager.class.getResourceAsStream("/libraries.yml");

            if (stream != null) {
                InputStreamReader reader = new InputStreamReader(stream);
                config = YamlConfiguration.loadConfiguration(reader);

                if (config != null) {
                    configuration = new WeakReference<YamlConfiguration>(config);
                }
            }
        } else {
            // Get from regerence
            config = configuration.get();
        }
        // Check if we got a configuration file
        if (config != null) {
            // return the proper name
            return config.getString(key);
        }
        // No key, just return.
        return key;
    }

    /**
     * Returns an API for the specified external library
     * @param library
     * @return
     */
    public IExternalLibrary getAPIObject(LibraryType library) {
        return libraryObjects.get(library);
    }

    /**
     * This will check if the given library has a proper handler loaded.
     * It will not detect any difference between a real and dummy manager.
     * @param library
     * @return true if the library or a dummy is loaded
     */
    public boolean isLoaded(LibraryType library) {
        return libraryObjects.containsKey(library);
    }

    /**
     * This will check all of the dependencies of the given librarytype.
     * If there is an error, it will automaticly report to the logger.
     *
     * @param library
     * @param plugin
     * @return true when the dependencies are valid
     */
    private boolean checkDependencies(LibraryType library, InnPlugin plugin) {
        boolean loadFailed = true;

        Plugin dependencyPlugin;
        String[] dependencies = library.getDependencies();
        // Check for available dependencies if they exist
        if (dependencies.length > 0) {
            String failedNames = "";

            for (String dependency : dependencies) {
                // Update the depency name according to config values
                dependency = getConfigValue(dependency + ".name");

                // Load the depencency plugin
                dependencyPlugin = plugin.getServer().getPluginManager().getPlugin(dependency);

                if (dependencyPlugin == null) {
                    loadFailed = false;

                    if (failedNames.isEmpty()) {
                        failedNames = dependency;
                    } else {
                        failedNames += ", " + dependency;
                    }
                }
            }

            if (!loadFailed) {
                plugin.logError(library.getName() + " is missing the following dependencies: " + failedNames);
                plugin.logError(library.getName() + " will use a substitute API instead.");
            }
        }

        return loadFailed;
    }

}
