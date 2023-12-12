import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static java.lang.Integer.parseInt;


public class Vehicle2 extends JFrame implements ActionListener {
    public static GameParams gp;
    Image imgBG;
    Image imageC;
    Image tileImg;
    Graphics graphics;
    MediaTracker tracker;
    boolean runState = true;
    boolean cursorState = false;
    int gameCounter = 0;
    int graphicsReady = 0;
    WorldParameters worldParameters;
    PlayerVehicle pVehicle;
    int screenWidth;
    int screenHeight;
    boolean mouseState = false;
    int mouseX = 0;
    int mouseY = 0;
    boolean shiftPressed = false;
    GameConstants gameConstants;

    public static void main(String[] args) {
        gp = new GameParams();

        Vehicle2 frame = new Vehicle2();
        frame.setTitle("Vehicle 2: Redux");
        frame.setUndecorated(true);
        frame.setSize(parseInt(gp.paramMap.get("width")), parseInt(gp.paramMap.get("height")));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Calculate the center position
        int centerX = (screenSize.width - parseInt(gp.paramMap.get("width"))) / 2;
        int centerY = (screenSize.height - parseInt(gp.paramMap.get("height"))) / 2;

        // Set the frame location
        frame.setLocation(centerX, centerY);

        // Go to main Loop
        frame.init();
        frame.run();
    }

    public void paint(Graphics graphics) {
        this.paintComponent(graphics);
    }

    public void update(Graphics graphics) {
        this.paintComponent(graphics);
    }

