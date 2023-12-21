import java.util.HashMap;

/**
 * Class to handle the parameters that the original java applet would have got from the HTML page it was on.
 */
public class GameParams {
    public HashMap<String, String> paramMap;

    /**
     * Constructs a GameParams object based on the original game's default settings.
     */
    GameParams() {
        paramMap = new HashMap<>();
        paramMap.put("Bild_w", "1600");
        paramMap.put("Bild_h", "1200");
        paramMap.put("Bild", "Levels/LevelLong_f.gif");
        paramMap.put("Bild_c", "Levels/LevelLong_c.jpg");
        paramMap.put("GoalX", "1485");
        paramMap.put("GoalY", "370");
        paramMap.put("GoalWidth", "100");
        paramMap.put("GoalHeight", "70");

        paramMap.put("StartX", "80");
        paramMap.put("StartY", "1155");

        paramMap.put("scaleSize", "70");  // percent
        paramMap.put("scaleF", "10");  // percent
        paramMap.put("scaleM", "100"); // percent

        paramMap.put("mHook", "100"); // hundredths
        paramMap.put("mRope", "50");  // hundredths
        paramMap.put("mWheels", "100"); // hundredths
        paramMap.put("mAxis", "200"); // hundredths
        paramMap.put("mCorpus", "200"); // hundredths

        paramMap.put("l0Rope", "2");
        paramMap.put("v0Rope", "1500");

        paramMap.put("FRopeMin", "100");
        paramMap.put("FRopeMax", "4000");
        paramMap.put("FWheels", "5000");
        paramMap.put("FCorpus", "10000");
        paramMap.put("FEngine", "60");

        paramMap.put("Gravity", "70");
        paramMap.put("dt", "25");
        paramMap.put("delay", "10"); // Milliseconds.
        paramMap.put("Reibung", "990"); // Friction in thousandths
        paramMap.put("ReibungW", "965"); // Friction in thousandths
        paramMap.put("width", "800"); // pixels
        paramMap.put("height", "600"); // pixels
    }

    /**
     * Constructs a GameParams object based on the provided HashMap of parameters.
     * The constructor initializes the paramMap with specific key-value pairs from the input HashMap.
     *
     * @param hashMap The HashMap containing key-value pairs representing various game parameters.
     */
    GameParams(HashMap<String, String> hashMap) {
        paramMap = new HashMap<>();
        paramMap.put("Bild_w", hashMap.get("Bild_w"));
        paramMap.put("Bild_h", hashMap.get("Bild_h"));
        paramMap.put("Bild", hashMap.get("Bild"));
        paramMap.put("Bild_c", hashMap.get("Bild_c"));
        paramMap.put("GoalX", hashMap.get("GoalX"));
        paramMap.put("GoalY", hashMap.get("GoalY"));
        paramMap.put("GoalWidth", hashMap.get("GoalWidth"));
        paramMap.put("GoalHeight", hashMap.get("GoalHeight"));

        paramMap.put("StartX", hashMap.get("StartX"));
        paramMap.put("StartY", hashMap.get("StartY"));

        paramMap.put("scaleSize", hashMap.get("scaleSize"));  // percent
        paramMap.put("scaleF", hashMap.get("scaleF"));  // percent
        paramMap.put("scaleM", hashMap.get("scaleM")); // percent

        paramMap.put("mHook", hashMap.get("mHook"));   // hundredths
        paramMap.put("mRope", hashMap.get("mRope"));    // hundredths
        paramMap.put("mWheels", hashMap.get("mWheels")); // hundredths
        paramMap.put("mAxis", hashMap.get("mAxis"));   // hundredths
        paramMap.put("mCorpus", hashMap.get("mCorpus")); // hundredths

        paramMap.put("l0Rope", hashMap.get("l0Rope"));
        paramMap.put("v0Rope", hashMap.get("v0Rope"));

        paramMap.put("FRopeMin", hashMap.get("FRopeMin"));
        paramMap.put("FRopeMax", hashMap.get("FRopeMax"));
        paramMap.put("FWheels", hashMap.get("FWheels"));
        paramMap.put("FCorpus", hashMap.get("FCorpus"));
        paramMap.put("FEngine", hashMap.get("FEngine"));

        paramMap.put("Gravity", hashMap.get("Gravity"));
        paramMap.put("dt", hashMap.get("dt"));
        paramMap.put("delay", hashMap.get("delay")); // Milliseconds.
        paramMap.put("Reibung", hashMap.get("Reibung")); // Friction in thousandths
        paramMap.put("ReibungW", hashMap.get("ReibungW")); // Friction in thousandths
    }

    /**
     * Retrieves an integer value associated with the specified key from the parameter map.
     *
     * @param key The key whose associated integer value is to be retrieved.
     * @return The integer value associated with the specified key.
     * @throws NumberFormatException If the value associated with the key cannot be parsed as an integer.
     */
    public int getInt(String key) {
        return Integer.parseInt(paramMap.get(key));
    }

    /**
     * Associates the specified integer value with the specified key in the parameter map.
     *
     * @param key The key with which the specified integer value is to be associated.
     * @param i   The integer value to be associated with the specified key.
     */
    public void putInt(String key, int i) {
        paramMap.put(key, String.valueOf(i));
    }
}
