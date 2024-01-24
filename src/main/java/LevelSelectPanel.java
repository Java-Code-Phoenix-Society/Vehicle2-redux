import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Represents a panel for selecting levels in a game.
 */
public class LevelSelectPanel extends JPanel {
    private static final int BUTTONS_PER_PAGE = 4;
    final private ArrayList<TriggerListener> listeners = new ArrayList<>();
    ArrayList<JButton> btnLevels;
    private int currentPage;

    /**
     * Constructs a LevelSelectPanel with a list of LevelMap objects.
     *
     * @param maps A list of LevelMap objects containing level information.
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
                System.out.println("Selected level: " + btnText);
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
     * Scales the dimensions of the original image while preserving aspect ratio.
     *
     * @param originalWidth  The original width of the image.
     * @param originalHeight The original height of the image.
     * @return An array containing the new width and height after scaling.
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
     * Creates a JButton with an image from the specified file path.
     *
     * @param imagePath The path to the image file.
     * @return The created JButton with the scaled image.
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
            //button.setText(description);
        }

        return button;
    }

    /**
     * Fires a TriggerEvent to all registered TriggerListeners.
     *
     * @param message The message associated with the event.
     */
    public void fireEvent(String message) {
        TriggerEvent event = new TriggerEvent(this, message);
        for (TriggerListener listener : listeners) {
            listener.onEventOccurred(event);
        }
    }

    /**
     * Adds a TriggerListener to the list of event listeners.
     *
     * @param listener The TriggerListener to be added.
     */
    public void addEventListener(TriggerListener listener) {
        listeners.add(listener);
    }

    /**
     * Updates the displayed buttons for the specified page.
     *
     * @param page The page number to display.
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
        JButton prevButton = new JButton("Previous Page");
        JLabel pageLabel = new JLabel("Page " + (page + 1));
        JButton nextButton = new JButton("Next Page");

        prevButton.addActionListener(e -> navigatePage(-1));
        prevButton.setEnabled(page != 0);
        nextButton.addActionListener(e -> navigatePage(1));
        nextButton.setEnabled(page != totalPages - 1);

        bottomPanel.add(prevButton);
        bottomPanel.add(pageLabel);
        bottomPanel.add(nextButton);

        // Add the panels to the main panel
        add(bottomPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    /**
     * Navigates to the previous or next page based on the specified delta.
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
