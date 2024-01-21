import java.io.File;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
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
    private static ArrayList<String> getLevelList() {
        ArrayList<String> list = new ArrayList<>();
        URL url = null;
        String urlPath = null;

        try {
            urlPath = "file:///" + System.getProperty("user.dir") + File.separator + "Levels";
            url = new URL(urlPath);
            System.out.println("Loading maps from: " + urlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        File file;

        try {
            assert url != null;
            file = new File(new URI(url.toString()));
        } catch (Exception e) {
            String userDir = System.getProperty("user.dir");
            System.err.println("Error trying to form File in getLevelList from default directory.\n" +
                    "Attempting to create File directly from user.dir='" + userDir + "' property.\nNotes: " +
                    "url=" + urlPath + ", urlContext=" + url);
            file = new File(userDir);
        }

        File[] files = file.listFiles();

        if(files != null) {
            for (File curFile : files) {
                String curName = curFile.getName();
                if (findIgnoreCase(curName, ".map") != -1 && curFile.isFile()) {
                    list.add(curName);
                }
            }
        }
        return list;
    }

    /**
     * Finds the index of the first occurrence of the target string in the given text.
     *
     * @param text     The text to search.
     * @param target   The target string to find.
     * @param loc      The starting index for the search.
     * @param caseBool Whether to perform a case-sensitive search.
     * @return The index of the first occurrence of the target string, or -1 if not found.
     */
    private static int find(String text, String target, int loc, boolean caseBool) {
        String firstChar = target.substring(0, 1);
        int targetLen = target.length();
        int textLen = text.length();
        if (loc >= -1) {
            for (int i = loc; i < textLen - targetLen + 1; ++i) {
                String substring = text.substring(i, i + 1);
                boolean checked = false;
                if (caseBool) {
                    if (substring.equalsIgnoreCase(firstChar)) {
                        checked = true;
                    }
                } else if (substring.equals(firstChar)) {
                    checked = true;
                }

                if (checked) {
                    String substring1 = text.substring(i, i + targetLen);
                    if (caseBool) {
                        if (substring1.equalsIgnoreCase(target)) {
                            return i;
                        }
                    } else if (substring1.equals(target)) {
                        return i;
                    }
                }
            }

        }
        return -1;
    }

    /**
     * Finds the index of the first occurrence of the target string in the given text, ignoring case.
     *
     * @param text   The text to search.
     * @param target The target string to find.
     * @return The index of the first occurrence of the target string, or -1 if not found.
     */
    public static int findIgnoreCase(String text, String target) {
        return find(text, target, 0, true);
    }

    /**
     * Retrieves an ArrayList of LevelMap objects based on the level files in the "Levels" directory.
     *
     * @return ArrayList of LevelMap objects.
     */
    public static ArrayList<LevelMap> getLevelMaps() {
        ArrayList<String> files = getLevelList();
        ArrayList<LevelMap> maps = new ArrayList<>();
        System.out.println("Map Files Loaded:");
        for (Object s : files) {
            System.out.println(s);
            LevelMap map = new LevelMap("Levels/" + s);
            maps.add(map);
        }
        return maps;
    }

    /**
     * Main method for testing LevelUtilities functionality.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        ArrayList<LevelMap> maps = getLevelMaps();
        System.out.println("Complete");
        String test = null;
        String exceptionList = "";
        try {
            test = String.valueOf(maps.get(0).get("Bild"));
        } catch (Exception e) {
            exceptionList += e;
        }
        System.out.println(exceptionList);
        System.out.println(test);
    }

    public static void writeHashmapToFile(HashMap<String, String> map, String filename) {
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(filename, "rw");
            randomAccessFile.seek(0L);
            randomAccessFile.setLength(0L);
        } catch (Exception e) {
            System.err.println("There was an error preparing the file in writeHashmapToFile().");
            return;
        }

        ArrayList<String> arrayList = new ArrayList<>(map.keySet());
        try {
            for (String o : arrayList) {
                randomAccessFile.writeBytes(o + "=" + map.get(o) + "\n");
            }
            randomAccessFile.close();
        } catch (Exception e) {
            System.err.println("There was an error writing the file in writeHashmapToFile().");
        }
    }
}
