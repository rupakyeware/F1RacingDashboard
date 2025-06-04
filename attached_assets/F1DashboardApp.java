package com.f1.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class F1DashboardApp extends JFrame {
    private JTable dataTable;
    private JScrollPane scrollPane;
    private JComboBox<String> raceComboBox;
    private JComboBox<String> driverComboBox;
    private JSlider timelineSlider;
    private JLabel statsLabel;
    private Map<String, Integer> raceMap = new HashMap<>();
    private Map<String, Integer> driverMap = new HashMap<>();

    public F1DashboardApp() {
        initializeUI();
        loadLastFiveRaces();
    }

    private void initializeUI() {
        setTitle("F1 Analytics Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Control Panel
        JPanel controlPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        raceComboBox = new JComboBox<>();
        driverComboBox = new JComboBox<>();
        JButton refreshButton = new JButton("Refresh Data");

        controlPanel.add(new JLabel("Select Race:"));
        controlPanel.add(raceComboBox);
        controlPanel.add(new JLabel("Select Driver:"));
        controlPanel.add(driverComboBox);
        controlPanel.add(refreshButton);

        // Statistics Display
        statsLabel = new JLabel("Select a race and driver to begin...");
        statsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Timeline Slider
        JPanel sliderPanel = new JPanel(new BorderLayout());
        timelineSlider = new JSlider(0, 100, 0);
        timelineSlider.setMajorTickSpacing(10);
        timelineSlider.setMinorTickSpacing(1);
        timelineSlider.setPaintTicks(true);
        timelineSlider.setPaintLabels(true);
        sliderPanel.add(new JLabel("Race Timeline:"), BorderLayout.NORTH);
        sliderPanel.add(timelineSlider, BorderLayout.CENTER);

        // Data Table
        dataTable = new JTable();
        scrollPane = new JScrollPane(dataTable);

        // Main layout
        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(sliderPanel, BorderLayout.SOUTH);
        add(statsLabel, BorderLayout.WEST);

        // Event Listeners
        raceComboBox.addActionListener(this::onRaceSelected);
        driverComboBox.addActionListener(this::onDriverSelected);
        refreshButton.addActionListener(this::refreshData);
        timelineSlider.addChangeListener(e -> updateTimelineData());
    }

    private void loadLastFiveRaces() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                String jsonResponse = fetchFromBackend("/api/meetings/last-five");
                List<Map<String, Object>> meetings = parseRaceData(jsonResponse);

                SwingUtilities.invokeLater(() -> {
                    raceComboBox.removeAllItems();
                    for (Map<String, Object> meeting : meetings) {
                        String name = (String) meeting.get("meeting_name");
                        int key = (int) meeting.get("meeting_key");
                        raceMap.put(name, key);
                        raceComboBox.addItem(name);
                    }
                });
                return null;
            }
        };
        worker.execute();
    }

    private void onRaceSelected(ActionEvent e) {
        String selectedRace = (String) raceComboBox.getSelectedItem();
        int meetingKey = raceMap.get(selectedRace);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                String jsonResponse = fetchFromBackend("/api/drivers?meeting_key=" + meetingKey);
                List<Map<String, Object>> drivers = parseDriverData(jsonResponse);

                SwingUtilities.invokeLater(() -> {
                    driverComboBox.removeAllItems();
                    for (Map<String, Object> driver : drivers) {
                        String name = (String) driver.get("full_name");
                        int number = (int) driver.get("driver_number");
                        driverMap.put(name, number);
                        driverComboBox.addItem(name);
                    }
                });
                return null;
            }
        };
        worker.execute();
    }

    private void onDriverSelected(ActionEvent e) {
        String selectedDriver = (String) driverComboBox.getSelectedItem();
        int driverNumber = driverMap.get(selectedDriver);
        int meetingKey = raceMap.get((String) raceComboBox.getSelectedItem());

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Fetch and display statistics
                String statsUrl = String.format("/api/driver-stats?meeting_key=%d&driver_number=%d", meetingKey, driverNumber);
                String statsData = fetchFromBackend(statsUrl);
                updateStatsDisplay(statsData);

                // Fetch and initialize timeline
                String timelineUrl = String.format("/api/timeline?meeting_key=%d&driver_number=%d", meetingKey, driverNumber);
                String timelineData = fetchFromBackend(timelineUrl);
                initializeTimeline(timelineData);
                return null;
            }
        };
        worker.execute();
    }

    private void updateTimelineData() {
        int selectedLap = timelineSlider.getValue();
        // Fetch and display data for the selected lap
        // Example: Fetch lap data from the backend and update the table
        String lapData = fetchFromBackend("/api/lap-data?lap=" + selectedLap);
        updateTableWithLapData(lapData);
    }

    private String fetchFromBackend(String endpoint) {
        try {
            URL url = new URL("http://localhost:8080" + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return "[]";
        }
    }

    private List<Map<String, Object>> parseRaceData(String jsonResponse) {
        // Implement JSON parsing logic here (e.g., using org.json or Gson)
        // For now, return an empty list
        return new ArrayList<>();
    }

    private List<Map<String, Object>> parseDriverData(String jsonResponse) {
        // Implement JSON parsing logic here (e.g., using org.json or Gson)
        // For now, return an empty list
        return new ArrayList<>();
    }

    private void updateStatsDisplay(String statsData) {
        // Implement logic to update the stats label
        statsLabel.setText("Stats: " + statsData);
    }

    private void initializeTimeline(String timelineData) {
        // Implement logic to initialize the timeline slider
        // Example: Parse timeline data and set slider range
    }

    private void updateTableWithLapData(String lapData) {
        // Implement logic to update the table with lap data
        // Example: Parse lap data and update the table model
    }

    private void refreshData(ActionEvent e) {
        loadLastFiveRaces();
        driverComboBox.removeAllItems();
        dataTable.setModel(new DefaultTableModel());
        statsLabel.setText("Data refreshed - please make new selections");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            F1DashboardApp app = new F1DashboardApp();
            app.setVisible(true);
        });
    }
}