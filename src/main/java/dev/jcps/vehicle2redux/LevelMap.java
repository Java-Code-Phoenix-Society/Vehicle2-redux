package dev.jcps.vehicle2redux;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a level map with key-value pairs stored in a file.
 */
class LevelMap {
    private final String filename;
    @SuppressWarnings("WeakerAccess")
    public GameParams lp;

    /**
     * Constructor for the dev.jcps.vehicle2redux.LevelMap class.
     *
     * @param filename The name of the file containing the level map data.
     */
    public LevelMap(String filename) {
        this.filename = filename;
        lp = new GameParams(this.readHashtableFromFile());
    }

    /**
     * Retrieves the value associated with the specified key.
     *
     * @param value The key whose associated value is to be retrieved.
     * @return The value associated with the specified key.
     */
    public String get(String value) {
        return this.lp.paramMap.get(value);
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
     * Retrieves the filename associated with the dev.jcps.vehicle2redux.LevelMap.
     *
     * @return The filename associated with the dev.jcps.vehicle2redux.LevelMap.
     */
    public String getFilename() {
        return this.filename;
    }

    // Private methods

    /**
     * Reads key-value pairs from the file and populates the HashMap.
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
            Vehicle2.logger.error("File error: {}", e.getMessage());
        }
        return hashMap;
    }

    /**
     * Writes the current HashMap to the file.
     */
    private void writeHashmapToFile() {
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(this.filename, "rw");
            randomAccessFile.seek(0L);
            randomAccessFile.setLength(0L);
        } catch (Exception e) {
            Vehicle2.logger.error("There was an error preparing the file in writeHashmapToFile().");
            return;
        }

        ArrayList<String> arrayList = new ArrayList<>(this.lp.paramMap.keySet());
        try {
            for (String o : arrayList) {
                randomAccessFile.writeBytes(o + "=" + this.lp.paramMap.get(o) + "\n");
            }
            randomAccessFile.close();
        } catch (Exception e) {
            Vehicle2.logger.error("There was an error writing the file in writeHashmapToFile().");
        }
    }
}
