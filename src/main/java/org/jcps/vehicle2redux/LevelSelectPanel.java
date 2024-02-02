package org.jcps.vehicle2redux;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The {@code LevelSelectPanel} class represents a custom JPanel for selecting a game level to play.
 * It presents a grid of buttons, each corresponding to a level, with the associated art image.
 * <p>
 * Users can navigate through multiple pages of levels and interact with the game by selecting a level to play.
 * The panel emits events based on the user's level selection, which can be captured by registered {@code TriggerListeners}.
 * </p>
 * <p>
 * <i>Note: The layout, appearance, and behaviour of this panel are customised to suit the requirements of the game.</i>
 * </p>
 *
 * @see javax.swing.JPanel
 * @see TriggerListener
 * @since 1.0
 */
public class LevelSelectPanel extends JPanel {
    /**
     * A constant representing the number of level buttons to be displayed per page.
     */
    private static final int BUTTONS_PER_PAGE = 4;
    /**
     * A list of {@code TriggerListeners} registered with this panel. These listeners will be notified
     * when certain events occur within this panel.
     *
     * @see TriggerListener
     */
    final private ArrayList<TriggerListener> listeners = new ArrayList<>();
    /**
     * A list of {@code JButtons} representing the levels. Each button corresponds to a level and shows an associated image.
     *
     * @see javax.swing.JButton
     */
    ArrayList<JButton> btnLevels;
    /**
     * The current page number being displayed by the panel. This value changes as the user navigates through the level pages.
     */
    private int currentPage;

    /**
     * Constructs a {@code LevelSelectPanel} object and populates it with level buttons based on the provided list of {@code LevelMaps}.
     * The panel is initially set to display the first page of level buttons.
     *
     * @param maps A list of {@code LevelMap} objects, each containing information about a game level.
     * @see LevelMap
     */
    public LevelSelectPanel(ArrayList<LevelMap> maps) {
        btnLevels = new ArrayList<>();

        int mapIndex = 0;
        for (LevelMap map : maps) {
            JButton btn = createImageButton(map.get("Bild_c"));
            int finalMapIndex = mapIndex;
            btn.addActionListener(e -> {
                JButton button = (JButton) e.getSource();
                String btnText = button.getText();
                if (V2RMain.DEBUG) System.out.println("Selected level: " + btnText);
                fireEvent("map:" + finalMapIndex);
            });
            btnLevels.add(btn);
            mapIndex++;
        }

        // Display the buttons for the first page
        updateButtonsForPage(0);

        currentPage = 0;
    }

    /**
     * Calculates and returns the new dimensions of an image when scaled to fit within a 200x200 pixel area
     * while preserving the original aspect ratio.
     *
     * @param originalWidth  The original width of the image.
     * @param originalHeight The original height of the image.
     * @return An array containing the new width and height of the image after scaling.
     */
    private static int[] scaleImage(int originalWidth, int originalHeight) {
        int newWidth;
        int newHeight;

        // Calculate new dimensions while preserving aspect ratio
        if (originalWidth > originalHeight) {
            // Landscape orientation
            newWidth = 200;
            newHeight = (int) ((double) originalHeight / originalWidth * 200);
        } else {
            // Portrait or square orientation
            newHeight = 200;
            newWidth = (int) ((double) originalWidth / originalHeight * 200);
        }

        return new int[]{newWidth, newHeight};
    }

