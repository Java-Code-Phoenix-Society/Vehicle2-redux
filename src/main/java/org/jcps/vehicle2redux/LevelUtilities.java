package org.jcps.vehicle2redux;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

/**
 * A utility class providing static methods for handling levels and maps.
 * This class contains methods for retrieving level filenames, finding string indices,
 * reading and writing {@code HashMap<String, String>} to and from files, and string manipulations.
 *
 * @since 1.0
 */
public class LevelUtilities {
    /**
     * Retrieves a list of level filenames present in the "Levels" directory of the game's root folder.
     * This method helps in dynamically loading the level files available in the game.
     *
     * @return An {@code ArrayList} containing the filenames of all level files found in the "Levels" directory.
     */
    private static ArrayList<String> getLevelList() {
        ArrayList<String> list = new ArrayList<>();
        URL url = null;
        String urlPath = null;

        try {
            urlPath = "file:///" + System.getProperty("user.dir") + File.separator + "Levels";
            url = new URL(urlPath);
            if (V2RApp.DEBUG) System.out.println("Loading maps from: " + urlPath);
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

        if (files != null) {
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
     * Searches for the index of the first occurrence of a target string within a text string, starting from a specified index.
     * The search can be performed in a case-sensitive or case-insensitive manner based on the provided boolean flag.
     *
     * @param text     The text within which the search is performed.
     * @param target   The target string to search for within the text.
     * @param index    The starting index of the search.
     * @param caseBool A boolean flag indicating whether the search should be case-sensitive (true) or case-insensitive (false).
     * @return The index of the first occurrence of the target string within the text, or -1 if the target is not found.
     */
    private static int find(String text, String target, int index, boolean caseBool) {
        String firstChar = target.substring(0, 1);
        int targetLen = target.length();
        int textLen = text.length();
        if (index >= -1) {
            for (int i = index; i < textLen - targetLen + 1; ++i) {
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
     * Performs a case-insensitive search for the first occurrence of a target string within a text string.
     * This method is a shorthand for invoking find(text, target, 0, true).
     *
     * @param text   The text within which the search is performed.
     * @param target The target string to search for within the text.
     * @return The index of the first occurrence of the target string within the text, or -1 if the target is not found.
     */
    public static int findIgnoreCase(String text, String target) {
        return find(text, target, 0, true);
    }

    /**
     * Retrieves an {@code ArrayList} of {@code LevelMap} objects based on the level files in the "Levels" directory.
     *
     * @return an {@code ArrayList} of {@code LevelMap} objects; the list might be empty if no level files are found in the directory.
     */
    public static ArrayList<LevelMap> getLevelMaps() {
        ArrayList<String> files = getLevelList();
        ArrayList<LevelMap> maps = new ArrayList<>();
        if (V2RApp.DEBUG) System.out.println("Map Files Loaded:");
        for (Object s : files) {
            if (V2RApp.DEBUG) System.out.println(s);
            LevelMap map = new LevelMap("Levels/" + s);
            maps.add(map);
        }
        return maps;
    }

    /**
     * Main method for testing the functionality provided by the {@code LevelUtilities} class.
     * It retrieves a list of {@code LevelMap} objects, prints "Complete" to the console, and then attempts to retrieve and print
     * the "Bild" attribute of the first {@code LevelMap} in the list.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        ArrayList<LevelMap> maps = getLevelMaps();
        if (V2RApp.DEBUG) System.out.println("Complete");
        String test = null;
        String exceptionList = "";
        try {
            test = String.valueOf(maps.get(0).get("Bild"));
        } catch (Exception e) {
            exceptionList += e;
        }
        if (V2RApp.DEBUG) {
            System.out.println(exceptionList);
            System.out.println(test);
        }
    }

    /**
     * Writes the key-value pairs of a {@code HashMap} to a file. Each pair is written in the format "key=value".
     * If the file already exists, it is overwritten; if it does not exist, it is created.
     *
     * @param map      The {@code HashMap} whose contents will be written to the file.
     * @param filename The name of the file to which the {@code HashMap} will be written.
     */
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

    /**
     * Reads a file and returns a {@code LinkedHashMap} containing the key-value pairs found in the file.
     * Each pair is expected to be in the format "key=value".
     *
     * @param filename The name of the file to read.
     * @return A {@code LinkedHashMap} containing the key-value pairs read from the file.
     * If an error occurs during reading, an empty {@code LinkedHashMap} is returned.
     */
    public static LinkedHashMap<String, String> readHashmapFromFile(String filename) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();

        byte[] fileContent;
        try (RandomAccessFile file = new RandomAccessFile(filename, "r")) {
            fileContent = new byte[(int) file.length()];
            file.readFully(fileContent);
        } catch (Exception e) {
            return hashMap;
        }

        String strLength = Arrays.toString(fileContent);
        strLength = stringReplaceCaseInsensitive(strLength, "\r", "\n");
        strLength = stringReplaceCaseInsensitive(strLength, "\n\n", "\n");
        ArrayList<String> arrayList = breakStringIntoWordsSeparatedByStringCaseInsensitive(strLength, "\n");

        assert arrayList != null;
        for (String item : arrayList) {
            ArrayList<String> breakList = breakStringIntoWordsSeparatedByStringCaseInsensitive(item, "=");
            assert breakList != null;
            if (breakList.size() == 2) {
                String key = breakList.get(0).trim();
                String value = breakList.get(1).trim();
                hashMap.put(key, value);
            }
        }
        return hashMap;
    }

    /**
     * Replaces all case-insensitive occurrences of a specified word in a text string with a replacement string.
     *
     * @param text            The original text string.
     * @param word            The word to be replaced.
     * @param replacementChar The replacement string.
     * @return The resulting string after performing the replacement.
     */
    public static String stringReplaceCaseInsensitive(String text, String word, String replacementChar) {
        ArrayList<String> arrList = breakStringIntoWordsSeparatedByStringCaseInsensitive(text, word);

        StringBuilder textBuilder = new StringBuilder();
        for (int i = 0; i < Objects.requireNonNull(arrList).size(); ++i) {
            textBuilder.append(arrList.get(i));
            if (i < arrList.size() - 1) {
                textBuilder.append(replacementChar);
            }
        }
        text = textBuilder.toString();

        return text;
    }

    /**
     * Breaks a text string into an {@code ArrayList} of words separated by a specified word.
     * The separation operation is case-insensitive.
     *
     * @param text The original text string.
     * @param word The word used as a separator to split the text string.
     * @return An {@code ArrayList} of words separated by the specified word.
     * If the original text string is null, returns null.
     * If the word is null or empty, returns an {@code ArrayList} containing the original text string.
     */
    public static ArrayList<String> breakStringIntoWordsSeparatedByStringCaseInsensitive(String text, String word) {
        if (word != null && !word.isEmpty()) {
            if (text == null) {
                return null;
            } else {
                String regEx = "(?i)" + Pattern.quote(word);
                String[] splitStr = text.split(regEx);

                return new ArrayList<>(Arrays.asList(splitStr));
            }
        } else {
            ArrayList<String> arrayList = new ArrayList<>(1);
            arrayList.add(text);
            return arrayList;
        }
    }
}
