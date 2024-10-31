package dev.jcps.vehicle2redux;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

import static dev.jcps.vehicle2redux.GameParams.BILD;
import static dev.jcps.vehicle2redux.GameParams.BILD_C;

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
    private final GameParams lp;

    /**
     * Constructs a {@code LevelMap} object based on the specified filename. It initiates a read operation
     * to load the level parameters from the file into a {@code GameParams} object.
     *
     * @param filename The name and path of the file containing the level map data.
     */
    private LevelMap(String filename) {
        this.filename = filename;
        lp = new GameParams(this.readHashtableFromFile());
    }

    /**
     * Retrieves a {@link LevelMap} object based on the specified map path.
     *
     * <p>This method attempts to create a {@link LevelMap} instance using the provided
     * map path. It then checks if the required files, identified by the keys
     * {@code BILD} and {@code BILD_C} in the parameter map, exist. If both files are found,
     * the method returns the constructed {@link LevelMap}. Otherwise, it returns {@code null}.</p>
     *
     * @param mapPath the path to the level map file
     * @return the {@link LevelMap} if both required files exist, otherwise {@code null}
     * @throws NullPointerException if the mapPath is {@code null}
     */
    public static @Nullable LevelMap getLevelMap(String mapPath) {
        LevelMap map = new LevelMap(mapPath);

        // check file exists
        boolean a = LevelUtilities.fileExists(map.lp.paramMap.get(BILD));
        boolean b = LevelUtilities.fileExists(map.lp.paramMap.get(BILD_C));

        if (a && b) {
            return map;
        }
        return null;
    }

    /**
     * Returns the {@code GameParams} object.
     * @return the {@code GameParams} object.
     */
    public GameParams getGameParams() {
        return lp;
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

    // Private methods

    /**
     * Retrieves the filename associated with the {@code LevelMap}.
     *
     * @return The filename associated with the {@code LevelMap}.
     */
    public String getFilename() {
        return this.filename;
    }

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
            if (V2RApp.isDebug()) V2RApp.logger.error("File error: {}", e.getMessage());
        }
        return hashMap;
    }

    /**
     * Writes the current {@code HashMap} to the file.
     */
    private void writeHashmapToFile() {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(this.filename, "rw")) {
            randomAccessFile.seek(0L);
            randomAccessFile.setLength(0L);

            ArrayList<String> arrayList = new ArrayList<>(this.lp.paramMap.keySet());
            for (String key : arrayList) {
                randomAccessFile.writeBytes(key + "=" + this.lp.paramMap.get(key) + "\n");
            }
        } catch (Exception e) {
            V2RApp.logger.error("There was an error writing the file in writeHashmapToFile(). {}",
                    e.getMessage());
        }
    }
}