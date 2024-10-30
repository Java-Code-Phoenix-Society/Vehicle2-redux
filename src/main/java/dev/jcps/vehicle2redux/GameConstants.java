package dev.jcps.vehicle2redux;

/**
 * The {@code GameConstants} class holds static constants representing RGB colour codes used in the game.
 * <p>
 * This class provides a centralised place to manage the colour codes for various elements in the game,
 * ensuring consistency in the game's level data representation. This includes elements such as air, water, ground,
 * and two specific elements, referred to here as (A) and (B).
 * </p>
 * <p>
 * <i>Note: The colour codes are in RGB format.</i>
 * </p>
 *
 * @author neoFuzz
 * @since 1.0
 */
public class GameConstants {
    private GameConstants() {
        // hidden
    }

    /**
     * The RGB colour code representing 'air' in the game. This colour is used to distinguish 'air' from other elements.
     */
    public static final int AIR_COLOR = 0xFFFFFF;

    /**
     * The RGB colour code representing 'water' in the game. This colour helps the game identify 'water' areas
     * in the level.
     */
    public static final int WATER_COLOR = 11129855;

    /**
     * The RGB colour code representing 'ground' in the game. This colour help identify 'ground' areas within the level.
     */
    public static final int GROUND_COLOR = 10643504;

    /**
     * The RGB colour code representing a specific element 'a' in the game. This colour is used to distinguish
     * element 'a' from other elements.
     */
    public static final int ELEMENT_A = 0x828181;

    /**
     * The RGB colour code representing another specific element 'b' in the game. This colour is used to distinguish
     * element 'b' from other elements.
     */
    public static final int ELEMENT_B = 8023138;
}
