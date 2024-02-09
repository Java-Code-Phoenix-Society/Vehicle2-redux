package org.jcps.vehicle2redux;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * The {@code BestTimesPanel} class is a custom {@link JPanel} that displays the best game times achieved
 * for different levels in the <i>Vehicle 2: Redux</i> game. It contains a title, a table for displaying level times,
 * and a button to return to the main menu. This class has dependencies on the {@link Vehicle2} class for level times
 * and the {@link TriggerListener} interface for event handling.
 * <p>
 * This panel is intended to be used as a part of the game's GUI, providing players with a summary of their best times.
 * </p>
 *
 * @author neoFuzz
 * @version 1.0
 */
public class BestTimesPanel extends JPanel {
    /**
     * A collection of {@code TriggerListener} objects. These listeners are notified when specific events occur within this panel.
     * This mechanism allows other components to respond to changes or events that take place within this panel.
     *
     * @see TriggerListener
     */
    final private ArrayList<TriggerListener> listeners = new ArrayList<>();

    /**
     * A JLabel that displays the title of this panel.
     *
     * @see javax.swing.JLabel
     */
    private final JLabel titleLabel;

    /**
     * A JButton that, when clicked, triggers an event to exit the best times panel and return to the main menu.
     *
     * @see javax.swing.JButton
     */
    private final JButton btnMenu;

    /**
     * Constructs a new instance of the {@code BestTimesPanel}, setting up the layout, title label, table for level times,
     * and the return button. It initialises the event listeners and populates the table with level times data from the {@link Vehicle2} class.
     */
    public BestTimesPanel() {
        // Set layout and GridBagConstraints
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.80; // Occupy full width
        gbc.fill = GridBagConstraints.CENTER;

        // Set up the title label
        titleLabel = new JLabel("<html><div style='text-align: center;'>Best Times</div></html>");
        titleLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 18));

        // Setting up the table
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable bTimes = new JTable(tableModel);

        // Adding columns to the table
        tableModel.addColumn("Level");
        tableModel.addColumn("Time");

        // Populating the table with Level Times data
        for (String key : Vehicle2.levelTimes.keySet()) {
            int startIndex = key.lastIndexOf("/") + 1;
            int endIndex = key.lastIndexOf(".");
            String shortKey = key.substring(startIndex, endIndex);
            String value = formatTime(Vehicle2.levelTimes.get(key));

            tableModel.addRow(new Object[]{shortKey, value});
        }

        // Align the content in the second column to the right
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        bTimes.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

        // Styling the table
        bTimes.getTableHeader().setBackground(Color.LIGHT_GRAY);
        bTimes.getTableHeader().setForeground(Color.BLACK);
        bTimes.setBackground(this.getBackground());
        bTimes.setShowGrid(false);
        bTimes.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));

        // Disable table selection
        bTimes.setEnabled(false);
        bTimes.setRowSelectionAllowed(false);
        bTimes.setColumnSelectionAllowed(false);

        // Set up the scroll pane
        JScrollPane scrollPane = new JScrollPane(bTimes);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Set up the button
        btnMenu = new JButton("Return to menu");
        btnMenu.addActionListener(e -> returnToMenu());

        // Center titleLabel horizontally
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        // Center scrollPane horizontally
        gbc.gridy = 1;
        add(scrollPane, gbc);

        // put btnMenu in the center
        gbc.gridy = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnMenu, gbc);
    }

    /**
     * Converts a time value from seconds into a human-readable format, e.g., "2m 30s".
     *
     * @param time The time value in seconds to be formatted.
     * @return A string representing the formatted time.
     * @throws IllegalArgumentException if the input time is negative.
     */
    public static String formatTime(String time) {
        int seconds = -1;

        try {
            seconds = Integer.parseInt(time);
        } catch (Exception e) {
            if (V2RApp.DEBUG) {
                System.out.println(e.getMessage() + "\nError parsing time: " + time);

            }
        }

        if (seconds < 0) {
            throw new IllegalArgumentException("Seconds must be non-negative");
        }

        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        if (minutes > 0) {
            return String.format("%dm %ds", minutes, remainingSeconds);
        } else {
            return String.format("%ds", remainingSeconds);
        }
    }

    /**
     * Fires a {@link TriggerEvent} to all registered {@code TriggerListeners}, signalling an intent to return to the main menu.
     * This method is invoked when the 'Return to menu' button is clicked.
     *
     * @see TriggerListener
     */
    private void returnToMenu() {
        fireEvent("exit_loop");
    }

    /**
     * Fires a {@link TriggerEvent} to all registered {@code TriggerListeners}. This method allows this
     * panel to communicate with other components of the application, triggering actions in response to events
     * such as returning to the main menu.
     *
     * <p>
     * <i>Note: This method does not guarantee that the event will be successfully processed by all listeners. Listeners
     * should be implemented in such a way that they can gracefully handle any issues that may arise during event processing.</i>
     * </p>
     *
     * @param message The message associated with the event, which can be used by listeners to determine the
     *                appropriate action to take.
     * @see TriggerListener
     */
    public void fireEvent(String message) {
        TriggerEvent event = new TriggerEvent(this, message);
        for (TriggerListener listener : listeners) {
            listener.onEventOccurred(event);
        }
    }

    /**
     * Adds a {@link TriggerListener} to the list of event listeners.
     * These listeners will be notified when certain events occur within this panel.
     *
     * @param listener The {@code TriggerListener} to be added.
     */
    public void addEventListener(TriggerListener listener) {
        listeners.add(listener);
    }
}
