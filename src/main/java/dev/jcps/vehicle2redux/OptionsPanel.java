package dev.jcps.vehicle2redux;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * The {@code OptionsPanel} class extends a {@link JPanel} to create a customised panel for the game's options settings.
 * This panel includes interactive components such as checkboxes, combo boxes, and buttons that allow the user to configure various game settings.
 * Events are generated when the user interacts with these components, which are then handled by registered {@code TriggerListener}s.
 *
 * @see javax.swing.JPanel
 * @see TriggerListener
 */
public class OptionsPanel extends JPanel {
    /**
     * A list of {@code TriggerListeners} that are registered with this {@code OptionsPanel} instance.
     * These listeners are notified of events that occur within this panel, such as changes to game options or navigation requests.
     *
     * @see TriggerListener
     */
    private final ArrayList<TriggerListener> listeners = new ArrayList<>();
    /**
     * A label displaying the title of the options panel.
     */
    private final JLabel titleLabel;
    /**
     * Checkbox for enabling/disabling a game option.
     */
    private final JCheckBox soundCheckBox;
    /**
     * Difficulty combo box example
     */
    private final JComboBox<String> screenSizeCombo;
    /**
     * A button that, when clicked, triggers an event to navigate back to the game's main menu.
     */
    private final JButton btnMenu;
    /**
     * A button that, when clicked, triggers an event to navigate to the 'Best Times' panel.
     */
    private final JButton btnBTPanel;

    /**
     * Constructs a new {@code OptionsPanel} with a pre-defined layout and components.
     * The components are added to the panel using a {@link GridBagLayout} for flexible positioning and sizing.
     */
    public OptionsPanel() {
        // Set layout for the panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0; // Occupy full width
        gbc.fill = GridBagConstraints.RELATIVE;

        //setLayout(new GridLayout(4, 2, 10, 10)); // Adjust the parameters based on your design
        setLayout(new GridBagLayout());

        // Create and add components
        titleLabel = new JLabel("<html><div style='text-align: center;border: 2px solid black;'>Game Options</div></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel lblBlank = new JLabel(" ");
        lblBlank.setFont(new Font("Arial", Font.BOLD, 18));
        btnMenu = new JButton("Return to menu");
        btnMenu.addActionListener(e -> returnToMenu());
        btnBTPanel = new JButton("Best Times");
        btnBTPanel.addActionListener(e -> goBestTimes());

        String[] screenSize = {"320", "640", "800"};
        screenSizeCombo = new JComboBox<>(screenSize);
        screenSizeCombo.setSelectedIndex(1);
        soundCheckBox = new JCheckBox();

        add(titleLabel, gbc);
        add(lblBlank, gbc);
        add(new JLabel("Enable Sound:"), gbc);
        add(soundCheckBox, gbc);
        add(new JLabel("Difficulty Level:"), gbc);
        add(screenSizeCombo, gbc);
        add(btnBTPanel, gbc); // Empty label for spacing
        add(btnMenu, gbc);
    }

    /**
     * Triggers the 'gotoBestTimes' event to navigate to the 'Best Times' panel.
     * This method should be called when a user wants to navigate to the 'Best Times' page.
     */
    private void goBestTimes() {
        fireEvent("gotoBestTimes");
    }

    /**
     * Triggers an 'exit_loop' event to return to the main menu.
     * This method should be called when a user wants to return to the main menu.
     */
    private void returnToMenu() {
        String settings = "";
        settings = (String) screenSizeCombo.getSelectedItem();
        fireEvent("exit_loop:" + settings);
    }

    /**
     * Fires a {@code TriggerEvent} to all registered {@code TriggerListeners}. Each listener will handle the event based on the provided message.
     *
     * @param message The message associated with the event. This message will dictate how the event is handled by the listeners.
     * @see TriggerEvent
     * @see TriggerListener
     */
    public void fireEvent(String message) {
        TriggerEvent event = new TriggerEvent(this, message);
        for (TriggerListener listener : listeners) {
            listener.onEventOccurred(event);
        }
    }

    /**
     * Adds a new {@code TriggerListener} to the list of event listeners for this {@code OptionsPanel}.
     * The added listener will then listen for and handle any future events that are fired from this panel.
     *
     * @param listener The {@code TriggerListener} to be added.
     * @see TriggerListener
     */
    public void addEventListener(TriggerListener listener) {
        listeners.add(listener);
    }
}
