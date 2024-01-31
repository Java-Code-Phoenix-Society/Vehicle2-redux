package org.jcps.vehicle2redux;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * The main class representing the game application for Vehicle 2: Redux.
 * Extends JFrame and implements TriggerListener for handling events.
 */
public class V2RMain extends JFrame implements TriggerListener {
    // Constants for the game state
    /**
     * Represents the game state when at the main menu panel.
     */
    public final static int MENU_PANEL = 0;

    /**
     * Represents the game state when at the game panel.
     */
    public final static int GAME_PANEL = 1;

    /**
     * Represents the game state when at the options panel.
     */
    public final static int OPTIONS_PANEL = 2;

    /**
     * Represents the game state when at the level selection panel.
     */
    public final static int SELECT_PANEL = 3;

    public final static int BEST_TIMES = 4;

    // General variables

    /**
     * The current game state.
     */
    public static int gameState;
    /**
     * Static debug flag. Set to true to show debugging information.
     */
    public static boolean DEBUG = true;
    /**
     * The Timer instance for handling game events.
     */
    private static Timer timer = null;
    /**
     * The MenuPanel instance for the main menu.
     */
    private final MenuPanel mp;
    /**
     * The OptionsPanel instance for managing the game options.
     */
    private final OptionsPanel optionsPanel;

    private BestTimesPanel btp;
    /**
     * The delay interval for the timer (in milliseconds).
     */
    private final int delay = 10;
    /**
     * The main Vehicle2 instance for the game.
     */
    public Vehicle2 vehicle2r;
    /**
     * The previous level in the game.
     */
    private int prevLevel;
    /**
     * The LevelSelectPanel instance for level selection.
     */
    private LevelSelectPanel levelSelectPanel;

    /**
     * Constructs the V2RMain frame for the game.
     */
    public V2RMain() {
        setTitle("V2: Redux");
        setUndecorated(true);
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

        this.mp = new MenuPanel();
        this.optionsPanel = new OptionsPanel();

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
        v2r.vehicle2r = new Vehicle2();
        v2r.levelSelectPanel = new LevelSelectPanel(v2r.vehicle2r.maps);
        v2r.btp = new BestTimesPanel();

        // Calculate the center position
        int centerX = (screenSize.width - 800) / 2;
        int centerY = (screenSize.height - 600) / 2;

        // Set the frame location
        v2r.setLocation(centerX, centerY);

        v2r.vehicle2r.addEventListener(v2r);
        v2r.levelSelectPanel.addEventListener(v2r);
        v2r.optionsPanel.addEventListener(v2r);
        v2r.btp.addEventListener(v2r);
        v2r.add(v2r.mp, BorderLayout.CENTER);

        gameState = MENU_PANEL;
        v2r.revalidate();
        v2r.repaint();
    }

