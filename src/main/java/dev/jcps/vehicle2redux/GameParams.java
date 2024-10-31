package dev.jcps.vehicle2redux;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * The {@code GameParams} class serves as a central repository for managing game parameters,
 * originally received from the HTML page when the game was an applet. It maintains a HashMap
 * to store level variables and game parameters.
 * <p>
 * The parameters managed by this class control various aspects of the game's behaviour and
 * appearance, such as gravity, friction, dimensions, colours, and initial settings of game objects.
 * </p>
 * <p>
 * <i>Note: Changes to the parameters in this class will affect the game globally.</i>
 * </p>
 *
 * @author neoFuzz
 * @since 1.0
 */
public class GameParams {
    public static final String BILD_W = "Bild_w";
    public static final String BILD_H = "Bild_h";
    public static final String BILD = "Bild";
    public static final String BILD_C = "Bild_c";
    public static final String GOAL_X = "GoalX";
    public static final String GOAL_Y = "GoalY";
    public static final String GOAL_WIDTH = "GoalWidth";
    public static final String GOAL_HEIGHT = "GoalHeight";
    public static final String START_X = "StartX";
    public static final String START_Y = "StartY";
    public static final String SCALE_SIZE = "scaleSize";
    public static final String SCALE_F = "scaleF";
    public static final String SCALE_M = "scaleM";
    public static final String M_HOOK = "mHook";
    public static final String M_ROPE = "mRope";
    public static final String M_WHEELS = "mWheels";
    public static final String M_AXIS = "mAxis";
    public static final String M_CORPUS = "mCorpus";
    public static final String L_0_ROPE = "l0Rope";
    public static final String V_0_ROPE = "v0Rope";
    public static final String F_ROPE_MIN = "FRopeMin";
    public static final String F_ROPE_MAX = "FRopeMax";
    public static final String F_WHEELS = "FWheels";
    public static final String F_CORPUS = "FCorpus";
    public static final String F_ENGINE = "FEngine";
    public static final String DT = "dt";
    public static final String DELAY = "delay";
    public static final String REIBUNG = "Reibung";
    public static final String REIBUNG_W = "ReibungW";
    public static final String GRAVITY = "Gravity";
    /**
     * A HashMap that stores key-value pairs representing various game parameters.
     * The keys are String identifiers for the parameters, and the values are their corresponding settings.
     */
    @SuppressWarnings("all")
    public HashMap<String, String> paramMap;

    /**
     * Constructs a {@code GameParams} object and initialises it with the default game settings,
     * represented as key-value pairs in the {@code paramMap}.
     */
    GameParams() {
        paramMap = new HashMap<>();
        paramMap.put(BILD_W, "1600");
        paramMap.put(BILD_H, "1200");
        paramMap.put(BILD, "Levels/LevelLong_f.gif");
        paramMap.put(BILD_C, "Levels/LevelLong_c.jpg");
        paramMap.put(GOAL_X, "1485");
        paramMap.put(GOAL_Y, "370");
        paramMap.put(GOAL_WIDTH, "100");
        paramMap.put(GOAL_HEIGHT, "70");

        paramMap.put(START_X, "80");
        paramMap.put(START_Y, "1155");

        paramMap.put(SCALE_SIZE, "70");  // percent
        paramMap.put(SCALE_F, "10");  // percent
        paramMap.put(SCALE_M, "100"); // percent

        paramMap.put(M_HOOK, "100"); // hundredths
        paramMap.put(M_ROPE, "50");  // hundredths
        paramMap.put(M_WHEELS, "100"); // hundredths
        paramMap.put(M_AXIS, "200"); // hundredths
        paramMap.put(M_CORPUS, "200"); // hundredths

        paramMap.put(L_0_ROPE, "2");
        paramMap.put(V_0_ROPE, "1500");

        paramMap.put(F_ROPE_MIN, "100");
        paramMap.put(F_ROPE_MAX, "4000");
        paramMap.put(F_WHEELS, "5000");
        paramMap.put(F_CORPUS, "10000");
        paramMap.put(F_ENGINE, "60");

        paramMap.put(GRAVITY, "70");
        paramMap.put(DT, "25");
        paramMap.put(DELAY, "10"); // Milliseconds.
        paramMap.put(REIBUNG, "990"); // Friction in thousandths
        paramMap.put(REIBUNG_W, "965"); // Friction in thousandths
        paramMap.put("width", "800"); // pixels
        paramMap.put("height", "600"); // pixels
    }

