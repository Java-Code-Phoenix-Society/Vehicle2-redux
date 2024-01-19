import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class V2RMain extends JFrame {
    private static Timer timer = null;
    private final int delay = 10; // Adjust the delay based on your game's requirements (in milliseconds)
    public static Vehicle2 objV2;
    private static MenuPanel mp;
    public final static int MENU_PANEL = 0;
    public final static int OPTIONS_PANEL = 1;
    public final static int GAME_PANEL = 2;
    public static int gameState;

    public V2RMain() {
        setTitle("V2: Redux");
        setUndecorated(false);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocation(0, 0);
        setLayout(new BorderLayout());


        // Initialize game loop
        timer = new Timer(delay, e -> {
            update(); // Method to update game state
        });

        mp = new MenuPanel();
        // Add window listener to handle closing action
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                // Clean up resources if needed
                timer.stop(); // Stop the game loop
                LevelUtilities.writeHashmapToFile(Vehicle2.levelTimes, "Level-times.txt");
                dispose();    // Dispose the JFrame and exit the application
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        // Make the frame visible
        setVisible(true);
    }

    // Method to update game logic
    private void update() {
        if (gameState == GAME_PANEL) {
            if (objV2.runState) {
                objV2.run();
            } else {
                // Stop the timer from running a duplicate loop
                timer.stop();

                // Capture an issue with level skipping
                if ((System.currentTimeMillis() - objV2.levelStartTime) < 1000) {
                    ++objV2.currentLevel;
                }

                if (objV2.currentLevel < objV2.maps.size()) {
                    objV2.init();
                    timer.start();
                } else {
                    gameState = MENU_PANEL;
                    getContentPane().removeAll();
                    add(mp);
                }
            }
        }

        if (gameState == MENU_PANEL) {
            if (timer.isRunning()) timer.stop();
        }

        repaint();
    }

    public static void main(String[] args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        V2RMain v2r = new V2RMain();
        objV2 = new Vehicle2();

        v2r.add(mp, BorderLayout.CENTER);

        // Calculate the center position
        int centerX = (screenSize.width - 800) / 2;
        int centerY = (screenSize.height - 600) / 2;

        // Set the frame location
        v2r.setLocation(centerX, centerY);

        gameState = MENU_PANEL;
    }

    public void startGame() {
        this.getContentPane().removeAll();
        this.add(objV2);
        gameState = GAME_PANEL;
        //setUndecorated(true);
        objV2.init();
        timer.start();
        //revalidate();
    }

    public class MenuPanel extends JPanel {
        JButton btnStart;
        JButton btnExit;
        JButton btnOptions;

        public MenuPanel() {
            // Set up GridBagLayout for the frame
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.CENTER;

            // Create a nested JPanel with FlowLayout to center the buttons
            JPanel centeredPanel = new JPanel();
            centeredPanel.setLayout(new BoxLayout(centeredPanel, BoxLayout.Y_AXIS));

            btnStart = new JButton("Start Game");
            btnStart.setMnemonic(KeyEvent.VK_S);
            btnStart.addActionListener(e -> {
                startGame();
                timer.start();
            });

            btnOptions = new JButton("Options");
            btnOptions.setMnemonic(KeyEvent.VK_O);
            btnOptions.addActionListener(e -> System.exit(0));

            btnExit = new JButton("Exit Game");
            btnExit.setMnemonic(KeyEvent.VK_X);
            btnExit.addActionListener(e -> System.exit(0));

            centeredPanel.add(btnStart);
            centeredPanel.add(btnOptions);
            centeredPanel.add(btnExit);
            add(centeredPanel, gbc);
        }
    }
    /*
    // Set up game panel
        JPanel gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Your rendering code goes here
            }
        };
    */
}