    public void init() {
        int n, p = 43, c = 83;
        this.screenWidth = this.getSize().width;
        this.screenHeight = this.getSize().height;
        this.gameConstants = new GameConstants();
        this.imageC = this.createImage(this.screenWidth, this.screenHeight);
        this.graphics = this.imageC.getGraphics();
        this.worldParameters = new WorldParameters();
        this.pVehicle = new PlayerVehicle(p, c);
        this.tracker = new MediaTracker(this);
        this.tileImg = loadImage("Bild");
        this.tracker.addImage(this.tileImg, 0);
        this.tracker.checkID(0, true);
        this.imgBG = loadImage("Bild_c");
        this.tracker.addImage(this.tileImg, 1);
        this.tracker.checkID(1, true);
        this.zeroA();
        double d2 = 0.0;
        double d3 = 0.0;
        int n2 = 40;
        int n3 = 0;
        while (n3 < 2) {
            double d4 = d2 + (n3 * 80);
            n = 0;
            while (n < pVehicle.maxRopeSegments) {
                double d5 = Math.PI * (n / 5.0);
                this.pVehicle.partList[this.pVehicle.np] =
                        new Connector(d4 + (double) n2 * Math.cos(d5),
                                d3 + (double) n2 * Math.sin(d5),
                                this.pVehicle.mWheels * this.worldParameters.scaleM,
                                new Color(0, 150, 0));
                ++this.pVehicle.np;
                ++n;
            }
            n = 0;
            while (n < pVehicle.maxRopeSegments) {
                this.pVehicle.vParts[this.pVehicle.nf] =
                        new VehiclePart(n + 11 * n3, (n + 1) % pVehicle.maxRopeSegments + 11 * n3,
                                this.worldParameters.scaleF * this.pVehicle.fWheels,
                                new Color(255, 200, 50));
                ++this.pVehicle.nf;
                this.pVehicle.vParts[this.pVehicle.nf] =
                        new VehiclePart(n + 11 * n3, (n + 3) % pVehicle.maxRopeSegments + 11 * n3,
                                this.worldParameters.scaleF * this.pVehicle.fWheels,
                                new Color(255, 200, 50));
                ++this.pVehicle.nf;
                ++n;
            }
            this.pVehicle.partList[this.pVehicle.np] =
                    new Connector(d4, d3, this.worldParameters.scaleM * this.pVehicle.mAxis,
                            new Color(0, 150, 0));
            ++this.pVehicle.np;
            n = 0;
            while (n < pVehicle.maxRopeSegments) {
                this.pVehicle.vParts[this.pVehicle.nf] =
                        new VehiclePart(n + 11 * n3, this.pVehicle.np - 1,
                                this.worldParameters.scaleF * this.pVehicle.fWheels,
                                new Color(255, 200, 50));
                ++this.pVehicle.nf;
                ++n;
            }
            ++n3;
        }
        this.pVehicle.partList[this.pVehicle.np] =
                new Connector(d2 + 40.0, d3, this.worldParameters.scaleM * this.pVehicle.mCorpus,
                        new Color(0, 150, 0));
        ++this.pVehicle.np;
        this.pVehicle.pCounter = this.pVehicle.np - 1;
        this.pVehicle.vParts[this.pVehicle.nf] =
                new VehiclePart(10, this.pVehicle.np - 1, this.worldParameters.scaleF * this.pVehicle.fCorpus,
                        new Color(255, 0, 0));
        ++this.pVehicle.nf;
        this.pVehicle.vParts[this.pVehicle.nf] =
                new VehiclePart(21, this.pVehicle.np - 1, this.worldParameters.scaleF * this.pVehicle.fCorpus,
                        new Color(255, 0, 0));
        ++this.pVehicle.nf;
        this.pVehicle.vParts[this.pVehicle.nf] =
                new VehiclePart(10, 21, this.worldParameters.scaleF * this.pVehicle.fCorpus,
                        new Color(255, 0, 0));
        this.pVehicle.ropeSegments[0] = this.pVehicle.np;
        this.pVehicle.ropeAnchor[0] = ++this.pVehicle.nf;
        n = 0;
        while (n < this.pVehicle.maxRopeSegments) {
            this.pVehicle.partList[this.pVehicle.np] =
                    new Connector(n, n, this.pVehicle.mRope * this.worldParameters.scaleM, new Color(0, 100, 0));
            if (n == this.pVehicle.maxRopeSegments - 1) {
                this.pVehicle.partList[this.pVehicle.np].a =
                        this.worldParameters.scaleM * this.pVehicle.mHook;
            }
            this.pVehicle.partList[this.pVehicle.np].gToggle = false;
            ++this.pVehicle.np;
            this.pVehicle.vParts[this.pVehicle.nf] = n == 0 ?
                    new VehiclePart(
                            22, this.pVehicle.ropeSegments[0], this.pVehicle.ropeMin, new Color(0, 80, 185)) :
                    new VehiclePart(
                            this.pVehicle.ropeSegments[0] + n - 1, this.pVehicle.ropeSegments[0] + n,
                            this.pVehicle.ropeMin, new Color(0, 80, 185));

            this.pVehicle.vParts[this.pVehicle.nf].sag = this.worldParameters.scaleSize *
                    (double) parseInt(gp.paramMap.get("l0Rope"));
            this.pVehicle.vParts[this.pVehicle.nf].partActive = false;
            ++this.pVehicle.nf;
            ++n;
        }
        this.pVehicle.ropeSegments[1] = this.pVehicle.np;
        this.pVehicle.ropeAnchor[1] = this.pVehicle.nf;
        n = 0;
        while (n < this.pVehicle.maxRopeSegments) {
            this.pVehicle.partList[this.pVehicle.np] =
                    new Connector(n, n, this.pVehicle.mRope * this.worldParameters.scaleM, new Color(0, 170, 0));
            if (n == this.pVehicle.maxRopeSegments - 1) {
                this.pVehicle.partList[this.pVehicle.np].a = this.worldParameters.scaleM * this.pVehicle.mHook;
            }
            this.pVehicle.partList[this.pVehicle.np].gToggle = false;
            ++this.pVehicle.np;
            this.pVehicle.vParts[this.pVehicle.nf] = n == 0 ?
                    new VehiclePart(22, this.pVehicle.ropeSegments[1], this.pVehicle.ropeMin, new Color(70, 170, 255)) :
                    new VehiclePart(this.pVehicle.ropeSegments[1] + n - 1, this.pVehicle.ropeSegments[1] + n,
                            this.pVehicle.ropeMin, new Color(70, 170, 255));
            this.pVehicle.vParts[this.pVehicle.nf].sag = this.worldParameters.scaleSize *
                    (double) parseInt(gp.paramMap.get("l0Rope"));
            this.pVehicle.vParts[this.pVehicle.nf].partActive = false;
            ++this.pVehicle.nf;
            ++n;
        }
        n = 0;
        while (n < this.pVehicle.np) {
            this.pVehicle.partList[n].lx *= this.worldParameters.scaleSize;
            this.pVehicle.partList[n].ly *= this.worldParameters.scaleSize;
            this.pVehicle.partList[n].lx += parseInt(gp.paramMap.get("StartX"));
            this.pVehicle.partList[n].ly += parseInt(gp.paramMap.get("StartY"));
            ++n;
        }
        n = 0;
        while (n < this.pVehicle.nf) {
            this.pVehicle.vParts[n].sag *= this.worldParameters.scaleSize;
            ++n;
        }
        System.out.println("nf=" + this.pVehicle.nf + ", np=" + this.pVehicle.np);
        this.worldParameters.wpX = this.pVehicle.partList[this.pVehicle.pCounter].lx - (double) (this.screenWidth / 2);
        this.worldParameters.wpY = this.pVehicle.partList[this.pVehicle.pCounter].ly - (double) (this.screenHeight / 2);
        this.addKeyListener(new GameControls());
        this.addMouseListener(new xMA());
        this.addMouseMotionListener(new MouseControls());
        this.graphicsReady = 1;
    }