    /**
     * Creates a level selection {@code JButton} with an associated image read from the specified file path.
     * The button's text is set to the name of the image file (without the extension).
     *
     * @param imagePath The path to the image file to be displayed on the button.
     * @return A {@code JButton} with the scaled image and text.
     * @see javax.swing.JButton
     */
    private JButton createImageButton(String imagePath) {
        JButton button = new JButton();
        String description = "";

        try {
            int startIndex = imagePath.lastIndexOf("/") + 1;
            int endIndex = imagePath.lastIndexOf(".");
            BufferedImage img = ImageIO.read(new File(imagePath));

            int[] scaleImage = scaleImage(img.getWidth(), img.getHeight());

            Image scaledImg = img.getScaledInstance(scaleImage[0], scaleImage[1], Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaledImg);

            if (startIndex < 0 || endIndex < 0 || startIndex >= endIndex) {
                // Handle invalid path or file extension not found
                description = imagePath;
            }
            button.setIcon(icon);
            description = imagePath.substring(startIndex, endIndex);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setText("<html><center>" + description + "</center></html>");
        }

        return button;
    }

    /**
     * Triggers a {@code TriggerEvent} to all registered {@code TriggerListeners}. The event carries a message
     * that is used by the listeners to determine the appropriate action in response to the event.
     *
     * @param message The message associated with the event, typically conveying information about the event's cause or context.
     * @see TriggerListener
     * @see TriggerEvent
     */
    public void fireEvent(String message) {
        TriggerEvent event = new TriggerEvent(this, message);
        for (TriggerListener listener : listeners) {
            listener.onEventOccurred(event);
        }
    }

    /**
     * Adds a {@code TriggerListener} to this panel's list of event listeners. These listeners are notified
     * when certain events occur within this panel, such as a level selection.
     *
     * @param listener The {@code TriggerListener} to be added.
     * @see TriggerListener
     */
    public void addEventListener(TriggerListener listener) {
        listeners.add(listener);
    }

    /**
     * Updates the level buttons displayed on the panel to correspond to the specified page number.
     * The method removes any existing buttons, adds the buttons for the new page, and updates the page navigation controls.
     *
     * @param page The page number of the level buttons to be displayed.
     */
    private void updateButtonsForPage(int page) {
        removeAll(); // Clear the existing components
        setLayout(new BorderLayout()); // Reset the layout manager

        // Create a panel for the level buttons with GridLayout
        JPanel levelsPanel = new JPanel(new GridLayout(2, 2));
        int startIndex = page * BUTTONS_PER_PAGE;
        int endIndex = Math.min(startIndex + BUTTONS_PER_PAGE, btnLevels.size());
        int totalPages = (int) Math.ceil((double) btnLevels.size() / BUTTONS_PER_PAGE);

        for (int i = startIndex; i < endIndex; i++) {
            levelsPanel.add(btnLevels.get(i));
        }

        // Add the panels to the main panel
        add(levelsPanel, BorderLayout.CENTER);

        // Update the page label and buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevButton = new JButton("<");
        JLabel pageLabel = new JLabel("Page " + (page + 1));
        JButton nextButton = new JButton(">");
        JButton exitButton = new JButton("Exit");
        JButton endButton = new JButton(">|"); // placeholder button

        pageLabel.setSize(100, 20);
        prevButton.addActionListener(e -> navigatePage(-1));
        prevButton.setEnabled(page != 0);
        nextButton.addActionListener(e -> navigatePage(1));
        nextButton.setEnabled(page != totalPages - 1);
        exitButton.addActionListener(e -> fireEvent("exit_loop"));

        bottomPanel.add(exitButton);
        bottomPanel.add(prevButton);
        bottomPanel.add(pageLabel);
        bottomPanel.add(nextButton);
        bottomPanel.add(endButton);

        // Add the panels to the main panel
        add(bottomPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    /**
     * Changes the displayed page of level buttons by a specified delta.
     * A positive delta navigates to subsequent pages, while a negative delta navigates to previous pages.
     *
     * @param delta The change in page number (negative for previous, positive for next).
     */
    private void navigatePage(int delta) {
        int newPage = currentPage + delta;
        if (newPage >= 0 && newPage < Math.ceil((double) btnLevels.size() / BUTTONS_PER_PAGE)) {
            currentPage = newPage;
            updateButtonsForPage(currentPage);
        }
    }
}
