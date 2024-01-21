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
import java.util.HashMap;
import java.util.stream.IntStream;

public class Vehicle2 extends JPanel implements ActionListener {

    public int currentLevel;
    public GameParams gp;
    boolean cursorState = false;
    boolean mouseState = false;
    public boolean runState = false;
    boolean shiftPressed = false;
    int gameCounter = 0;
    int graphicsReady = 0;
    int screenWidth;
    int screenHeight;
    int mouseX = 0;
    int mouseY = 0;
    long levelStartTime;
    Graphics graphics;
    Image imgBG;
    Image screenBuffer;
    Image tileImg;
    MediaTracker tracker;
    PlayerVehicle pVehicle;
    WorldParameters worldParameters;
    public ArrayList<LevelMap> maps;
    static HashMap<String, String> levelTimes;
    double lxt = 0.0;
    double lyt = 0.0;
    long startTime = System.currentTimeMillis();
    private final long delay = 5000; // 5 seconds: Time to stay in the goal
    boolean insideGoal = false;
    final private ArrayList<TriggerListener> listeners = new ArrayList<>();

    public Vehicle2() {
        currentLevel = 0;
        levelTimes = new HashMap<>();
        maps = LevelUtilities.getLevelMaps();
        setSize(800, 600);
    }

    public void addEventListener(TriggerListener listener) {
        listeners.add(listener);
    }

    public void fireEvent(String message) {
        TriggerEvent event = new TriggerEvent(this, message);
        for (TriggerListener listener : listeners) {
            listener.onEventOccurred(event);
        }
    }

    public void paint(Graphics graphics) {
        this.paintComponent(graphics);
    }

    public void update(Graphics graphics) {
        this.paintComponent(graphics);
    }

