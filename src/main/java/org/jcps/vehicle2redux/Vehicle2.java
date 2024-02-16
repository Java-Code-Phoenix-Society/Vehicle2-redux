package org.jcps.vehicle2redux;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;

/**
 * Represents the main panel for the Vehicle2 game functions and implements ActionListener for event handling.
 */
public class Vehicle2 extends JPanel implements ActionListener {
    // Fields
    /**
     * Static map of level times stored as key-value pairs.
     */
    static LinkedHashMap<String, String> levelTimes;
    /**
     * The delay time for staying in the goal state (in milliseconds).
     */
    private final long delay = 5000; // 5 seconds: Time to stay in the goal
    /**
     * The list of TriggerListeners for handling game events.
     */
    final private ArrayList<TriggerListener> listeners = new ArrayList<>();
    /**
     * The current level in the game.
     */
    public int currentLevel;
    /**
     * The game parameters object containing various settings.
     */
    public GameParams gp;
    /**
     * Represents the run state of the game.
     */
    public boolean runState = false;
    /**
     * The list of level maps containing level information.
     */
    public ArrayList<LevelMap> maps;
    /**
     * Represents the state of the cursor.
     */
    boolean cursorState = false;
    /**
     * Represents the state of the mouse.
     */
    boolean mouseState = false;
    /**
     * Represents the state of the Shift key.
     */
    boolean shiftPressed = false;
    /**
     * A counter for game events.
     */
    int gameCounter = 0;
    /**
     * Indicates the readiness of graphics.
     */
    int graphicsReady = 0;
    /**
     * The width of the screen.
     */
    int screenWidth;
    /**
     * The height of the screen.
     */
    int screenHeight;
    /**
     * The x-coordinate of the mouse.
     */
    int mouseX = 0;
    /**
     * The y-coordinate of the mouse.
     */
    int mouseY = 0;
    /**
     * The start time of the current level.
     */
    long levelStartTime;
    /**
     * The graphics object for rendering.
     */
    Graphics graphics;
    /**
     * The background image.
     */
    Image imgBG;
    /**
     * The screen buffer image.
     */
    Image screenBuffer;
    /**
     * The tile image.
     */
    Image tileImg;
    /**
     * The media tracker for loading images.
     */
    MediaTracker tracker;
    /**
     * The player vehicle object.
     */
    PlayerVehicle pVehicle;
    /**
     * The world parameters object.
     */
    WorldParameters worldParameters;
    /**
     * The x-coordinate of the level time.
     */
    double lxt = 0.0;
    /**
     * The y-coordinate of the level time.
     */
    double lyt = 0.0;
    /**
     * The start time of the game instance.
     */
    long startTime = System.currentTimeMillis();
    /**
     * Represents whether the vehicle is inside the goal.
     */
    boolean insideGoal = false;


    /**
     * Constructs a new instance of the Vehicle2 class.
     * Initializes the object with default values and sets up the initial level.
     * The default size of the object is set to 800x600 pixels.
     */
    public Vehicle2() {
        currentLevel = 0; // Set the initial level to 0
        levelTimes = new LinkedHashMap<>(); // Initialize the levelTimes HashMap
        maps = LevelUtilities.getLevelMaps(); // Retrieve level maps using LevelUtilities and set the maps variable
        setSize(800, 600); // Set the default size of the Vehicle2 object to 800x600 pixels
    }

    /**
     * Adds an event listener to the list of listeners.
     *
     * @param listener The TriggerListener to be added.
     */
    public void addEventListener(TriggerListener listener) {
        listeners.add(listener);
    }

    /**
     * Fires an event with the given message to all registered listeners.
     *
     * @param message The message associated with the triggered event.
     */
    public void fireEvent(String message) {
        TriggerEvent event = new TriggerEvent(this, message);
        for (TriggerListener listener : listeners) {
            listener.onEventOccurred(event);
        }
    }

    /**
     * Passes through to paintComponent(graphics)
     */
    public void paint(Graphics graphics) {
        this.paintComponent(graphics);
    }

    /**
     * Passes through to paintComponent(graphics)
     */
    public void update(Graphics graphics) {
        this.paintComponent(graphics);
    }

