package dev.jcps.vehicle2redux;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The {@code LevelMap} class encapsulates the map of a specific game level, stored in a {@code .map} file.
 * It provides methods to read, write, and manipulate the level parameters.
 * <p>
 * The map is represented as a collection of key-value pairs, where each pair corresponds to a specific
 * parameter of the level. The parameters include various characteristics and settings of the game level.
 * </p>
 * <p>
 * <i>Note: Changes made to the map parameters are directly written to the associated {@code .map} file.</i>
 * </p>
 *
 * @author neoFuzz
 * @since 1.0
 */
public class LevelMap {
    /**
     * The name and path of the file storing the level map data.
     */
    private final String filename;
    /**
     * A {@code GameParams} object that holds the level parameters loaded from the {@code .map} file.
     * These parameters can be accessed and modified through this object.
     *
     * @see GameParams
     */
    public GameParams lp;

    /**
     * Constructs a {@code LevelMap} object based on the specified filename. It initiates a read operation
     * to load the level parameters from the file into a {@code GameParams} object.
     *
     * @param filename The name and path of the file containing the level map data.
     */
    public LevelMap(String filename) {
        this.filename = filename;
        lp = new GameParams(this.readHashtableFromFile());
    }

    /**
     * Retrieves the filename associated with the {@code LevelMap}. This filename represents the
     * {@code .map} file where the level parameters are stored.
     *
     * @param key The key with which the specified value is to be associated.
     * @return The name and path of the file associated with the {@code LevelMap}.
     */
    public String get(String key) {
        return this.lp.paramMap.get(key);
    }

    /**
     * Associates the specified value with the specified key and writes the updated map to the file.
     *
     * @param key   The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     */
    public void put(String key, String value) {
        this.lp.paramMap.put(key, value);
        this.writeHashmapToFile();
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return The number of key-value mappings in this map.
     */
    public int size() {
        return this.lp.paramMap.size();
    }

    /**
     * Retrieves the filename associated with the {@code LevelMap}.
     *
     * @return The filename associated with the {@code LevelMap}.
     */
    public String getFilename() {
        return this.filename;
    }

    // Private methods

    /**
     * Reads key-value pairs from the file and populates the {@code HashMap}.
     *
     * @return A {@code HashMap} containing key-value pairs read from the file.
     */
    private @NotNull HashMap<String, String> readHashtableFromFile() {
        HashMap<String, String> hashMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(this.filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(";")) {
                    continue; // Skip empty lines and comments
                }
                int equalsIndex = line.indexOf('=');
                if (equalsIndex != -1) {
                    String key = line.substring(0, equalsIndex).trim();
                    String value = line.substring(equalsIndex + 1).trim();
                    hashMap.put(key, value);
                }
            }
        } catch (IOException e) {
            if (V2RApp.debug) System.out.println("File error: " + e.getMessage());
        }
        return hashMap;
    }

    /**
     * Writes the current {@code HashMap} to the file.
     */
    private void writeHashmapToFile() {
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(this.filename, "rw");
            randomAccessFile.seek(0L);
            randomAccessFile.setLength(0L);
        } catch (Exception e) {
            System.err.println("There was an error preparing the file in writeHashmapToFile().");
            return;
        }

        ArrayList<String> arrayList = new ArrayList<>(this.lp.paramMap.keySet());
        try {
            for (String o : arrayList) {
                randomAccessFile.writeBytes(o + "=" + this.lp.paramMap.get(o) + "\n");
            }
            randomAccessFile.close();
        } catch (Exception e) {
            System.err.println("There was an error writing the file in writeHashmapToFile().");
        }
    }
}