    public void zeroA() {
        ColorModel colorModel = ColorModel.getRGBdefault();
        int[] nArray = new int[this.worldParameters.levelWidth * this.worldParameters.levelHeight];
        while (!this.tracker.checkAll()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
        PixelGrabber pixelGrabber = new PixelGrabber(this.tileImg, 0, 0, this.worldParameters.levelWidth,
                this.worldParameters.levelHeight, nArray, 0, this.worldParameters.levelWidth);
        try {
            pixelGrabber.grabPixels();
        } catch (InterruptedException interruptedException) {
            System.err.println("interrupted waiting for pixels!");
            return;
        }
        int n = 0;
        while (n < this.worldParameters.levelWidth * this.worldParameters.levelHeight) {
            nArray[n] = colorModel.getRed(nArray[n]) << 16 | colorModel.getGreen(nArray[n]) << 8 | colorModel.getBlue(nArray[n]);
            this.worldParameters.n[n % this.worldParameters.levelWidth][n / this.worldParameters.levelWidth] =
                    (char) (nArray[n] == this.gameConstants.hexMax ? 108 : (nArray[n] == this.gameConstants.maxNumber ?
                            119 : (nArray[n] == this.gameConstants.minNumber ? 101 :
                            (nArray[n] == this.gameConstants.hexLow ? 102 :
                                    (nArray[n] == this.gameConstants.theConst ? 69 : 70))))
                    );
            ++n;
        }
    }

    public void paintComponent(Graphics graphics) {
        if (this.graphicsReady > 0) {
            graphics.drawImage(this.imageC, 0, 0, this); // Draw game image
        }
    }

    public void run() {
        double d2 = 0.0;
        double d3 = 0.0;
        while (this.runState) { // Main loop
            this.worldParameters.x = this.worldParameters.wpX;
            this.worldParameters.y = this.worldParameters.wpY;
            double d4 = 0.0;
            double d5 = 0.0;
            int n = 0;
            while (n < 20) {
                d4 += this.pVehicle.partList[n].dx;
                d5 += this.pVehicle.partList[n].dy;
                ++n;
            }
            this.worldParameters.wpX = 0.99 * this.worldParameters.x + 0.01 *
                    (this.pVehicle.partList[this.pVehicle.pCounter].lx + 3.0 *
                            (d4 /= 20.0) - (double) (this.screenWidth / 2));
            this.worldParameters.wpY = 0.99 * this.worldParameters.y + 0.01 *
                    (this.pVehicle.partList[this.pVehicle.pCounter].ly + 3.0 *
                            (d5 /= 20.0) - (double) (this.screenHeight / 2));
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
            this.graphics.drawImage(
                    this.imgBG, 0, 0, this.screenWidth, this.screenHeight,
                    this.worldParameters.viewportX, this.worldParameters.viewportY,
                    this.worldParameters.viewportX + this.screenWidth,
                    this.worldParameters.viewportY + this.screenHeight, this);
            this.pVehicle.updatePos();
            this.graphics.setColor(Color.black);
            this.graphics.drawLine((int) this.pVehicle.partList[this.pVehicle.pCounter].lx - this.worldParameters.viewportX,
                    (int) this.pVehicle.partList[this.pVehicle.pCounter].ly -
                            this.worldParameters.viewportY,
                    (int) (this.pVehicle.partList[this.pVehicle.pCounter].lx -
                            (double) this.worldParameters.viewportX + 10.0 * Math.cos(this.pVehicle.turretAngle)),
                    (int) (this.pVehicle.partList[this.pVehicle.pCounter].ly -
                            (double) this.worldParameters.viewportY + 10.0 * Math.sin(this.pVehicle.turretAngle)));
            if (this.worldParameters.fireHook && this.pVehicle.a[this.pVehicle.ropeSlot]) {
                n = 0;
                while (n < this.pVehicle.maxRopeSegments) {
                    this.pVehicle.partList[this.pVehicle.ropeSegments[this.pVehicle.ropeSlot] + n].gToggle = false;
                    this.pVehicle.vParts[this.pVehicle.ropeAnchor[this.pVehicle.ropeSlot] + n].partActive = false;
                    ++n;
                }
                this.pVehicle.a[this.pVehicle.ropeSlot] = false;
                this.worldParameters.fireHook = false;
            }
            if (this.pVehicle.a[this.pVehicle.ropeSlot] && this.worldParameters.windRope) {
                n = this.pVehicle.ropeAnchor[this.pVehicle.ropeSlot];
                while (n < this.pVehicle.ropeAnchor[this.pVehicle.ropeSlot] + this.pVehicle.maxRopeSegments) {
                    if (!(this.pVehicle.vParts[n].rLength < this.pVehicle.ropeMax)) break;
                    this.pVehicle.vParts[n].rLength *= 1.1;
                    ++n;
                }
                this.worldParameters.windRope = false;
            }
            if (this.pVehicle.a[this.pVehicle.ropeSlot] && this.worldParameters.unwindRope) {
                n = this.pVehicle.ropeAnchor[this.pVehicle.ropeSlot];
                while (n < this.pVehicle.ropeAnchor[this.pVehicle.ropeSlot] + this.pVehicle.maxRopeSegments) {
                    if (this.pVehicle.vParts[n].rLength > this.pVehicle.ropeMin) {
                        this.pVehicle.vParts[n].rLength *= 0.9;
                    }
                    ++n;
                }
                this.worldParameters.unwindRope = false;
            }
            if (this.worldParameters.fireHook && !this.pVehicle.a[this.pVehicle.ropeSlot]) {
                this.pVehicle.h[this.pVehicle.ropeSlot] = false;
                this.pVehicle.a[this.pVehicle.ropeSlot] = true;
                this.worldParameters.fireHook = false;
                n = 0;
                while (n < this.pVehicle.maxRopeSegments) {
                    this.pVehicle.partList[this.pVehicle.ropeSegments[this.pVehicle.ropeSlot] + n].gToggle = true;
                    this.pVehicle.vParts[this.pVehicle.ropeAnchor[this.pVehicle.ropeSlot] + n].partActive = true;
                    this.pVehicle.partList[this.pVehicle.ropeSegments[this.pVehicle.ropeSlot] + n].lx =
                            this.pVehicle.partList[this.pVehicle.pCounter].lx + (double) ((1 + n) * 2) *
                                    Math.cos(this.pVehicle.turretAngle);
                    this.pVehicle.partList[this.pVehicle.ropeSegments[this.pVehicle.ropeSlot] + n].ly =
                            this.pVehicle.partList[this.pVehicle.pCounter].ly + (double) ((1 + n) * 2) *
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
            if (this.worldParameters.leftPressed || this.worldParameters.rightPressed) {
                int n2 = this.worldParameters.leftPressed ? 1 : -1;
                int n3 = 0;
                while (n3 < 2) {
                    n = 0;
                    while (n < pVehicle.maxRopeSegments) {
                        int n4 = n + 11 * n3;
                        this.pVehicle.partList[n4].dy +=
                                (this.pVehicle.partList[pVehicle.maxRopeSegments + 11 * n3].lx -
                                        this.pVehicle.partList[n4].lx) * this.pVehicle.fEngine * (double) n2;
                        this.pVehicle.partList[n4].dx +=
                                (this.pVehicle.partList[n4].ly - this.pVehicle.partList[pVehicle.maxRopeSegments +
                                        11 * n3].ly) * this.pVehicle.fEngine * (double) n2;
                        ++n;
                    }
                    ++n3;
                }
            }
            n = 0;
            while (n < this.pVehicle.nf) {
                if (this.pVehicle.vParts[n].partActive) {
                    double d6 = this.pVehicle.partList[this.pVehicle.vParts[n].rx].lx - this.pVehicle.partList[this.pVehicle.vParts[n].ry].lx;
                    double d7 = this.pVehicle.partList[this.pVehicle.vParts[n].rx].ly - this.pVehicle.partList[this.pVehicle.vParts[n].ry].ly;
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
                    if (this.worldParameters.checkPosition((int) this.pVehicle.partList[n].lx, (int) this.pVehicle.partList[n].ly) == 'w') {
                        this.pVehicle.partList[n].dy += (0.5 - this.pVehicle.buoyancy) * this.worldParameters.dt * this.worldParameters.gravity;
                        this.pVehicle.partList[n].dx *= this.worldParameters.frictionW;
                        this.pVehicle.partList[n].dy *= this.worldParameters.frictionW;
                    } else {
                        this.pVehicle.partList[n].dy += this.worldParameters.dt * this.worldParameters.gravity;
                        this.pVehicle.partList[n].dx *= this.worldParameters.friction;
                        this.pVehicle.partList[n].dy *= this.worldParameters.friction;
                    }
                    if (!(n == this.pVehicle.ropeSegments[0] + this.pVehicle.maxRopeSegments - 1 && this.pVehicle.h[0] || n == this.pVehicle.ropeSegments[1] + this.pVehicle.maxRopeSegments - 1 && this.pVehicle.h[1])) {
                        d2 = this.pVehicle.partList[n].lx;
                        d3 = this.pVehicle.partList[n].ly;
                        this.pVehicle.partList[n].lx += this.worldParameters.dt * this.pVehicle.partList[n].dx;
                        this.pVehicle.partList[n].ly += this.worldParameters.dt * this.pVehicle.partList[n].dy;
                    }
                    if ((c2 = this.worldParameters.checkPosition((int) this.pVehicle.partList[n].lx, (int) this.pVehicle.partList[n].ly)) != 'l' && c2 != 'w' || this.pVehicle.partList[n].lx < 0.0 || this.pVehicle.partList[n].lx > (double) (this.worldParameters.levelWidth - 1) || this.pVehicle.partList[n].ly < 0.0 || this.pVehicle.partList[n].ly > (double) (this.worldParameters.levelHeight - 1)) {
                        if (n == this.pVehicle.ropeSegments[0] + this.pVehicle.maxRopeSegments - 1) {
                            this.pVehicle.h[0] = true;
                        } else if (n == this.pVehicle.ropeSegments[1] + this.pVehicle.maxRopeSegments - 1) {
                            this.pVehicle.h[1] = true;
                        } else {
                            this.pVehicle.partList[n].lx = d2;
                            this.pVehicle.partList[n].ly = d3;
                            this.pVehicle.partList[n].dx = 0.0;
                            this.pVehicle.partList[n].dy = 0.0;
                        }
                    }
                }
                ++n;
            }
            ++this.gameCounter;
            this.gameCounter %= 2;
            if (this.gameCounter != 0) continue;

            graphics.setColor(Color.GRAY);
            graphics.fillRect(10, 10, 85, 18);
            if (this.mouseY < 33 && this.mouseX < 100) {
                graphics.setColor(new Color(0, 0, 255));
            } else {
                graphics.setColor(Color.black);
            }
            graphics.drawRect(10, 10, 85, 18);
            graphics.drawString("GitHub", 13, 23);

            this.repaint();
            //update(this.graphics);
            if (this.worldParameters.delay <= 0) continue;
            try {
                Thread.sleep(this.worldParameters.delay);
            } catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public Image loadImage(String param) {
        String imagePath = gp.paramMap.get(param); // Assuming getParameter is available in your context
        Image imgBuffer = null;
        if (imagePath != null) {
            // Assuming the image is in the same directory as your class
            File imageFile = new File(imagePath);

            try {
                imgBuffer = ImageIO.read(imageFile);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception appropriately based on your requirements
            }
        } else {
            // Handle the case where the parameter "Bild" is not found or is null
        }
        return imgBuffer;
    }

    public void openURLInBrowser(URL url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(url.toURI());
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
                // Handle the exception appropriately based on your requirements
            }
        } else {
            // Desktop not supported, handle this case if needed
        }
    }

    static class GameConstants {
        int hexMax = 0xFFFFFF;
        int maxNumber = 11129855;
        int minNumber = 10643504;
        int hexLow = 0x828181;
        int theConst = 8023138;

        GameConstants() {
        }
    }

    class GameControls extends KeyAdapter {
        int pressedKey;

        GameControls() {
            // Do nothing
        }

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
        }

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
                    URL uRL = new URL("http://www.eigelb.at");
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
        char[][] n;
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
        double a;
        double maxFrameHeight;
        double scaleM;
        double scaleSize;
        double scaleF;
        int delay;

        WorldParameters() {
            this.maxFrameWidth = screenWidth - 1;
            this.a = 0.0;
            this.maxFrameHeight = screenHeight - 1;
            this.levelWidth = parseInt(gp.paramMap.get("Bild_w"));
            this.levelHeight = parseInt(gp.paramMap.get("Bild_h"));
            this.scaleM = 0.01 * (double) parseInt(gp.paramMap.get("scaleM"));
            this.scaleF = 0.01 * (double) parseInt(gp.paramMap.get("scaleF"));
            this.scaleSize = 0.01 * (double) parseInt(gp.paramMap.get("scaleSize"));
            this.dt = 0.001 * (double) parseInt(gp.paramMap.get("dt"));
            this.delay = parseInt(gp.paramMap.get("delay"));
            this.gravity = parseInt(gp.paramMap.get("Gravity"));
            this.friction = 0.001 * (double) parseInt(gp.paramMap.get("Reibung"));
            this.frictionW = 0.001 * (double) parseInt(gp.paramMap.get("ReibungW"));
            this.n = new char[this.levelWidth][this.levelHeight];
        }

        public char checkPosition(int x, int y) {
            if (x < 0 || y < 0 || x >= this.levelWidth || y >= this.levelHeight) {
                return 'e';
            }
            return this.n[x][y];
        }
    }

    class PlayerVehicle {
        int pCounter;
        boolean[] a = new boolean[2];
        boolean[] h = new boolean[2];
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
            this.py = connectors;
            this.px = parts;
            this.vParts = new VehiclePart[this.py];
            this.partList = new Connector[this.px];
            this.mHook = 0.01 * (double) parseInt(gp.paramMap.get("mHook"));
            this.mRope = 0.01 * (double) parseInt(gp.paramMap.get("mRope"));
            this.mWheels = 0.01 * (double) parseInt(gp.paramMap.get("mWheels"));
            this.mAxis = 0.01 * (double) parseInt(gp.paramMap.get("mAxis"));
            this.mCorpus = 0.01 * (double) parseInt(gp.paramMap.get("mCorpus"));
            this.ropeMin = worldParameters.scaleF * (double) parseInt(gp.paramMap.get("FRopeMin"));
            this.ropeMax = worldParameters.scaleF * (double) parseInt(gp.paramMap.get("FRopeMax"));
            this.fWheels = parseInt(gp.paramMap.get("FWheels"));
            this.fCorpus = parseInt(gp.paramMap.get("FCorpus"));
            this.fEngine = 0.1 * worldParameters.scaleSize * worldParameters.dt *
                    (double) parseInt(gp.paramMap.get("FEngine"));
            this.v0Rope = parseInt(gp.paramMap.get("v0Rope"));
        }

        public void updatePos() {
            int n = 0;
            while (n < this.nf) {
                if (this.vParts[n].partActive) {
                    this.vParts[n].drawPart();
                }
                ++n;
            }
            n = 0;
            while (n < this.np) {
                if (this.partList[n].gToggle) {
                    this.partList[n].drawConnector();
                }
                ++n;
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
        Color colorG;
        boolean gToggle;

        public Connector(double x, double y, double d4, Color color) {
            this.lx = x;
            this.ly = y;
            this.a = d4;
            this.dx = 0.0;
            this.dy = 0.0;
            this.colorG = color;
            this.gToggle = true;
        }

        public void drawConnector() {
            graphics.setColor(this.colorG);
            graphics.fillRect((int) (this.lx - 1.0 - (double) worldParameters.viewportX),
                    (int) (this.ly - 1.0 - (double) worldParameters.viewportY), 3, 3);
        }
    }

    class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Your drawing logic here
        }
    }
}