    /**
     * Initializes the game components, sets up the world, and starts the timer.
     * This method is responsible for initializing various parameters, loading images,
     * creating game objects, and preparing the game world for execution.
     */
    public void init() {
        // Retrieve the current level parameters
        gp = maps.get(currentLevel).lp;

        // Initialize variables for positioning and dimensions
        int partIndex, wheelOffset = 43, corpusOffset = 83;
        int ropeSegmentOffset = 40, axisOffset;
        double axisPosition = 0.0, corpusPosition = 0.0;

        // Get the dimensions of the screen
        screenWidth = this.getParent().getSize().width;
        screenHeight = this.getParent().getSize().height;

        // Create screen buffer and graphics context
        screenBuffer = this.createImage(this.screenWidth, this.screenHeight);
        graphics = this.screenBuffer.getGraphics();

        // Initialize game objects
        worldParameters = new WorldParameters();
        pVehicle = new PlayerVehicle(wheelOffset, corpusOffset);
        tracker = new MediaTracker(this);

        // Load tile image
        tileImg = loadImage("Bild");
        tracker.addImage(this.tileImg, 0);
        tracker.checkID(0, true);

        // Load background image
        imgBG = loadImage("Bild_c");
        tracker.addImage(this.tileImg, 1);
        tracker.checkID(1, true);

        processAndMapPixels();

        // Define color values
        Color greenColor = new Color(0, 150, 0);
        Color orangeColor = new Color(255, 200, 50);
        Color redColor = new Color(255, 0, 0);

        // Create vehicle parts and connectors
        for (axisOffset = 0; axisOffset < 2; axisOffset++) {
            double xPartOffset = axisPosition + (axisOffset * 80);
            for (partIndex = 0; partIndex < pVehicle.maxRopeSegments; partIndex++) {
                double angleIncrement = Math.PI * (partIndex / 5.0);
                this.pVehicle.partList[this.pVehicle.bodyPart] =
                        new Connector(xPartOffset + (double) ropeSegmentOffset * Math.cos(angleIncrement),
                                corpusPosition + (double) ropeSegmentOffset * Math.sin(angleIncrement),
                                this.pVehicle.mWheels * this.worldParameters.scaleM, greenColor);
                ++this.pVehicle.bodyPart;
            }
            partIndex = 0;
            while (partIndex < pVehicle.maxRopeSegments) {
                this.pVehicle.vParts[this.pVehicle.ropePart] =
                        new VehiclePart(partIndex + 11 * axisOffset, (partIndex + 1) % pVehicle.maxRopeSegments + 11 * axisOffset,
                                this.worldParameters.scaleF * this.pVehicle.fWheels, orangeColor);
                ++this.pVehicle.ropePart;
                this.pVehicle.vParts[this.pVehicle.ropePart] =
                        new VehiclePart(partIndex + 11 * axisOffset, (partIndex + 3) % pVehicle.maxRopeSegments + 11 * axisOffset,
                                this.worldParameters.scaleF * this.pVehicle.fWheels, orangeColor);
                ++this.pVehicle.ropePart;
                ++partIndex;
            }
            this.pVehicle.partList[this.pVehicle.bodyPart] =
                    new Connector(xPartOffset, corpusPosition, this.worldParameters.scaleM * this.pVehicle.mAxis,
                            greenColor);
            ++this.pVehicle.bodyPart;
            partIndex = 0;
            while (partIndex < pVehicle.maxRopeSegments) {
                this.pVehicle.vParts[this.pVehicle.ropePart] =
                        new VehiclePart(partIndex + 11 * axisOffset, this.pVehicle.bodyPart - 1,
                                this.worldParameters.scaleF * this.pVehicle.fWheels, orangeColor);
                ++this.pVehicle.ropePart;
                ++partIndex;
            }

        }
        this.pVehicle.partList[this.pVehicle.bodyPart] =
                new Connector(axisPosition + 40.0, corpusPosition, this.worldParameters.scaleM * this.pVehicle.mCorpus,
                        greenColor);
        ++this.pVehicle.bodyPart;
        this.pVehicle.pCounter = this.pVehicle.bodyPart - 1;
        this.pVehicle.vParts[this.pVehicle.ropePart] =
                new VehiclePart(10, this.pVehicle.bodyPart - 1,
                        this.worldParameters.scaleF * this.pVehicle.fCorpus, redColor);
        ++this.pVehicle.ropePart;
        this.pVehicle.vParts[this.pVehicle.ropePart] =
                new VehiclePart(21, this.pVehicle.bodyPart - 1,
                        this.worldParameters.scaleF * this.pVehicle.fCorpus, redColor);
        ++this.pVehicle.ropePart;
        this.pVehicle.vParts[this.pVehicle.ropePart] =
                new VehiclePart(10, 21, this.worldParameters.scaleF * this.pVehicle.fCorpus, redColor);
        this.pVehicle.ropeSegments[0] = this.pVehicle.bodyPart;
        this.pVehicle.ropeAnchor[0] = ++this.pVehicle.ropePart;

        // Adjust positions and scales
        partIndex = 0;
        while (partIndex < this.pVehicle.maxRopeSegments) {
            processPart(partIndex, greenColor);
            this.pVehicle.vParts[this.pVehicle.ropePart] = partIndex == 0 ?
                    new VehiclePart(22, this.pVehicle.ropeSegments[0], this.pVehicle.ropeMin,
                            new Color(0, 80, 185)) :
                    new VehiclePart(this.pVehicle.ropeSegments[0] + partIndex - 1, this.pVehicle.ropeSegments[0] + partIndex,
                            this.pVehicle.ropeMin, new Color(0, 80, 185));

            this.pVehicle.vParts[this.pVehicle.ropePart].sag = this.worldParameters.scaleSize *
                    (double) gp.getInt("l0Rope");
            this.pVehicle.vParts[this.pVehicle.ropePart].partActive = false;
            ++this.pVehicle.ropePart;
            ++partIndex;
        }
        this.pVehicle.ropeSegments[1] = this.pVehicle.bodyPart;
        this.pVehicle.ropeAnchor[1] = this.pVehicle.ropePart;
        partIndex = 0;
        while (partIndex < this.pVehicle.maxRopeSegments) {
            processPart(partIndex, greenColor);
            this.pVehicle.vParts[this.pVehicle.ropePart] = partIndex == 0 ?
                    new VehiclePart(22, this.pVehicle.ropeSegments[1],
                            this.pVehicle.ropeMin, new Color(70, 170, 255)) :
                    new VehiclePart(this.pVehicle.ropeSegments[1] + partIndex - 1, this.pVehicle.ropeSegments[1] + partIndex,
                            this.pVehicle.ropeMin, new Color(70, 170, 255));
            this.pVehicle.vParts[this.pVehicle.ropePart].sag = this.worldParameters.scaleSize *
                    (double) gp.getInt("l0Rope");
            this.pVehicle.vParts[this.pVehicle.ropePart].partActive = false;
            ++this.pVehicle.ropePart;
            ++partIndex;
        }
        partIndex = 0;
        while (partIndex < this.pVehicle.bodyPart) {
            this.pVehicle.partList[partIndex].x *= this.worldParameters.scaleSize;
            this.pVehicle.partList[partIndex].y *= this.worldParameters.scaleSize;
            this.pVehicle.partList[partIndex].x += gp.getInt("StartX");
            this.pVehicle.partList[partIndex].y += gp.getInt("StartY");
            ++partIndex;
        }
        partIndex = 0;
        while (partIndex < this.pVehicle.ropePart) {
            this.pVehicle.vParts[partIndex].sag *= this.worldParameters.scaleSize;
            ++partIndex;
        }

        // Print map information
        if (V2RApp.DEBUG)
            System.out.println("Map: " + gp.paramMap.get("Bild_c") + ", nf=" + this.pVehicle.ropePart + ", np=" + this.pVehicle.bodyPart);

        // Set the initial position of the game world
        this.worldParameters.wpX = this.pVehicle.partList[this.pVehicle.pCounter].x - ((double) this.screenWidth / 2);
        this.worldParameters.wpY = this.pVehicle.partList[this.pVehicle.pCounter].y - ((double) this.screenHeight / 2);

        // Add the control listeners once. This could probably be done better.
        if (graphicsReady == 0) {
            this.addMouseListener(new xMA());
            this.addMouseMotionListener(new MouseControls());
            this.getRootPane().getParent().addKeyListener(new GameControls());
        }
        this.graphicsReady = 1; // Set graphics readiness flag

        // Start the timer and set the run state to true
        levelStartTime = System.currentTimeMillis();
        this.runState = true;
    }

