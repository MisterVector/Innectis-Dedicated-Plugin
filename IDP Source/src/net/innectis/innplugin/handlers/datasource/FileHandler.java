package net.innectis.innplugin.handlers.datasource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Hret
 */
public class FileHandler {

    public static List<String> getData(String file) throws FileNotFoundException {
        return getData(new File(file));
    }

    public static List<String> getData(File file) throws FileNotFoundException {
        List<String> data = new ArrayList<String>();

        Scanner scanner = new Scanner(new FileInputStream(file));
        try {
            while (scanner.hasNextLine()) {

                String line = scanner.nextLine();

                if (!line.isEmpty()) {
                    data.add(line);
                }
            }

        } finally {
            scanner.close();
        }
        return data;
    }

    /**
     * Adds the data to the end of the file.
     * @param file
     * @param data
     * @throws IOException
     */
    public static void addData(File file, String[] data, boolean append) throws IOException {
        if (!append) {
            file.delete();
            file.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        try {
            for (String str : data) {
                writer.newLine();
                writer.write(str);
                writer.flush();
            }
        } catch (IOException ioex) {
            throw ioex;
        } finally {
            // Always close it off!
            writer.close();
        }
    }

    /**
     * Returns a loaded configuration with the contents of the given yml file.
     * @param location
     * @return the yml file as a config
     * @throws FileNotFoundException
     */
    public static YamlConfiguration getYmlFile(String location) throws FileNotFoundException {
        File file = new File(location);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found! " + file.getAbsolutePath());
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config;
    }

    public static void saveObject(Object obj, String path) throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
        oos.writeObject(obj);
        oos.flush();
        oos.close();
    }

    public static Object loadObject(String path) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
        Object result = ois.readObject();
        ois.close();
        return result;
    }

    /**
     * Force deletion of directory
     * @param path
     * @return true if the file was deleted
     * False when file not deleted but marked as deleteonexit
     */
    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);

                } else {
                    if (!files[i].delete()) {
                        files[i].deleteOnExit();
                    }
                }
            }
        }
        if (!path.delete()) {
            path.deleteOnExit();
            return false;
        }
        return true;
    }
    
}