    public void init() {
        gp = maps.get(currentLevel).lp;
        int n, p = 43, c = 83;
        int n2 = 40, n3;
        double d2 = 0.0, d3 = 0.0;
        screenWidth = this.getSize().width;
        screenHeight = this.getSize().height;
        screenBuffer = this.createImage(this.screenWidth, this.screenHeight);
        graphics = this.screenBuffer.getGraphics();

        worldParameters = new WorldParameters();
        pVehicle = new PlayerVehicle(p, c);
        tracker = new MediaTracker(this);
        tileImg = loadImage("Bild");
        tileImg = loadImage("Bild");
        tracker.addImage(this.tileImg, 0);
        tracker.checkID(0, true);
        imgBG = loadImage("Bild_c");
        tracker.addImage(this.tileImg, 1);
        tracker.checkID(1, true);
        processAndMapPixels();

        Color greenColor = new Color(0, 150, 0);
        Color orangeColor = new Color(255, 200, 50);
        Color redColor = new Color(255, 0, 0);

        for (n3 = 0; n3 < 2; n3++) {
            double d4 = d2 + (n3 * 80);
            for (n = 0; n < pVehicle.maxRopeSegments; n++) {
                double d5 = Math.PI * (n / 5.0);
                this.pVehicle.partList[this.pVehicle.np] =
                        new Connector(d4 + (double) n2 * Math.cos(d5),
                                d3 + (double) n2 * Math.sin(d5),
                                this.pVehicle.mWheels * this.worldParameters.scaleM, greenColor);
                ++this.pVehicle.np;
            }
            n = 0;
            while (n < pVehicle.maxRopeSegments) {
                this.pVehicle.vParts[this.pVehicle.nf] =
                        new VehiclePart(n + 11 * n3, (n + 1) % pVehicle.maxRopeSegments + 11 * n3,
                                this.worldParameters.scaleF * this.pVehicle.fWheels, orangeColor);
                ++this.pVehicle.nf;
                this.pVehicle.vParts[this.pVehicle.nf] =
                        new VehiclePart(n + 11 * n3, (n + 3) % pVehicle.maxRopeSegments + 11 * n3,
                                this.worldParameters.scaleF * this.pVehicle.fWheels, orangeColor);
                ++this.pVehicle.nf;
                ++n;
            }
            this.pVehicle.partList[this.pVehicle.np] =
                    new Connector(d4, d3, this.worldParameters.scaleM * this.pVehicle.mAxis,
                            greenColor);
            ++this.pVehicle.np;
            n = 0;
            while (n < pVehicle.maxRopeSegments) {
                this.pVehicle.vParts[this.pVehicle.nf] =
                        new VehiclePart(n + 11 * n3, this.pVehicle.np - 1,
                                this.worldParameters.scaleF * this.pVehicle.fWheels, orangeColor);
                ++this.pVehicle.nf;
                ++n;
            }

        }
        this.pVehicle.partList[this.pVehicle.np] =
                new Connector(d2 + 40.0, d3, this.worldParameters.scaleM * this.pVehicle.mCorpus,
                        greenColor);
        ++this.pVehicle.np;
        this.pVehicle.pCounter = this.pVehicle.np - 1;
        this.pVehicle.vParts[this.pVehicle.nf] =
                new VehiclePart(10, this.pVehicle.np - 1,
                        this.worldParameters.scaleF * this.pVehicle.fCorpus, redColor);
        ++this.pVehicle.nf;
        this.pVehicle.vParts[this.pVehicle.nf] =
                new VehiclePart(21, this.pVehicle.np - 1,
                        this.worldParameters.scaleF * this.pVehicle.fCorpus, redColor);
        ++this.pVehicle.nf;
        this.pVehicle.vParts[this.pVehicle.nf] =
                new VehiclePart(10, 21, this.worldParameters.scaleF * this.pVehicle.fCorpus, redColor);
        this.pVehicle.ropeSegments[0] = this.pVehicle.np;
        this.pVehicle.ropeAnchor[0] = ++this.pVehicle.nf;
        n = 0;
        while (n < this.pVehicle.maxRopeSegments) {
            processPart(n, greenColor);
            this.pVehicle.vParts[this.pVehicle.nf] = n == 0 ?
                    new VehiclePart(22, this.pVehicle.ropeSegments[0], this.pVehicle.ropeMin,
                            new Color(0, 80, 185)) :
                    new VehiclePart(this.pVehicle.ropeSegments[0] + n - 1, this.pVehicle.ropeSegments[0] + n,
                            this.pVehicle.ropeMin, new Color(0, 80, 185));

            this.pVehicle.vParts[this.pVehicle.nf].sag = this.worldParameters.scaleSize *
                    (double) gp.getInt("l0Rope");
            this.pVehicle.vParts[this.pVehicle.nf].partActive = false;
            ++this.pVehicle.nf;
            ++n;
        }
        this.pVehicle.ropeSegments[1] = this.pVehicle.np;
        this.pVehicle.ropeAnchor[1] = this.pVehicle.nf;
        n = 0;
        while (n < this.pVehicle.maxRopeSegments) {
            processPart(n, greenColor);
            this.pVehicle.vParts[this.pVehicle.nf] = n == 0 ?
                    new VehiclePart(22, this.pVehicle.ropeSegments[1],
                            this.pVehicle.ropeMin, new Color(70, 170, 255)) :
                    new VehiclePart(this.pVehicle.ropeSegments[1] + n - 1, this.pVehicle.ropeSegments[1] + n,
                            this.pVehicle.ropeMin, new Color(70, 170, 255));
            this.pVehicle.vParts[this.pVehicle.nf].sag = this.worldParameters.scaleSize *
                    (double) gp.getInt("l0Rope");
            this.pVehicle.vParts[this.pVehicle.nf].partActive = false;
            ++this.pVehicle.nf;
            ++n;
        }
        n = 0;
        while (n < this.pVehicle.np) {
            this.pVehicle.partList[n].lx *= this.worldParameters.scaleSize;
            this.pVehicle.partList[n].ly *= this.worldParameters.scaleSize;
            this.pVehicle.partList[n].lx += gp.getInt("StartX");
            this.pVehicle.partList[n].ly += gp.getInt("StartY");
            ++n;
        }
        n = 0;
        while (n < this.pVehicle.nf) {
            this.pVehicle.vParts[n].sag *= this.worldParameters.scaleSize;
            ++n;
        }
        System.out.println("Map: " + gp.paramMap.get("Bild_c") + ", nf=" + this.pVehicle.nf + ", np=" + this.pVehicle.np);
        this.worldParameters.wpX = this.pVehicle.partList[this.pVehicle.pCounter].lx - ((double) this.screenWidth / 2);
        this.worldParameters.wpY = this.pVehicle.partList[this.pVehicle.pCounter].ly - ((double) this.screenHeight / 2);

        // Add the control listeners once. Probably not the best way
        if (graphicsReady == 0) {
            this.addMouseListener(new xMA());
            this.addMouseMotionListener(new MouseControls());
            this.getRootPane().getParent().addKeyListener(new GameControls());
        }
        this.graphicsReady = 1;
        levelStartTime = System.currentTimeMillis(); // Start the timer
        this.runState = true;
    }