    /**
     * Processes a vehicle part and updates the part list with a new Connector.
     *
     * @param coOrd Starting co-ordinate.
     * @param color The color of the vehicle part.
     */
    private void processPart(int coOrd, Color color) {
        this.pVehicle.partList[this.pVehicle.bodyPart] =
                new Connector(coOrd, coOrd, this.pVehicle.mRope * this.worldParameters.scaleM, color);
        if (coOrd == this.pVehicle.maxRopeSegments - 1) {
            this.pVehicle.partList[this.pVehicle.bodyPart].angle = this.worldParameters.scaleM * this.pVehicle.mHook;
        }
        this.pVehicle.partList[this.pVehicle.bodyPart].gToggle = false;
        ++this.pVehicle.bodyPart;
    }

    /**
     * Processes and maps pixels from the tile image to characters in the level map.
     * Uses parallel processing for improved performance.
     * <p>
     * <b>Implementation details:</b><br>
     * This function grabs the pixels from the tile image using PixelGrabber,
     * processes each pixel in parallel, and maps it to a character using the mapPixelToChar function.
     * The resulting characters are then stored in the level map.
     * <i>Note: Ensure that the mapPixelToChar function is appropriately defined for accurate mapping.</i>
     * <br><b>Example usage:<br></b>
     * {@code processAndMapPixels(); // This function is typically called when initializing the level map.}
     * <p><i>Caution: Since parallel processing is used, ensure that operations on shared resources are thread-safe.</i></p>
     */
    public void processAndMapPixels() {
        int levelWidth = this.worldParameters.levelWidth;
        int levelHeight = this.worldParameters.levelHeight;
        int[] nArray = new int[levelWidth * levelHeight];

        PixelGrabber pixelGrabber = new PixelGrabber(this.tileImg, 0, 0,
                levelWidth, levelHeight, nArray, 0, levelWidth);

        try {
            pixelGrabber.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("Interrupted waiting for pixels!");
            Thread.currentThread().interrupt();
            return;
        }

        // Wait for all images to be loaded
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Use parallel stream for processing the pixels
        IntStream.range(0, nArray.length).parallel().forEach(n -> {
            int pixel = nArray[n];
            int red = (pixel >> 16) & 0xFF;
            int green = (pixel >> 8) & 0xFF;
            int blue = pixel & 0xFF;

            // Map pixel values to characters
            char mappedChar = mapPixelToChar(red, green, blue);

            // Since we are using parallel processing, ensure that the operation on the shared resource is thread-safe
            synchronized (this.worldParameters.levelMap) {
                this.worldParameters.levelMap[n % levelWidth][n / levelWidth] = mappedChar;
            }
        });
    }

    /**
     * <p>Maps RGB values of a pixel to a corresponding character based on predefined color constants.</p>
     * <br><p>
     * <b>Implementation details:</b><br>
     * This function combines the RGB values of a pixel into a single integer representation.
     * It then checks this integer value against predefined color constants to determine the corresponding character.
     * </p><br>
     * <p>Returns:
     * <ul>
     *     <li>'l' for air color</li>
     *     <li>'w' for water color</li>
     *     <li>'e' for ground color</li>
     *     <li>'f' for color 'a'</li>
     *     <li>'E' for color 'b'</li>
     *     <li>'F' for any other color</li>
     * </ul>
     * </p>
     * <p>
     *     Example usage:<br>
     *     {@code char mappedChar = mapPixelToChar(255, 0, 0); // Assuming red color}
     *     <br>Result: 'F' since red is not one of the predefined colors
     * </p>
     * <p>
     * <i>Note: Ensure that the color constants in GameConstants are appropriately defined for accurate mapping.</i>
     *
     * @param red   The red component of the pixel color (0-255).
     * @param green The green component of the pixel color (0-255).
     * @param blue  The blue component of the pixel color (0-255).
     * @return The character mapped based on the predefined color constants.
     */
    private char mapPixelToChar(int red, int green, int blue) {
        int pixelValue = (red << 16) | (green << 8) | blue;
        if (pixelValue == GameConstants.AIR_COLOR) {
            return 'l';
        } else if (pixelValue == GameConstants.WATER_COLOR) {
            return 'w';
        } else if (pixelValue == GameConstants.GROUND_COLOR) {
            return 'e';
        } else if (pixelValue == GameConstants.ELEMENT_A) {
            return 'f';
        } else if (pixelValue == GameConstants.ELEMENT_B) {
            return 'E';
        }
        return 'F';
    }

    /**
     * Paint the game
     */
    public void paintComponent(Graphics graphics) {
        if (this.graphicsReady > 0) {
            graphics.drawImage(this.screenBuffer, 0, 0, this); // Draw game image
        }
    }

