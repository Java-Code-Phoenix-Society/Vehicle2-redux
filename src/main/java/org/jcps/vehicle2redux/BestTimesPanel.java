package org.jcps.vehicle2redux;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * {@code BestTimesPanel} class represents a JPanel for displaying the best times
 * achieved in the game for different levels.
 * <p>
 * This panel includes a title, a table displaying level times, and a button
 * to return to the main menu.
 * </p>
 *
 * @author neoFuzz
 * @version 1.0
 */
public class BestTimesPanel extends JPanel {
    /**
     * Listeners registered with this instance.
     */
    final private ArrayList<TriggerListener> listeners = new ArrayList<>();

    /**
     * Title JLabel
     */
    private final JLabel titleLabel;

    /**
     * Button to exit the best times panel and return to the main menu.
     */
    private final JButton btnMenu;

    /**
     * Creates a new instance of the {@code BestTimesPanel}.
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
     * Formats the time from seconds to a human-readable format (e.g., "2m 30s").
     *
     * @param time The time in seconds.
     * @return The formatted time string.
     */
    public static String formatTime(String time) {
        int seconds = -1;

        try {
            seconds = Integer.parseInt(time);
        } catch (Exception e) {
            e.printStackTrace();
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
     * Fire event to return to the main menu.
     */
    private void returnToMenu() {
        fireEvent("exit_loop");
    }

    /**
     * Fires a {@code TriggerEvent} to all registered {@code TriggerListeners}.
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
     * Adds a {@code TriggerListener} to the list of event listeners.
     *
     * @param listener The {@code TriggerListener} to be added.
     */
    public void addEventListener(TriggerListener listener) {
        listeners.add(listener);
    }
}