    /**
     * Method to update game logic
     */
    private void update() {
        if (gameState == GAME_PANEL) {
            if (vehicle2r.runState) {
                vehicle2r.run();
            } else {
                // Stop the timer from running a duplicate loop
                timer.stop();

                if (prevLevel != -2) {
                    if (prevLevel < vehicle2r.currentLevel) {
                        ++vehicle2r.currentLevel;
                        prevLevel++;
                    }
                }
                if (vehicle2r.currentLevel < vehicle2r.maps.size()) {
                    if (vehicle2r.gp != vehicle2r.maps.get(vehicle2r.currentLevel).lp) {
                        vehicle2r.init();
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

    /**
     * Returns to the main menu.
     */
    private void returnToMenu() {
        gameState = MENU_PANEL;
        getContentPane().removeAll();
        add(this.mp);
    }

    /**
     * Initiates the process of starting the game.
     */
    public void startGame() {
        // Simple protection for no maps
        if (vehicle2r.maps.size() != 0) {
            this.getContentPane().removeAll();
            this.add(vehicle2r);
            gameState = GAME_PANEL;
            vehicle2r.init();
            timer.start();
        } else {
            JOptionPane.showMessageDialog(null,
                    "Can not start the game. There are no map files in the Levels/ directory",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Processes events fired from children.
     *
     * @param event Incoming event from trigger
     */
    @Override
    public void onEventOccurred(TriggerEvent event) {
        if (DEBUG) System.out.println("Event occurred: " + event.getMessage());
        if (event.getMessage().equals("exit_loop")) {
            if (gameState == MENU_PANEL) {
                System.exit(0);
            }
            if (gameState == OPTIONS_PANEL || gameState == SELECT_PANEL || gameState == BEST_TIMES) {
                timer.stop();
                gameState = MENU_PANEL;
                this.getContentPane().removeAll();
                this.add(this.mp);
            }
            if (gameState == GAME_PANEL) {
                timer.stop();
                gameState = MENU_PANEL;
                vehicle2r.currentLevel = 0;
                prevLevel = -1;
                this.getContentPane().removeAll();
                this.add(this.mp);
            }
        }
        if (event.getMessage().contains("gotoBestTimes")) {
            gameState = BEST_TIMES;
            this.getContentPane().removeAll();
            this.btp = new BestTimesPanel();
            this.btp.addEventListener(this);
            this.add(this.btp);
        }
        if (event.getMessage().contains("map:")) {
            String msg = event.getMessage();
            msg = msg.substring(msg.lastIndexOf(':') + 1);

            if (DEBUG) System.out.println("Go to map: " + msg);
            vehicle2r.currentLevel = Integer.parseInt(msg);
            prevLevel = -2;
            startGame();
        }

        revalidate();
        repaint();
    }

    /**
     * Opens the options menu.
     */
    private void openOptions() {
        this.getContentPane().removeAll();
        this.add(this.optionsPanel);
        gameState = OPTIONS_PANEL;
        revalidate();
        repaint();
    }

    private void openLevelSelect() {
        // Simple protection for no maps
        if (vehicle2r.maps.size() != 0) {
            this.getContentPane().removeAll();
            this.add(this.levelSelectPanel);
            gameState = SELECT_PANEL;
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
        /**
         * Button to initiate the start of the game.
         */
        JButton btnStart;

        /**
         * Button to exit the game.
         */
        JButton btnExit;

        /**
         * Button to access and configure game options.
         */
        JButton btnOptions;

        /**
         * Button to navigate to the level selection panel.
         */
        JButton btnLevelSelect;

        /**
         * Constructs a MenuPanel with buttons for starting the game, accessing options, and exiting the game.
         */
        public MenuPanel() {
            Dimension buttonSize = new Dimension(200, 40);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.weightx = 1.0; // Occupy full width
            gbc.fill = GridBagConstraints.RELATIVE;

            setLayout(new GridBagLayout());

            // Set up "Start Game" button
            btnStart = createButton("Start Game", buttonSize, KeyEvent.VK_S, e -> startGame());

            // Set up Level Select button
            btnLevelSelect = createButton("Level Select", buttonSize, KeyEvent.VK_L, e -> openLevelSelect());

            // Set up Options button
            btnOptions = createButton("Options", buttonSize, KeyEvent.VK_O, e -> openOptions());

            // Set up exit button
            btnExit = createButton("Exit Game", buttonSize, KeyEvent.VK_X, e -> {
                LevelUtilities.writeHashmapToFile(Vehicle2.levelTimes, "Level-times.txt");
                System.exit(0);
            });

            // Add to panel
            add(btnStart, gbc);
            add(btnLevelSelect, gbc);
            add(btnOptions, gbc);
            add(btnExit, gbc);
        }

        /**
         * Creates and configures a JButton with the specified text, size, mnemonic, and ActionListener.
         *
         * @param text     The text to be displayed on the button.
         * @param size     The preferred size of the button.
         * @param mnemonic The mnemonic key code for keyboard shortcut.
         * @param action   The ActionListener to be triggered when the button is clicked.
         * @return A configured JButton with the specified properties.
         */
        private JButton createButton(String text, Dimension size, int mnemonic, ActionListener action) {
            JButton button = new JButton(text);
            button.setPreferredSize(size);
            button.setMnemonic(mnemonic);
            button.addActionListener(action);
            return button;
        }
    }
}