    /**
     * Function attached to the timer to run the game. Timer acts as a loop.
     */
    public void run() {
        final double SMOOTHING_FACTOR = 0.99;
        final double ADJUSTMENT_FACTOR = 0.01;
        final double VELOCITY_MULTIPLIER = 3.0;
        final int PARTS_COUNT = 20;
        final double ENGINE_FORCE = this.pVehicle.fEngine;

        // Main loop
        this.worldParameters.x = this.worldParameters.wpX;
        this.worldParameters.y = this.worldParameters.wpY;
        double d4 = 0.0;
        double d5 = 0.0;
        int n = 0;
        while (n < PARTS_COUNT) {
            d4 += this.pVehicle.partList[n].dx;
            d5 += this.pVehicle.partList[n].dy;
            ++n;
        }
        Connector c = this.pVehicle.partList[this.pVehicle.pCounter];
        this.worldParameters.wpX = SMOOTHING_FACTOR * this.worldParameters.x + ADJUSTMENT_FACTOR *
                (c.x + VELOCITY_MULTIPLIER * (d4 /= 20.0) - (double) (this.screenWidth / 2));
        this.worldParameters.wpY = SMOOTHING_FACTOR * this.worldParameters.y + ADJUSTMENT_FACTOR *
                (c.y + VELOCITY_MULTIPLIER * (d5 /= 20.0) - (double) (this.screenHeight / 2));
        this.worldParameters.viewportX = (int) this.worldParameters.wpX;
        this.worldParameters.viewportY = (int) this.worldParameters.wpY;
        if (this.worldParameters.viewportX < 0) {
            this.worldParameters.viewportX = 0;
        } else if (this.worldParameters.viewportX > this.worldParameters.levelWidth - this.screenWidth) {
            this.worldParameters.viewportX = this.worldParameters.levelWidth - this.screenWidth;
        }
        if (this.worldParameters.viewportY < 0) {
            this.worldParameters.viewportY = 0;
        } else if (this.worldParameters.viewportY > this.worldParameters.levelHeight - this.screenHeight) {
            this.worldParameters.viewportY = this.worldParameters.levelHeight - this.screenHeight;
        }

        // Draw Background
        this.graphics.drawImage(
                this.imgBG, 0, 0, this.screenWidth, this.screenHeight,
                this.worldParameters.viewportX, this.worldParameters.viewportY,
                this.worldParameters.viewportX + this.screenWidth,
                this.worldParameters.viewportY + this.screenHeight, this);
        this.pVehicle.drawVehicle();
        this.graphics.setColor(Color.black);

        this.graphics.drawLine(
                (int) c.x - this.worldParameters.viewportX,
                (int) (c.y) - this.worldParameters.viewportY,
                (int) (c.x - this.worldParameters.viewportX + 10.0 * Math.cos(this.pVehicle.turretAngle)),
                (int) (c.y - this.worldParameters.viewportY + 10.0 * Math.sin(this.pVehicle.turretAngle)));

        // Handle hooks and rope
        if (this.worldParameters.fireHook && this.pVehicle.activeRope[this.pVehicle.ropeSlot]) {
            n = 0;
            while (n < this.pVehicle.maxRopeSegments) {
                this.pVehicle.partList[this.pVehicle.ropeSegments[this.pVehicle.ropeSlot] + n].gToggle = false;
                this.pVehicle.vParts[this.pVehicle.ropeAnchor[this.pVehicle.ropeSlot] + n].partActive = false;
                ++n;
            }
            this.pVehicle.activeRope[this.pVehicle.ropeSlot] = false;
            this.worldParameters.fireHook = false;
        }
        if (this.pVehicle.activeRope[this.pVehicle.ropeSlot] && this.worldParameters.windRope) {
            n = this.pVehicle.ropeAnchor[this.pVehicle.ropeSlot];
            while (n < this.pVehicle.ropeAnchor[this.pVehicle.ropeSlot] + this.pVehicle.maxRopeSegments) {
                if (!(this.pVehicle.vParts[n].rLength < this.pVehicle.ropeMax)) break;
                this.pVehicle.vParts[n].rLength *= 1.1;
                ++n;
            }
            this.worldParameters.windRope = false;
        }
        if (this.pVehicle.activeRope[this.pVehicle.ropeSlot] && this.worldParameters.unwindRope) {
            int ropeSlot = this.pVehicle.ropeSlot;
            int startAnchor = this.pVehicle.ropeAnchor[ropeSlot];
            int endAnchor = startAnchor + this.pVehicle.maxRopeSegments;
            for (int i = startAnchor; i < endAnchor; i++) {
                if (this.pVehicle.vParts[i].rLength > this.pVehicle.ropeMin) {
                    this.pVehicle.vParts[i].rLength *= 0.9;
                }
            }
            this.worldParameters.unwindRope = false;
        }
        if (this.worldParameters.fireHook && !this.pVehicle.activeRope[this.pVehicle.ropeSlot]) {
            this.pVehicle.inactiveRope[this.pVehicle.ropeSlot] = false;
            this.pVehicle.activeRope[this.pVehicle.ropeSlot] = true;
            this.worldParameters.fireHook = false;
            n = 0;
            while (n < this.pVehicle.maxRopeSegments) {
                this.pVehicle.partList[this.pVehicle.ropeSegments[this.pVehicle.ropeSlot] + n].gToggle = true;
                this.pVehicle.vParts[this.pVehicle.ropeAnchor[this.pVehicle.ropeSlot] + n].partActive = true;
                this.pVehicle.partList[this.pVehicle.ropeSegments[this.pVehicle.ropeSlot] + n].x =
                        c.x + (double) ((1 + n) * 2) *
                                Math.cos(this.pVehicle.turretAngle);
                this.pVehicle.partList[this.pVehicle.ropeSegments[this.pVehicle.ropeSlot] + n].y =
                        c.y + (double) ((1 + n) * 2) *
                                Math.sin(this.pVehicle.turretAngle);
                this.pVehicle.vParts[this.pVehicle.ropeAnchor[this.pVehicle.ropeSlot] + n].rLength =
                        this.pVehicle.ropeMin;
                ++n;
            }
            this.pVehicle.partList[this.pVehicle.ropeSegments[this.pVehicle.ropeSlot] +
                    this.pVehicle.maxRopeSegments - 1].dy = this.worldParameters.scaleSize *
                    this.pVehicle.v0Rope * Math.sin(this.pVehicle.turretAngle);
            this.pVehicle.partList[this.pVehicle.ropeSegments[this.pVehicle.ropeSlot] +
                    this.pVehicle.maxRopeSegments - 1].dx = this.worldParameters.scaleSize *
                    this.pVehicle.v0Rope * Math.cos(this.pVehicle.turretAngle);
        }

        // Process movement
        if (this.worldParameters.leftPressed || this.worldParameters.rightPressed) {
            int n2 = this.worldParameters.leftPressed ? 1 : -1;
            int n3 = 0;
            while (n3 < 2) {
                n = 0;
                while (n < pVehicle.maxRopeSegments) {
                    int n4 = n + 11 * n3;
                    this.pVehicle.partList[n4].dy +=
                            (this.pVehicle.partList[pVehicle.maxRopeSegments + 11 * n3].x -
                                    this.pVehicle.partList[n4].x) * ENGINE_FORCE * (double) n2;
                    this.pVehicle.partList[n4].dx +=
                            (this.pVehicle.partList[n4].y - this.pVehicle.partList[pVehicle.maxRopeSegments +
                                    11 * n3].y) * ENGINE_FORCE * (double) n2;
                    ++n;
                }
                ++n3;
            }
        }
        n = 0;
        while (n < this.pVehicle.ropePart) {
            if (this.pVehicle.vParts[n].partActive) {
                double d6 = this.pVehicle.partList[this.pVehicle.vParts[n].x].x -
                        this.pVehicle.partList[this.pVehicle.vParts[n].y].x;
                double d7 = this.pVehicle.partList[this.pVehicle.vParts[n].x].y -
                        this.pVehicle.partList[this.pVehicle.vParts[n].y].y;
                double d8 = Math.sqrt(d6 * d6 + d7 * d7);
                double d9 = this.pVehicle.vParts[n].rLength * (d8 - this.pVehicle.vParts[n].sag);
                double d10 = d9 / this.pVehicle.partList[this.pVehicle.vParts[n].x].angle;
                this.pVehicle.partList[this.pVehicle.vParts[n].x].dx -= this.worldParameters.dt * d10 * (d6 /= d8);
                this.pVehicle.partList[this.pVehicle.vParts[n].x].dy -= this.worldParameters.dt * d10 * (d7 /= d8);
                d10 = d9 / this.pVehicle.partList[this.pVehicle.vParts[n].y].angle;
                this.pVehicle.partList[this.pVehicle.vParts[n].y].dx += this.worldParameters.dt * d10 * d6;
                this.pVehicle.partList[this.pVehicle.vParts[n].y].dy += this.worldParameters.dt * d10 * d7;
            }
            ++n;
        }
        n = 0;
        while (n < this.pVehicle.bodyPart) {
            if (this.pVehicle.partList[n].gToggle) {
                char c2;
                if (this.worldParameters.checkPosition((int) this.pVehicle.partList[n].x,
                        (int) this.pVehicle.partList[n].y) == 'w') {
                    this.pVehicle.partList[n].dy += (0.5 - this.pVehicle.buoyancy) *
                            this.worldParameters.dt * this.worldParameters.gravity;
                    this.pVehicle.partList[n].dx *= this.worldParameters.frictionW;
                    this.pVehicle.partList[n].dy *= this.worldParameters.frictionW;
                } else {
                    this.pVehicle.partList[n].dy += this.worldParameters.dt * this.worldParameters.gravity;
                    this.pVehicle.partList[n].dx *= this.worldParameters.friction;
                    this.pVehicle.partList[n].dy *= this.worldParameters.friction;
                }
                if (!(n == this.pVehicle.ropeSegments[0] + this.pVehicle.maxRopeSegments - 1 &&
                        this.pVehicle.inactiveRope[0] || n == this.pVehicle.ropeSegments[1] +
                        this.pVehicle.maxRopeSegments - 1 && this.pVehicle.inactiveRope[1])) {
                    lxt = this.pVehicle.partList[n].x;
                    lyt = this.pVehicle.partList[n].y;
                    this.pVehicle.partList[n].x += this.worldParameters.dt * this.pVehicle.partList[n].dx;
                    this.pVehicle.partList[n].y += this.worldParameters.dt * this.pVehicle.partList[n].dy;
                }
                if ((c2 = this.worldParameters.checkPosition((int) this.pVehicle.partList[n].x,
                        (int) this.pVehicle.partList[n].y)) != 'l' && c2 != 'w' ||
                        this.pVehicle.partList[n].x < 0.0 ||
                        this.pVehicle.partList[n].x > (double) (this.worldParameters.levelWidth - 1) ||
                        this.pVehicle.partList[n].y < 0.0 ||
                        this.pVehicle.partList[n].y > (double) (this.worldParameters.levelHeight - 1)) {
                    if (n == this.pVehicle.ropeSegments[0] + this.pVehicle.maxRopeSegments - 1) {
                        this.pVehicle.inactiveRope[0] = true;
                    } else if (n == this.pVehicle.ropeSegments[1] + this.pVehicle.maxRopeSegments - 1) {
                        this.pVehicle.inactiveRope[1] = true;
                    } else {
                        this.pVehicle.partList[n].x = lxt;
                        this.pVehicle.partList[n].y = lyt;
                        this.pVehicle.partList[n].dx = 0.0;
                        this.pVehicle.partList[n].dy = 0.0;
                    }
                }
            }
            ++n;
        }
        graphics.setColor(Color.GRAY);
        graphics.fillRect(10, 10, 85, 18);
        if (this.mouseY < 33 && this.mouseX < 100) {
            graphics.setColor(new Color(0, 0, 255));
        } else {
            graphics.setColor(Color.black);
        }
        graphics.drawRect(10, 10, 85, 18);
        graphics.drawString("GitHub", 32, 24);

        // Display simple timer
        long levelTime = (System.currentTimeMillis() - levelStartTime) / 1000;
        graphics.setColor(Color.black);
        graphics.drawString("Time: " + levelTime, 380, 24);

        ++this.gameCounter;
        this.gameCounter %= 2;
        if (this.gameCounter != 0) return;

        boolean inside1 = isCoordinateInArea((int) pVehicle.partList[10].x, (int) pVehicle.partList[10].y);
        boolean inside2 = isCoordinateInArea((int) pVehicle.partList[21].x, (int) pVehicle.partList[21].y);

        if (inside1 && inside2 && !insideGoal) {
            // Set the start time when the body triangle is inside the goal area
            startTime = System.currentTimeMillis();
            insideGoal = true;
        }
        if (insideGoal) {
            long elapsedTime = System.currentTimeMillis() - startTime;

            // Check if conditions remain true for 5 seconds
            if (!inside1 && !inside2) {
                insideGoal = false;
            } else {
                int x = pVehicle.partList[21].x < pVehicle.partList[10].x ?
                        (int) ((pVehicle.partList[10].x - pVehicle.partList[21].x) + pVehicle.partList[21].x) -
                                1 - worldParameters.viewportX - 80 :
                        (int) ((pVehicle.partList[21].x - pVehicle.partList[10].x) + pVehicle.partList[10].x) -
                                1 - worldParameters.viewportX - 80;

                int y = pVehicle.partList[21].y < pVehicle.partList[10].y ?
                        (int) (pVehicle.partList[21].y - 25.0 - worldParameters.viewportY) :
                        (int) (pVehicle.partList[10].y - 25.0 - worldParameters.viewportY);
                graphics.setColor(new Color(255, 0, 0));
                graphics.drawString("Stay inside the goal: " + ((delay - elapsedTime) / 1000), x, y);
            }
            if (elapsedTime >= delay) {
                this.runState = false;
                int storedTime = 9999;
                try {
                    storedTime = parseInt(levelTimes.get(gp.paramMap.get("Bild")));
                } catch (Exception e) {
                    if (V2RApp.DEBUG) System.out.println("No stored time..");
                }
                if (levelTime < storedTime) {
                    levelTimes.put(gp.paramMap.get("Bild"), String.valueOf(levelTime));
                }
                startTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * Determines whether a given coordinate is within a specified rectangular area.
     *
     * @param x The x-coordinate to check.
     * @param y The y-coordinate to check.
     * @return {@code true} if the coordinate (x, y) lies within the bounds of the area defined by
     * the Goal variables defined in {@link GameParams}, {@code false} otherwise.
     */
    public boolean isCoordinateInArea(int x, int y) {
        int areaX = gp.getInt("GoalX");
        int areaY = gp.getInt("GoalY");
        int areaWidth = gp.getInt("GoalWidth");
        int areaHeight = gp.getInt("GoalHeight");

        boolean withinXBounds = x >= areaX && x < (areaX + areaWidth);
        boolean withinYBounds = y >= areaY && y < (areaY + areaHeight);
        return withinXBounds && withinYBounds;
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }

    /**
     * Loads an image from the specified file path.
     *
     * @param filePath The file path of the image to be loaded.
     * @return The loaded Image object, or null if the image could not be loaded.
     */
    public Image loadImage(String filePath) {
        String imagePath = gp.paramMap.get(filePath);
        Image imgBuffer = null;
        if (imagePath != null) {
            File imageFile = new File(imagePath);

            try {
                imgBuffer = ImageIO.read(imageFile);
            } catch (IOException e) {
                if (V2RApp.DEBUG) {
                    System.out.println("Image not found!" + e.getMessage());
                }
            }
        } else {
            System.err.println("Image not found!");
            System.exit(1);
        }
        return imgBuffer;
    }

    /**
     * Opens the specified URL in the default web browser, if supported.
     *
     * @param url The URL to be opened in the browser.
     */
    public void openURLInBrowser(URL url) {
        // Checks if the Desktop is supported and browsing action is supported.
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                // Opens the specified URL in the default web browser.
                Desktop.getDesktop().browse(url.toURI());
            } catch (IOException | URISyntaxException e) {
                if (V2RApp.DEBUG) {
                    System.out.println("Error opening URL in browser: " + e.getMessage());
                }
            }
        } else {
            // Desktop not supported, handle this case if needed
            System.out.println("Desktop not supported for launching web browser.");
        }
    }

    /**
     * Custom KeyAdapter implementation for managing game controls in the application.
     */
    class GameControls extends KeyAdapter {
        int pressedKey;

        /**
         * Constructs a new GameControls (KeyAdapter) instance.
         */
        GameControls() {
            // Do nothing
        }

        /**
         * Invoked when a key is pressed.
         *
         * @param keyEvent The KeyEvent associated with the key press.
         */
        @Override
        public void keyPressed(KeyEvent keyEvent) {
            // Get the code of the pressed key
            int keyCode = keyEvent.getKeyCode();

            // Update the pressed key for other methods to access
            this.pressedKey = keyCode;

            // Check different key codes and perform corresponding actions
            if (keyCode == KeyEvent.VK_LEFT) {
                worldParameters.leftPressed = true;
            }
            if (keyCode == KeyEvent.VK_RIGHT) {
                worldParameters.rightPressed = true;
            }
            if (keyCode == KeyEvent.VK_D) {
                worldParameters.fireHook = true;
            }
            if (keyCode == KeyEvent.VK_1) {
                pVehicle.ropeSlot = 0;
            }
            if (keyCode == KeyEvent.VK_2) {
                pVehicle.ropeSlot = 1;
            }
            if (keyCode == KeyEvent.VK_E) {
                worldParameters.windRope = true;
            }
            if (keyCode == KeyEvent.VK_C) {
                worldParameters.unwindRope = true;
            }
            if (keyCode == KeyEvent.VK_UP && pVehicle.buoyancy < 1.0) {
                pVehicle.buoyancy += 0.15;
            }
            if (keyCode == KeyEvent.VK_DOWN && pVehicle.buoyancy > 0.0) {
                pVehicle.buoyancy -= 0.15;
            }
            if (keyCode == KeyEvent.VK_S) {
                pVehicle.turretAngle -= 0.1;
            }
            if (keyCode == KeyEvent.VK_F) {
                pVehicle.turretAngle += 0.1;
            }
            if (keyCode == KeyEvent.VK_SHIFT) {
                shiftPressed = true; // TODO: Make wheels sticky (pretty sure it did that)
            }
            // Escape to exit the game.
            if (keyCode == KeyEvent.VK_ESCAPE) {
                runState = false;
                fireEvent("exit_loop");
            }
        }

        /**
         * Invoked when a key is released.
         *
         * @param keyEvent The KeyEvent associated with the key release.
         */
        @Override
        public void keyReleased(KeyEvent keyEvent) {
            int keyCode = keyEvent.getKeyCode();
            this.pressedKey = keyCode;
            if (keyCode == KeyEvent.VK_LEFT) {
                worldParameters.leftPressed = false;
            }
            if (keyCode == KeyEvent.VK_RIGHT) {
                worldParameters.rightPressed = false;
            }
            if (keyCode == KeyEvent.VK_SHIFT) {
                shiftPressed = false;
            }
        }
    }

    /**
     * Custom MouseMotionAdapter implementation for managing mouse controls in the application.
     */
    class MouseControls extends MouseMotionAdapter {
        /**
         * Constructs a new MouseControls (MouseMotionAdapter) instance.
         */
        MouseControls() {
        }

        /**
         * Invoked when the mouse is dragged.
         *
         * @param mouseEvent The MouseEvent associated with the mouse drag.
         */
        public void mouseDragged(MouseEvent mouseEvent) {
            mouseX = mouseEvent.getX();
            mouseY = mouseEvent.getY();
        }

        /**
         * Invoked when the mouse is moved.
         *
         * @param mouseEvent The MouseEvent associated with the mouse movement.
         */
        public void mouseMoved(MouseEvent mouseEvent) {
            mouseX = mouseEvent.getX();
            mouseY = mouseEvent.getY();
            if (mouseY < 33 && mouseX < 100) {
                if (!cursorState) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    cursorState = true;
                }
            } else if (cursorState) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                cursorState = false;
            }
        }
    }

    /**
     * Custom MouseAdapter implementation for handling mouse events in the application.
     */
    class xMA extends MouseAdapter {
        /**
         * Constructs a new xMA (MouseAdapter) instance.
         */
        xMA() {
            // Do nothing
        }

        /**
         * Invoked when a mouse button is pressed.
         *
         * @param mouseEvent The MouseEvent associated with the button press.
         */
        public void mousePressed(MouseEvent mouseEvent) {
            mouseX = mouseEvent.getX();
            mouseY = mouseEvent.getY();
            mouseState = true;
            if (mouseY < 33 && mouseX < 100) {
                mouseState = false;
                try {
                    URL uRL = new URL("https://github.com/Java-Code-Phoenix-Society/Vehicle2-redux");
                    openURLInBrowser(uRL);

                } catch (MalformedURLException malformedURLException) {
                    // empty catch block
                }
            }
        }

        /**
         * Invoked when a mouse button is released.
         *
         * @param mouseEvent The MouseEvent associated with the button release.
         */
        public void mouseReleased(MouseEvent mouseEvent) {
            mouseState = false;
        }
    }

    /**
     * Manages parameters related to the world, including gravity, friction, dimensions, and user input.
     */
    class WorldParameters {
        final char[][] levelMap;
        double gravity;
        double dt;
        double friction;
        double frictionW;
        int levelWidth;
        int levelHeight;
        int viewportX = 0;
        int viewportY = 0;
        double x;
        double y;
        double wpX;
        double wpY;
        boolean leftPressed = false;
        boolean rightPressed = false;
        boolean fireHook = false;
        boolean windRope = false;
        boolean unwindRope = false;
        double maxFrameWidth;
        double maxFrameHeight;
        double scaleM;
        double scaleSize;
        double scaleF;
        int delay;

        /**
         * Constructs a WorldParameters object with default values based on configuration settings.
         */
        WorldParameters() {
            this.maxFrameWidth = screenWidth - 1;
            this.maxFrameHeight = screenHeight - 1;
            this.levelWidth = gp.getInt("Bild_w");
            this.levelHeight = gp.getInt("Bild_h");
            this.scaleM = 0.01 * (double) gp.getInt("scaleM");
            this.scaleF = 0.01 * (double) gp.getInt("scaleF");
            this.scaleSize = 0.01 * (double) gp.getInt("scaleSize");
            this.dt = 0.001 * (double) gp.getInt("dt");
            this.delay = gp.getInt("delay");
            this.gravity = gp.getInt("Gravity");
            this.friction = 0.001 * (double) gp.getInt("Reibung");
            this.frictionW = 0.001 * (double) gp.getInt("ReibungW");
            this.levelMap = new char[this.levelWidth][this.levelHeight];
        }

        /**
         * Checks the type of terrain at a specific position on the level map.
         *
         * @param x The x-coordinate to check.
         * @param y The y-coordinate to check.
         * @return The type of terrain ('e' for out of bounds, 'g' for ground, 'w' for water, etc.).
         */
        public char checkPosition(int x, int y) {
            if (x < 0 || y < 0 || x >= this.levelWidth || y >= this.levelHeight) {
                return 'e';
            }
            return this.levelMap[x][y];
        }
    }

    /**
     * Represents the player-controlled vehicle with various attributes such as mass, forces, and parts.
     */
    class PlayerVehicle {
        int pCounter; // Counter for tracking elements in the vehicle.
        boolean[] activeRope = new boolean[2]; // Array indicating the activity state of rope segments.
        boolean[] inactiveRope = new boolean[2]; // Array indicating the inactivity state of rope segments.
        double turretAngle = -1.5707963267948966; // The angle of the turret.
        double buoyancy = 1.0; // The buoyancy factor of the player vehicle.
        double mHook;
        double mRope;
        double mWheels;
        double mAxis;
        double mCorpus;
        double ropeMin; // Minimum length of the rope connected to the player vehicle.
        double ropeMax; // Maximum length of the rope connected to the player vehicle.
        double fWheels;
        double fCorpus;
        double fEngine; // Engine's output torque
        double v0Rope;
        int px;
        int py;
        int bodyPart = 0;
        int ropePart = 0;
        int ropeSlot = 0; // Current selected rope slot.
        int[] ropeSegments = new int[2];
        int[] ropeAnchor = new int[2];
        int maxRopeSegments = 10; // breaks code above 10
        VehiclePart[] vParts; // vehicle parts: 60,61,62 make the red body
        Connector[] partList; // Array of connectors associated with the player vehicle.

        /**
         * Constructs a PlayerVehicle with specified initial values.
         *
         * @param parts      The initial number of parts in the player vehicle.
         * @param connectors The initial number of connectors in the player vehicle.
         */
        public PlayerVehicle(int parts, int connectors) {
            py = connectors;
            px = parts;
            vParts = new VehiclePart[this.py];
            partList = new Connector[this.px];
            mHook = 0.01 * (double) gp.getInt("mHook");
            mRope = 0.01 * (double) gp.getInt("mRope");
            mWheels = 0.01 * (double) gp.getInt("mWheels");
            mAxis = 0.01 * (double) gp.getInt("mAxis");
            mCorpus = 0.01 * (double) gp.getInt("mCorpus");
            ropeMin = worldParameters.scaleF * (double) gp.getInt("FRopeMin");
            ropeMax = worldParameters.scaleF * (double) gp.getInt("FRopeMax");
            fWheels = gp.getInt("FWheels");
            fCorpus = gp.getInt("FCorpus");
            fEngine = 0.1 * worldParameters.scaleSize * worldParameters.dt *
                    (double) gp.getInt("FEngine");
            v0Rope = gp.getInt("v0Rope");
        }

        /**
         * Draws the player vehicle by rendering its active parts and connectors.
         */
        public void drawVehicle() {
            // Draw active vehicle parts
            for (VehiclePart part : this.vParts) {
                if (part != null && part.partActive) {
                    part.drawPart();
                }
            }

            // Draw connectors that are toggled on
            for (Connector c : this.partList) {
                if (c != null && c.gToggle) {
                    c.drawConnector();
                }
            }
        }
    }

    /**
     * Represents a part of a vehicle with specific coordinates, length, sag, color, and activity status.
     */
    class VehiclePart {
        int x;
        int y;
        double rLength;
        double sag;
        Color color;
        boolean partActive = true;

        /**
         * Constructs a VehiclePart with specified initial values.
         *
         * @param x     The initial x-coordinate of the vehicle part.
         * @param y     The initial y-coordinate of the vehicle part.
         * @param min   The initial rest length of the vehicle part.
         * @param color The initial color of the vehicle part.
         */
        public VehiclePart(int x, int y, double min, Color color) {
            this.x = x;
            this.y = y;
            this.rLength = min;
            this.color = color;
            double d3 = pVehicle.partList[this.x].x -
                    pVehicle.partList[this.y].x;
            double d4 = pVehicle.partList[this.x].y -
                    pVehicle.partList[this.y].y;
            this.sag = Math.sqrt(d3 * d3 + d4 * d4);
        }

        /**
         * Draws the vehicle part on the graphics context.
         */
        public void drawPart() {
            graphics.setColor(this.color);
            /*
             Draws a line on the graphics context representing the vehicle part.
             The line is drawn from (pVehicle.partList[x].x - worldParameters.viewportX, pVehicle.partList[x].y - worldParameters.viewportY)
             to (pVehicle.partList[y].x - worldParameters.viewportX, pVehicle.partList[y].y - worldParameters.viewportY).
             */
            graphics.drawLine(
                    (int) pVehicle.partList[this.x].x - worldParameters.viewportX,
                    (int) pVehicle.partList[this.x].y - worldParameters.viewportY,
                    (int) pVehicle.partList[this.y].x - worldParameters.viewportX,
                    (int) pVehicle.partList[this.y].y - worldParameters.viewportY);
        }
    }

    /**
     * Represents a Connector with specified coordinates, dimensions, color, and graphical state.
     */
    class Connector {
        double x;
        double y;
        double dx;
        double dy;
        double angle;
        Color color;
        boolean gToggle; // Shows the connector if true.

        /**
         * Constructs a Connector with specified initial values.
         *
         * @param x     The initial x-coordinate of the connector.
         * @param y     The initial y-coordinate of the connector.
         * @param angle The initial value of dimension 4 (angle).
         * @param color The initial color of the connector.
         */
        public Connector(double x, double y, double angle, Color color) {
            this.x = x;
            this.y = y;
            this.angle = angle;
            this.dx = 0.0;
            this.dy = 0.0;
            this.color = color;
            this.gToggle = true;
        }

        /**
         * Draws a connector at the specified coordinates on the graphics context.
         * The connector is represented as a filled rectangle with a color defined by this.cColor.
         * The coordinates are adjusted based on the viewport position in the worldParameters.
         * <p>
         * Note: The method assumes that the necessary graphics context has been set before calling.
         *
         * @see WorldParameters
         */
        public void drawConnector() {
            graphics.setColor(this.color);
            graphics.fillRect((int) (this.x - 1.0 - (double) worldParameters.viewportX),
                    (int) (this.y - 1.0 - (double) worldParameters.viewportY), 3, 3);
        }
    }
}