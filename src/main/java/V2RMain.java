import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * The main class representing the game application for Vehicle 2: Redux.
 * Extends JFrame and implements TriggerListener for handling events.
 */
public class V2RMain extends JFrame implements TriggerListener {
    // Constants for the game state
    public final static int MENU_PANEL = 0;
    public final static int GAME_PANEL = 1;
    public final static int OPTIONS_PANEL = 2;

    // General variables
    public static int gameState;
    public Vehicle2 objV2;
    private int prevLevel;
    private static Timer timer = null;
    private static MenuPanel mp;
    private static LevelSelectPanel lsp;
    private final int delay = 10;

    /**
     * Constructs the V2RMain frame for the game.
     */
    public V2RMain() {
        setTitle("V2: Redux");
        setUndecorated(false);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocation(0, 0);
        setLayout(new BorderLayout());
        prevLevel = -1;

        // Initialize game loop
        timer = new Timer(delay, e -> {
            update(); // Method to update game state
        });
        timer.setInitialDelay(20);
        mp = new MenuPanel();

        // Add window listener to handle closing action
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                Vehicle2.levelTimes = LevelUtilities.readHashmapFromFile("Level-times.txt");
            }

            @Override
            public void windowClosing(WindowEvent e) {
                // Clean up resources if needed
                timer.stop(); // Stop the game loop
                dispose();    // Dispose the JFrame and exit the application
            }

            @Override
            public void windowClosed(WindowEvent e) {
                LevelUtilities.writeHashmapToFile(Vehicle2.levelTimes, "Level-times.txt");
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

    /**
     * The main method to start the V2RMain application.
     *
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        V2RMain v2r = new V2RMain();
        v2r.objV2 = new Vehicle2();
        lsp = new LevelSelectPanel(v2r.objV2.maps);

        // Calculate the center position
        int centerX = (screenSize.width - 800) / 2;
        int centerY = (screenSize.height - 600) / 2;

        // Set the frame location
        v2r.setLocation(centerX, centerY);

        v2r.objV2.addEventListener(v2r);
        v2r.lsp.addEventListener(v2r);
        v2r.add(mp, BorderLayout.CENTER);

        gameState = MENU_PANEL;
        v2r.revalidate();
        v2r.repaint();
    }

    /** Method to update game logic */
    private void update() {
        if (gameState == GAME_PANEL) {
            if (objV2.runState) {
                objV2.run();
            } else {
                // Stop the timer from running a duplicate loop
                timer.stop();

                if (prevLevel != -2) {
                    if (prevLevel < objV2.currentLevel) {
                        ++objV2.currentLevel;
                        prevLevel++;
                    }
                }
                if (objV2.currentLevel < objV2.maps.size()) {
                    if (objV2.gp != objV2.maps.get(objV2.currentLevel).lp) {
                        objV2.init();
                        timer.start();
                    } else {
                        returnToMenu();
                    }
                } else {
                    returnToMenu();
                }
            }
        }

        if (gameState == MENU_PANEL) {
            if (timer.isRunning()) timer.stop();
        }

        repaint();
    }

    /** Returns to the main menu.
    */
    private void returnToMenu() {
        gameState = MENU_PANEL;
        getContentPane().removeAll();
        add(mp);
    }

    /**
     * Initiates the process of starting the game.
     */
    public void startGame() {
        // Simple protection for no maps
        if (objV2.maps.size() != 0) {
            this.getContentPane().removeAll();
            this.add(objV2);
            gameState = GAME_PANEL;
            objV2.init();
            timer.start();
        } else {
            JOptionPane.showMessageDialog(null,
                    "Can not start the game. There are no map files in the Levels/ directory",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** Processes events fired from children.
     *
     * @param event Incoming event from trigger
     */
    @Override
    public void onEventOccurred(TriggerEvent event) {
        System.out.println("Event occurred: " + event.getMessage());
        if (event.getMessage().equals("exit_loop")) {
            if (gameState == MENU_PANEL) {
                System.exit(0);
            }

            if (gameState == GAME_PANEL) {
                timer.stop();
                gameState = MENU_PANEL;
                objV2.currentLevel = 0;
                prevLevel = -1;
                this.getContentPane().removeAll();
                this.add(mp);
            }
        }
        if (event.getMessage().contains("map:")) {
            String msg = event.getMessage();
            msg = msg.substring(msg.lastIndexOf(':') + 1);

            System.out.println("Go to map: " + msg);
            objV2.currentLevel = Integer.parseInt(msg);
            prevLevel = -2;
            startGame();
        }

        revalidate();
        repaint();
    }

    /**
     * Opens the options menu (Temporary the level select menu).
     */
    private void openOptions() {
        // Simple protection for no maps
        if (objV2.maps.size() != 0) {
            this.getContentPane().removeAll();
            this.add(lsp);
            gameState = OPTIONS_PANEL;
        } else {
            JOptionPane.showMessageDialog(null,
                    "Can not start the game. There are no map files in the Levels/ directory",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
        }
        revalidate();
        repaint();
    }

    /**
     * Represents a panel containing menu buttons for starting, configuring options, and exiting the game.
     */
    public class MenuPanel extends JPanel {
        JButton btnStart;
        JButton btnExit;
        JButton btnOptions;

        /**
         * Constructs a MenuPanel with buttons for starting the game, accessing options, and exiting the game.
         */
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
            btnStart.addActionListener(e -> startGame());

            btnOptions = new JButton("Options (LS TEMP)");
            btnOptions.setMnemonic(KeyEvent.VK_O);
            btnOptions.addActionListener(e -> openOptions());

            btnExit = new JButton("Exit Game");
            btnExit.setMnemonic(KeyEvent.VK_X);
            btnExit.addActionListener(e -> {
                LevelUtilities.writeHashmapToFile(Vehicle2.levelTimes, "Level-times.txt");
                System.exit(0);
            });

            centeredPanel.add(btnStart);
            centeredPanel.add(btnOptions);
            centeredPanel.add(btnExit);
            add(centeredPanel, gbc);
        }
    }
}
