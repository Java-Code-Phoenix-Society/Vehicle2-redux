package org.jcps.vehicle2redux;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * OptionsPanel class that contains the JPanel setup to show the Game options.
 */
public class OptionsPanel extends JPanel {
    /**
     * Title JLabel
     */
    private JLabel titleLabel;
    /**
     * Checkbox to an option
     */
    private JCheckBox soundCheckBox;
    /**
     * Difficulty combo box
     */
    private JComboBox<String> difficultyComboBox;
    /**
     * Button to exit the options.
     */
    private JButton btnMenu;
    /**
     * Listeners registered with this instance.
     */
    final private ArrayList<TriggerListener> listeners = new ArrayList<>();

    /**
     * Creates the options panel
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
//        titleLabel.setPreferredSize(new Dimension(400,80));
        String[] difficultyLevels = {"Easy", "Medium", "Hard"};
        difficultyComboBox = new JComboBox<>(difficultyLevels);
        JLabel lblBlank = new JLabel(" ");
        lblBlank.setFont(new Font("Arial", Font.BOLD, 18));
        soundCheckBox = new JCheckBox();
        btnMenu = new JButton("Return to menu");
        btnMenu.addActionListener(e -> returnToMenu());

        add(titleLabel,gbc);
        add(lblBlank,gbc);
        add(new JLabel("Enable Sound:"),gbc);
        add(soundCheckBox,gbc);
        add(new JLabel("Difficulty Level:"),gbc);
        add(difficultyComboBox,gbc);
        add(new JLabel(),gbc); // Empty label for spacing
        add(btnMenu,gbc);
    }

    /**
     * Fire event to return to the main menu.
     */
    private void returnToMenu() {
        fireEvent("exit_loop");
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
}