    /**
     * Constructs a {@code GameParams} object and initialises it with a given set of game settings.
     * The settings are provided as a HashMap of key-value pairs, which are then stored in the paramMap.
     *
     * @param hashMap The HashMap containing key-value pairs representing various game parameters.
     */
    GameParams(@NotNull HashMap<String, String> hashMap) {
        paramMap = new GameParams().paramMap;
        if (hashMap.isEmpty() && V2RApp.isDebug()) {
            V2RApp.logger.error("GameParam Error! defaults will be used.");
        } else {
            paramMap.put(BILD_W, hashMap.get(BILD_W));
            paramMap.put(BILD_H, hashMap.get(BILD_H));
            paramMap.put(BILD, hashMap.get(BILD));
            paramMap.put(BILD_C, hashMap.get(BILD_C));
            paramMap.put(GOAL_X, hashMap.get(GOAL_X));
            paramMap.put(GOAL_Y, hashMap.get(GOAL_Y));
            paramMap.put(GOAL_WIDTH, hashMap.get(GOAL_WIDTH));
            paramMap.put(GOAL_HEIGHT, hashMap.get(GOAL_HEIGHT));

            paramMap.put(START_X, hashMap.get(START_X));
            paramMap.put(START_Y, hashMap.get(START_Y));

            paramMap.put(SCALE_SIZE, hashMap.get(SCALE_SIZE));  // percent
            paramMap.put(SCALE_F, hashMap.get(SCALE_F));  // percent
            paramMap.put(SCALE_M, hashMap.get(SCALE_M)); // percent

            paramMap.put(M_HOOK, hashMap.get(M_HOOK));   // hundredths
            paramMap.put(M_ROPE, hashMap.get(M_ROPE));    // hundredths
            paramMap.put(M_WHEELS, hashMap.get(M_WHEELS)); // hundredths
            paramMap.put(M_AXIS, hashMap.get(M_AXIS));   // hundredths
            paramMap.put(M_CORPUS, hashMap.get(M_CORPUS)); // hundredths

            paramMap.put(L_0_ROPE, hashMap.get(L_0_ROPE));
            paramMap.put(V_0_ROPE, hashMap.get(V_0_ROPE));

            paramMap.put(F_ROPE_MIN, hashMap.get(F_ROPE_MIN));
            paramMap.put(F_ROPE_MAX, hashMap.get(F_ROPE_MAX));
            paramMap.put(F_WHEELS, hashMap.get(F_WHEELS));
            paramMap.put(F_CORPUS, hashMap.get(F_CORPUS));
            paramMap.put(F_ENGINE, hashMap.get(F_ENGINE));

            paramMap.put(GRAVITY, hashMap.get(GRAVITY));
            paramMap.put(DT, hashMap.get(DT));
            paramMap.put(DELAY, hashMap.get(DELAY)); // Milliseconds.
            paramMap.put(REIBUNG, hashMap.get(REIBUNG)); // Friction in thousandths
            paramMap.put(REIBUNG_W, hashMap.get(REIBUNG_W)); // Friction in thousandths
        }
    }

    /**
     * Retrieves an integer value associated with a specific key from the paramMap.
     *
     * @param key The key associated with the integer value to be retrieved.
     * @return The integer value associated with the specified key.
     * @throws NumberFormatException If the value associated with the key cannot be parsed as an integer.
     */
    public int getInt(String key) {
        return Integer.parseInt(paramMap.get(key));
    }

    /**
     * Associates a specific integer value with a specific key in the paramMap.
     * If the key is already present in the paramMap, this will update its associated value.
     *
     * @param key The key with which the specified integer value is to be associated.
     * @param i   The integer value to be associated with the specified key.
     */
    public void putInt(String key, int i) {
        paramMap.put(key, String.valueOf(i));
    }
}
