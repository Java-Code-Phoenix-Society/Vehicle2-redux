package dev.jcps.vehicle2redux;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Utility class for handling levels and maps.
 */
public class LevelUtilities {
    /**
     * Retrieves a list of level filenames from the "Levels" directory.
     *
     * @return ArrayList of level filenames.
     */
    private static @NotNull ArrayList<String> getLevelList() {
        ArrayList<String> list = new ArrayList<>();
        URL url = null;
        String urlPath = null;

        try {
            urlPath = "file:///" + System.getProperty("user.dir") + File.separator + "Levels";
            url = new URL(urlPath);
            Vehicle2.logger.info("Loading maps from: {}", urlPath);
        } catch (Exception e) {
            Vehicle2.logger.error(Arrays.toString(e.getStackTrace()));
        }

        File file;

        try {
            assert url != null;
            file = new File(new URI(url.toString()));
        } catch (Exception e) {
            String userDir = System.getProperty("user.dir");
            String mainText = "Error trying to form File in getLevelList from default directory.\nAttempting to create File directly from";
            Vehicle2.logger.error("{} user.dir='{}' property.\nNotes: url={}, urlContext={}",
                    mainText, userDir, urlPath, url);
            file = new File(userDir);
        }

        File[] files = file.listFiles();

        assert files != null;
        for (File curFile : files) {
            String curName = curFile.getName();
            if (findIgnoreCase(curName, ".map") != -1 && curFile.isFile()) {
                list.add(curName);
            }
        }

        return list;
    }

    /**
     * Finds the index of the first occurrence of the target string in the given text.
     *
     * @param text   The text to search.
     * @param target The target string to find.
     * @return The index of the first occurrence of the target string, or -1 if not found.
     */
    private static int find(@NotNull String text, @NotNull String target) {
        String firstChar = target.substring(0, 1);
        int targetLen = target.length();
        int textLen = text.length();
        for (int i = 0; i < textLen - targetLen + 1; ++i) {
            String substring = text.substring(i, i + 1);
            boolean checked = false;
            checked = isChecked(true, substring, firstChar, checked);

            if (checked) {
                String substring1 = text.substring(i, i + targetLen);
                if (substring1.equalsIgnoreCase(target)) {
                    return i;
                }
            }
        }

        return -1;
    }

    private static boolean isChecked(boolean caseBool, String substring, String firstChar, boolean checked) {
        if (caseBool) {
            if (substring.equalsIgnoreCase(firstChar)) {
                checked = true;
            }
        } else if (substring.equals(firstChar)) {
            checked = true;
        }
        return checked;
    }

    /**
     * Finds the index of the first occurrence of the target string in the given text, ignoring case.
     *
     * @param text   The text to search.
     * @param target The target string to find.
     * @return The index of the first occurrence of the target string, or -1 if not found.
     */
    public static int findIgnoreCase(String text, String target) {
        return find(text, target);
    }

    /**
     * Retrieves an ArrayList of dev.jcps.vehicle2redux.LevelMap objects based on the level files in the "Levels" directory.
     *
     * @return ArrayList of dev.jcps.vehicle2redux.LevelMap objects.
     */
    public static @NotNull ArrayList<LevelMap> getLevelMaps() {
        ArrayList<String> files = getLevelList();
        ArrayList<LevelMap> maps = new ArrayList<>();
        Vehicle2.logger.info("Map Files Loaded:");
        for (Object s : files) {
            Vehicle2.logger.info("Loading: {}", s);
            LevelMap map = new LevelMap("Levels/" + s);
            maps.add(map);
        }
        return maps;
    }

    /**
     * Main method for testing dev.jcps.vehicle2redux.LevelUtilities functionality.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        ArrayList<LevelMap> maps = getLevelMaps();
        System.out.println("Complete"); //NOSONAR commandline output
        String test = null;
        String exceptionList = "";
        try {
            test = String.valueOf(maps.get(0).get("Bild"));
        } catch (Exception e) {
            exceptionList += e;
        }
        System.out.println(exceptionList); //NOSONAR
        System.out.println(test); //NOSONAR
    }

    public static void writeHashmapToFile(HashMap<String, String> map, String filename) {
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(filename, "rw");
            randomAccessFile.seek(0L);
            randomAccessFile.setLength(0L);
        } catch (Exception e) {
            Vehicle2.logger.error("There was an error preparing the file in writeHashmapToFile().");
            return;
        }

        ArrayList<String> arrayList = new ArrayList<>(map.keySet());
        try {
            for (String o : arrayList) {
                randomAccessFile.writeBytes(o + "=" + map.get(o) + "\n");
            }
            randomAccessFile.close();
        } catch (Exception e) {
            Vehicle2.logger.error("There was an error writing the file in writeHashmapToFile().");
        }
    }
}