    private void processPart(int n, Color color) {
        this.pVehicle.partList[this.pVehicle.np] =
                new Connector(n, n, this.pVehicle.mRope * this.worldParameters.scaleM, color);
        if (n == this.pVehicle.maxRopeSegments - 1) {
            this.pVehicle.partList[this.pVehicle.np].a = this.worldParameters.scaleM * this.pVehicle.mHook;
        }
        this.pVehicle.partList[this.pVehicle.np].gToggle = false;
        ++this.pVehicle.np;
    }

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
            return;
        }

        // Wait for all images to be loaded
        while (!this.tracker.checkAll()) {
            try {
                Thread.sleep(1L);
            } catch (InterruptedException e) {
                // Consider logging the exception or handling it appropriately
            }
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

    private char mapPixelToChar(int red, int green, int blue) {
        int pixelValue = (red << 16) | (green << 8) | blue;
        if (pixelValue == GameConstants.airColor) {
            return 'l';
        } else if (pixelValue == GameConstants.waterColor) {
            return 'w';
        } else if (pixelValue == GameConstants.groundColor) {
            return 'e';
        } else if (pixelValue == GameConstants.aColor) {
            return 'f';
        } else if (pixelValue == GameConstants.bColor) {
            return 'E';
        }
        return 'F';
    }

    public void paintComponent(Graphics graphics) {
        if (this.graphicsReady > 0) {
            graphics.drawImage(this.screenBuffer, 0, 0, this); // Draw game image
        }
    }

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
                    (c.lx + VELOCITY_MULTIPLIER * (d4 /= 20.0) - (double) (this.screenWidth / 2));
            this.worldParameters.wpY = SMOOTHING_FACTOR * this.worldParameters.y + ADJUSTMENT_FACTOR *
                    (c.ly + VELOCITY_MULTIPLIER * (d5 /= 20.0) - (double) (this.screenHeight / 2));
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
                    (int) c.lx - this.worldParameters.viewportX,
                    (int) (c.ly) - this.worldParameters.viewportY,
                    (int) (c.lx - this.worldParameters.viewportX + 10.0 * Math.cos(this.pVehicle.turretAngle)),
                    (int) (c.ly - this.worldParameters.viewportY + 10.0 * Math.sin(this.pVehicle.turretAngle)));

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
                    this.pVehicle.partList[this.pVehicle.ropeSegments[this.pVehicle.ropeSlot] + n].lx =
                            c.lx + (double) ((1 + n) * 2) *
                                    Math.cos(this.pVehicle.turretAngle);
                    this.pVehicle.partList[this.pVehicle.ropeSegments[this.pVehicle.ropeSlot] + n].ly =
                            c.ly + (double) ((1 + n) * 2) *
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
                                (this.pVehicle.partList[pVehicle.maxRopeSegments + 11 * n3].lx -
                                        this.pVehicle.partList[n4].lx) * ENGINE_FORCE * (double) n2;
                        this.pVehicle.partList[n4].dx +=
                                (this.pVehicle.partList[n4].ly - this.pVehicle.partList[pVehicle.maxRopeSegments +
                                        11 * n3].ly) * ENGINE_FORCE * (double) n2;
                        ++n;
                    }
                    ++n3;
                }
            }
            n = 0;
            while (n < this.pVehicle.nf) {
                if (this.pVehicle.vParts[n].partActive) {
                    double d6 = this.pVehicle.partList[this.pVehicle.vParts[n].rx].lx -
                            this.pVehicle.partList[this.pVehicle.vParts[n].ry].lx;
                    double d7 = this.pVehicle.partList[this.pVehicle.vParts[n].rx].ly -
                            this.pVehicle.partList[this.pVehicle.vParts[n].ry].ly;
                    double d8 = Math.sqrt(d6 * d6 + d7 * d7);
                    double d9 = this.pVehicle.vParts[n].rLength * (d8 - this.pVehicle.vParts[n].sag);
                    double d10 = d9 / this.pVehicle.partList[this.pVehicle.vParts[n].rx].a;
                    this.pVehicle.partList[this.pVehicle.vParts[n].rx].dx -= this.worldParameters.dt * d10 * (d6 /= d8);
                    this.pVehicle.partList[this.pVehicle.vParts[n].rx].dy -= this.worldParameters.dt * d10 * (d7 /= d8);
                    d10 = d9 / this.pVehicle.partList[this.pVehicle.vParts[n].ry].a;
                    this.pVehicle.partList[this.pVehicle.vParts[n].ry].dx += this.worldParameters.dt * d10 * d6;
                    this.pVehicle.partList[this.pVehicle.vParts[n].ry].dy += this.worldParameters.dt * d10 * d7;
                }
                ++n;
            }
            n = 0;
            while (n < this.pVehicle.np) {
                if (this.pVehicle.partList[n].gToggle) {
                    char c2;
                    if (this.worldParameters.checkPosition((int) this.pVehicle.partList[n].lx,
                            (int) this.pVehicle.partList[n].ly) == 'w') {
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
                        lxt = this.pVehicle.partList[n].lx;
                        lyt = this.pVehicle.partList[n].ly;
                        this.pVehicle.partList[n].lx += this.worldParameters.dt * this.pVehicle.partList[n].dx;
                        this.pVehicle.partList[n].ly += this.worldParameters.dt * this.pVehicle.partList[n].dy;
                    }
                    if ((c2 = this.worldParameters.checkPosition((int) this.pVehicle.partList[n].lx,
                            (int) this.pVehicle.partList[n].ly)) != 'l' && c2 != 'w' ||
                            this.pVehicle.partList[n].lx < 0.0 ||
                            this.pVehicle.partList[n].lx > (double) (this.worldParameters.levelWidth - 1) ||
                            this.pVehicle.partList[n].ly < 0.0 ||
                            this.pVehicle.partList[n].ly > (double) (this.worldParameters.levelHeight - 1)) {
                        if (n == this.pVehicle.ropeSegments[0] + this.pVehicle.maxRopeSegments - 1) {
                            this.pVehicle.inactiveRope[0] = true;
                        } else if (n == this.pVehicle.ropeSegments[1] + this.pVehicle.maxRopeSegments - 1) {
                            this.pVehicle.inactiveRope[1] = true;
                        } else {
                            this.pVehicle.partList[n].lx = lxt;
                            this.pVehicle.partList[n].ly = lyt;
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

            boolean inside1 = isCoordinateInArea((int) pVehicle.partList[10].lx, (int) pVehicle.partList[10].ly);
            boolean inside2 = isCoordinateInArea((int) pVehicle.partList[21].lx, (int) pVehicle.partList[21].ly);

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
                    int x = pVehicle.partList[21].lx < pVehicle.partList[10].lx ?
                            (int) ((pVehicle.partList[10].lx - pVehicle.partList[21].lx) + pVehicle.partList[21].lx) -
                                    1 - worldParameters.viewportX - 80 :
                            (int) ((pVehicle.partList[21].lx - pVehicle.partList[10].lx) + pVehicle.partList[10].lx) -
                                    1 - worldParameters.viewportX - 80;

                    int y = pVehicle.partList[21].ly < pVehicle.partList[10].ly ?
                            (int) (pVehicle.partList[21].ly - 25.0 - worldParameters.viewportY) :
                            (int) (pVehicle.partList[10].ly - 25.0 - worldParameters.viewportY);
                    graphics.setColor(new Color(255, 0, 0));
                    graphics.drawString("Stay inside the goal: " + ((delay - elapsedTime) / 1000), x, y);
                }
                if (elapsedTime >= delay) {
                    this.runState = false;
                    levelTimes.put(gp.paramMap.get("Bild"), String.valueOf(levelTime));
                    startTime = System.currentTimeMillis();
                }
            }

            /*/this.repaint();
            //update(this.graphics);
            if (this.worldParameters.delay <= 0) return;
            try {
                Thread.sleep(this.worldParameters.delay);
            } catch (InterruptedException interruptedException) {
                // empty catch block
            } */

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

    public Image loadImage(String param) {
        String imagePath = gp.paramMap.get(param);
        Image imgBuffer = null;
        if (imagePath != null) {
            File imageFile = new File(imagePath);

            try {
                imgBuffer = ImageIO.read(imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Image not found!");
            System.exit(1);
        }
        return imgBuffer;
    }

    public void openURLInBrowser(URL url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(url.toURI());
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            // Desktop not supported, handle this case if needed
            System.out.println("Desktop not supported for launching web browser.");
        }
    }

    class GameControls extends KeyAdapter {
        int pressedKey;

        GameControls() {
            // Do nothing
        }

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

    class MouseControls extends MouseMotionAdapter {
        MouseControls() {
        }

        public void mouseDragged(MouseEvent mouseEvent) {
            mouseX = mouseEvent.getX();
            mouseY = mouseEvent.getY();
        }

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

    class xMA extends MouseAdapter {
        xMA() {
            // Do nothing
        }

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

        public void mouseReleased(MouseEvent mouseEvent) {
            mouseState = false;
        }
    }

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

        public char checkPosition(int x, int y) {
            if (x < 0 || y < 0 || x >= this.levelWidth || y >= this.levelHeight) {
                return 'e';
            }
            return this.levelMap[x][y];
        }
    }

    class PlayerVehicle {
        int pCounter;
        boolean[] activeRope = new boolean[2];
        boolean[] inactiveRope = new boolean[2];
        double turretAngle = -1.5707963267948966;
        double buoyancy = 1.0;
        double mHook;
        double mRope;
        double mWheels;
        double mAxis;
        double mCorpus;
        double ropeMin;
        double ropeMax;
        double fWheels;
        double fCorpus;
        double fEngine;
        double v0Rope;
        int px = 32;
        int py = 64;
        int np = 0;
        int nf = 0;
        int ropeSlot = 0;
        int[] ropeSegments = new int[2];
        int[] ropeAnchor = new int[2];
        int maxRopeSegments = 10; // breaks code above 10
        VehiclePart[] vParts; // vehicle parts: 60,61,62 make the red body
        Connector[] partList;

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

    class VehiclePart {
        int rx;
        int ry;
        double rLength;
        double sag;
        Color rColor;
        boolean partActive = true;

        public VehiclePart(int x, int y, double min, Color color) {
            this.rx = x;
            this.ry = y;
            this.rLength = min;
            this.rColor = color;
            double d3 = pVehicle.partList[this.rx].lx -
                    pVehicle.partList[this.ry].lx;
            double d4 = pVehicle.partList[this.rx].ly -
                    pVehicle.partList[this.ry].ly;
            this.sag = Math.sqrt(d3 * d3 + d4 * d4);
        }

        public void drawPart() {
            graphics.setColor(this.rColor);
            graphics.drawLine(
                    (int) pVehicle.partList[this.rx].lx - worldParameters.viewportX,
                    (int) pVehicle.partList[this.rx].ly - worldParameters.viewportY,
                    (int) pVehicle.partList[this.ry].lx - worldParameters.viewportX,
                    (int) pVehicle.partList[this.ry].ly - worldParameters.viewportY);
        }
    }

    class Connector {
        double lx;
        double ly;
        double dx;
        double dy;
        double a;
        Color cColor;
        boolean gToggle;

        public Connector(double x, double y, double d4, Color color) {
            this.lx = x;
            this.ly = y;
            this.a = d4;
            this.dx = 0.0;
            this.dy = 0.0;
            this.cColor = color;
            this.gToggle = true;
        }

        public void drawConnector() {
            graphics.setColor(this.cColor);
            graphics.fillRect((int) (this.lx - 1.0 - (double) worldParameters.viewportX),
                    (int) (this.ly - 1.0 - (double) worldParameters.viewportY), 3, 3);
        }
    }
}