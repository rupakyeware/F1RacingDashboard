import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * F1SwingDashboard - A Swing-based Formula 1 Analytics Dashboard
 * Displays race data, driver statistics, and telemetry information
 */
public class F1SwingDashboard extends JFrame {
    // UI Components
    private JPanel mainPanel;
    private JPanel controlPanel;
    private JPanel statsPanel;
    private JPanel chartPanel;
    private JPanel dataPanel;
    private JPanel timelinePanel;
    
    private JComboBox<String> raceComboBox;
    private JComboBox<String> driverComboBox;
    private JButton refreshButton;
    private JLabel statusLabel;
    private JSlider timelineSlider;
    private JLabel currentLapLabel;
    private JTable dataTable;
    private JScrollPane tableScrollPane;
    
    // Data Storage
    private Map<String, String> raceMap;
    private Map<String, String> driverMap;
    private List<Map<String, Object>> currentLapData;
    
    // Constants
    private static final String BASE_API_URL = "https://api.openf1.org/v1";
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;
    
    /**
     * Constructor - initialize the dashboard
     */
    public F1SwingDashboard() {
        // Initialize data structures
        raceMap = new HashMap<>();
        driverMap = new HashMap<>();
        currentLapData = new ArrayList<>();
        
        // Set up the main frame
        setTitle("F1 Analytics Dashboard");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialize UI components
        initializeUI();
        
        // Load initial data
        loadRaceData();
        
        // Make the frame visible
        setVisible(true);
    }
    
    /**
     * Set up the UI components and layout
     */
    private void initializeUI() {
        // Main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Control panel at the top
        initializeControlPanel();
        
        // Center panel with charts and data
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        
        // Stats and chart panel on the left
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        initializeStatsPanel();
        initializeChartPanel();
        leftPanel.add(statsPanel);
        leftPanel.add(chartPanel);
        
        // Data panel on the right
        initializeDataPanel();
        
        centerPanel.add(leftPanel);
        centerPanel.add(dataPanel);
        
        // Timeline slider at the bottom
        initializeTimelinePanel();
        
        // Add components to the main panel
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(timelinePanel, BorderLayout.SOUTH);
        
        // Status bar at the bottom
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        
        // Add the main panel to the frame
        setContentPane(mainPanel);
    }
    
    /**
     * Initialize the control panel with race and driver selection
     */
    private void initializeControlPanel() {
        controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        
        // Race selection
        JLabel raceLabel = new JLabel("Select Race:");
        raceComboBox = new JComboBox<>();
        raceComboBox.setPreferredSize(new Dimension(200, 25));
        
        // Driver selection
        JLabel driverLabel = new JLabel("Select Driver:");
        driverComboBox = new JComboBox<>();
        driverComboBox.setPreferredSize(new Dimension(200, 25));
        driverComboBox.setEnabled(false); // Disabled until race is selected
        
        // Refresh button
        refreshButton = new JButton("Refresh Data");
        
        // Add components to the control panel
        controlPanel.add(raceLabel);
        controlPanel.add(raceComboBox);
        controlPanel.add(driverLabel);
        controlPanel.add(driverComboBox);
        controlPanel.add(refreshButton);
        
        // Add event listeners
        raceComboBox.addActionListener(e -> handleRaceSelection());
        driverComboBox.addActionListener(e -> handleDriverSelection());
        refreshButton.addActionListener(e -> loadRaceData());
    }
    
    /**
     * Initialize the stats panel for displaying driver statistics
     */
    private void initializeStatsPanel() {
        statsPanel = new JPanel(new BorderLayout(10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Driver Statistics"));
        
        // Default content
        JLabel placeholderLabel = new JLabel("Select a race and driver to view statistics", SwingConstants.CENTER);
        statsPanel.add(placeholderLabel, BorderLayout.CENTER);
    }
    
    /**
     * Initialize the chart panel for telemetry visualization
     */
    private void initializeChartPanel() {
        chartPanel = new JPanel(new BorderLayout(10, 10));
        chartPanel.setBorder(BorderFactory.createTitledBorder("Telemetry"));
        
        // Default content
        JLabel placeholderLabel = new JLabel("Select a race and driver to view telemetry data", SwingConstants.CENTER);
        chartPanel.add(placeholderLabel, BorderLayout.CENTER);
    }
    
    /**
     * Initialize the data panel with the lap data table
     */
    private void initializeDataPanel() {
        dataPanel = new JPanel(new BorderLayout(10, 10));
        dataPanel.setBorder(BorderFactory.createTitledBorder("Lap Data"));
        
        // Create table with empty model
        dataTable = new JTable(new DefaultTableModel(
            new Object[][] {},
            new String[] {"Lap", "Time", "Speed (km/h)", "Position", "Compound"}
        ));
        tableScrollPane = new JScrollPane(dataTable);
        
        dataPanel.add(tableScrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Initialize the timeline panel with slider
     */
    private void initializeTimelinePanel() {
        timelinePanel = new JPanel(new BorderLayout(10, 10));
        timelinePanel.setBorder(BorderFactory.createTitledBorder("Race Timeline"));
        
        // Timeline slider
        timelineSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        timelineSlider.setMajorTickSpacing(10);
        timelineSlider.setPaintTicks(true);
        timelineSlider.setPaintLabels(true);
        timelineSlider.setEnabled(false);
        
        // Current lap label
        currentLapLabel = new JLabel("Lap: 0", SwingConstants.CENTER);
        
        // Add components to the timeline panel
        timelinePanel.add(currentLapLabel, BorderLayout.NORTH);
        timelinePanel.add(timelineSlider, BorderLayout.CENTER);
        
        // Add event listener
        timelineSlider.addChangeListener(e -> updateTimelinePosition());
    }
    
    /**
     * Load race data from the F1 API
     */
    private void loadRaceData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    statusLabel.setText("Loading race data...");
                    
                    // Sample race data (to be replaced with actual API call)
                    List<Map<String, Object>> races = getSampleRaces();
                    
                    // Update UI on EDT
                    SwingUtilities.invokeLater(() -> {
                        raceComboBox.removeAllItems();
                        raceMap.clear();
                        
                        for (Map<String, Object> race : races) {
                            String raceName = (String) race.get("meeting_name");
                            String raceKey = race.get("meeting_key").toString();
                            raceMap.put(raceName, raceKey);
                            raceComboBox.addItem(raceName);
                        }
                        
                        statusLabel.setText("Race data loaded successfully");
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Error loading race data: " + ex.getMessage());
                        JOptionPane.showMessageDialog(F1SwingDashboard.this,
                            "Error loading race data: " + ex.getMessage(), 
                            "Data Loading Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };
        worker.execute();
    }
    
    /**
     * Handle race selection event
     */
    private void handleRaceSelection() {
        String selectedRace = (String) raceComboBox.getSelectedItem();
        if (selectedRace == null) return;
        
        String raceKey = raceMap.get(selectedRace);
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    statusLabel.setText("Loading drivers for " + selectedRace + "...");
                    
                    // Sample driver data (to be replaced with actual API call)
                    List<Map<String, Object>> drivers = getSampleDrivers();
                    
                    // Update UI on EDT
                    SwingUtilities.invokeLater(() -> {
                        driverComboBox.removeAllItems();
                        driverMap.clear();
                        
                        for (Map<String, Object> driver : drivers) {
                            String driverName = (String) driver.get("full_name");
                            String driverNumber = driver.get("driver_number").toString();
                            driverMap.put(driverName, driverNumber);
                            driverComboBox.addItem(driverName);
                        }
                        
                        driverComboBox.setEnabled(true);
                        statusLabel.setText("Driver data loaded for " + selectedRace);
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Error loading driver data: " + ex.getMessage());
                        JOptionPane.showMessageDialog(F1SwingDashboard.this,
                            "Error loading driver data: " + ex.getMessage(), 
                            "Data Loading Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };
        worker.execute();
    }
    
    /**
     * Handle driver selection event
     */
    private void handleDriverSelection() {
        String selectedRace = (String) raceComboBox.getSelectedItem();
        String selectedDriver = (String) driverComboBox.getSelectedItem();
        if (selectedRace == null || selectedDriver == null) return;
        
        String raceKey = raceMap.get(selectedRace);
        String driverNumber = driverMap.get(selectedDriver);
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    statusLabel.setText("Loading data for " + selectedDriver + "...");
                    
                    // Sample lap data (to be replaced with actual API call)
                    List<Map<String, Object>> lapData = getSampleLapData();
                    Map<String, Object> driverStats = getSampleDriverStats();
                    
                    // Store lap data for use with the timeline
                    currentLapData = lapData;
                    
                    // Update UI on EDT
                    SwingUtilities.invokeLater(() -> {
                        // Update statistics panel
                        updateStatsPanel(driverStats, selectedDriver);
                        
                        // Update chart panel
                        updateChartPanel(lapData);
                        
                        // Update data table
                        updateDataTable(lapData);
                        
                        // Update timeline
                        int totalLaps = lapData.size();
                        timelineSlider.setMaximum(totalLaps);
                        timelineSlider.setValue(1);
                        timelineSlider.setEnabled(true);
                        currentLapLabel.setText("Lap: 1");
                        
                        statusLabel.setText("Data loaded for " + selectedDriver);
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Error loading driver data: " + ex.getMessage());
                        JOptionPane.showMessageDialog(F1SwingDashboard.this,
                            "Error loading driver data: " + ex.getMessage(), 
                            "Data Loading Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };
        worker.execute();
    }
    
    /**
     * Update the timeline position based on slider value
     */
    private void updateTimelinePosition() {
        int lapNumber = timelineSlider.getValue();
        currentLapLabel.setText("Lap: " + lapNumber);
        
        // Highlight the current lap in the table
        if (dataTable.getRowCount() >= lapNumber) {
            dataTable.setRowSelectionInterval(lapNumber - 1, lapNumber - 1);
            dataTable.scrollRectToVisible(dataTable.getCellRect(lapNumber - 1, 0, true));
        }
        
        // Update chart to show data for the selected lap
        // This would be implementation-specific based on the charting library
    }
    
    /**
     * Update the statistics panel with driver data
     */
    private void updateStatsPanel(Map<String, Object> stats, String driverName) {
        // Clear the panel
        statsPanel.removeAll();
        
        // Create a panel for the stats
        JPanel statsDataPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        statsDataPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add driver name and team
        JLabel nameLabel = new JLabel("Driver:", SwingConstants.RIGHT);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        statsDataPanel.add(nameLabel);
        statsDataPanel.add(new JLabel(driverName));
        
        JLabel teamLabel = new JLabel("Team:", SwingConstants.RIGHT);
        teamLabel.setFont(teamLabel.getFont().deriveFont(Font.BOLD));
        statsDataPanel.add(teamLabel);
        statsDataPanel.add(new JLabel((String) stats.getOrDefault("team", "Unknown")));
        
        // Format for decimal values
        DecimalFormat df = new DecimalFormat("#.##");
        
        // Add stats to panel
        for (Map.Entry<String, Object> entry : stats.entrySet()) {
            if (entry.getKey().equals("team")) continue; // Already displayed above
            
            String key = entry.getKey().replace("_", " ");
            key = key.substring(0, 1).toUpperCase() + key.substring(1);
            
            JLabel keyLabel = new JLabel(key + ":", SwingConstants.RIGHT);
            keyLabel.setFont(keyLabel.getFont().deriveFont(Font.BOLD));
            statsDataPanel.add(keyLabel);
            
            Object value = entry.getValue();
            String displayValue = (value instanceof Number) ? 
                df.format(value) : value.toString();
            statsDataPanel.add(new JLabel(displayValue));
        }
        
        statsPanel.add(statsDataPanel, BorderLayout.CENTER);
        statsPanel.revalidate();
        statsPanel.repaint();
    }
    
    /**
     * Update the chart panel with lap data
     */
    private void updateChartPanel(List<Map<String, Object>> lapData) {
        // Clear the panel
        chartPanel.removeAll();
        
        // Create a simple bar chart (placeholder)
        // In a real application, you would use a charting library like JFreeChart
        
        int width = chartPanel.getWidth();
        int height = chartPanel.getHeight();
        
        JPanel chartArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Setup antialiasing for smoother lines
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int padding = 30;
                int labelPadding = 10;
                int chartWidth = getWidth() - (2 * padding);
                int chartHeight = getHeight() - (2 * padding);
                
                // Find max Y value
                double maxSpeed = 0;
                for (Map<String, Object> lap : lapData) {
                    double speed = Double.parseDouble(lap.get("speed").toString());
                    if (speed > maxSpeed) maxSpeed = speed;
                }
                
                // Round up to nearest 50
                maxSpeed = Math.ceil(maxSpeed / 50) * 50;
                
                // Draw axes
                g2d.setColor(Color.BLACK);
                g2d.drawLine(padding, getHeight() - padding, padding, padding);
                g2d.drawLine(padding, getHeight() - padding, getWidth() - padding, getHeight() - padding);
                
                // Draw Y axis labels (speed)
                for (int i = 0; i <= 5; i++) {
                    int y = getHeight() - padding - (i * chartHeight / 5);
                    int speed = (int)(i * maxSpeed / 5);
                    g2d.drawString(String.valueOf(speed), padding - 25, y + 5);
                    g2d.drawLine(padding - 5, y, padding, y);
                }
                
                // Draw X axis labels (laps)
                int dataPoints = lapData.size();
                int xStep = chartWidth / (dataPoints - 1);
                for (int i = 0; i < dataPoints; i++) {
                    int x = padding + (i * xStep);
                    Map<String, Object> lap = lapData.get(i);
                    String lapNum = lap.get("lap_number").toString();
                    g2d.drawString(lapNum, x - 5, getHeight() - padding + 20);
                    g2d.drawLine(x, getHeight() - padding, x, getHeight() - padding + 5);
                }
                
                // Draw title
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.drawString("Speed per Lap (km/h)", getWidth() / 2 - 70, 15);
                
                // Draw data points and connect with lines
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(2f));
                
                int prevX = -1, prevY = -1;
                for (int i = 0; i < dataPoints; i++) {
                    Map<String, Object> lap = lapData.get(i);
                    double speed = Double.parseDouble(lap.get("speed").toString());
                    int x = padding + (i * xStep);
                    int y = getHeight() - padding - (int)((speed / maxSpeed) * chartHeight);
                    
                    // Draw point
                    g2d.fillOval(x - 3, y - 3, 6, 6);
                    
                    // Connect with line if not first point
                    if (prevX != -1) {
                        g2d.drawLine(prevX, prevY, x, y);
                    }
                    
                    prevX = x;
                    prevY = y;
                }
            }
        };
        
        chartArea.setPreferredSize(new Dimension(width, height));
        chartPanel.add(chartArea, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }
    
    /**
     * Update the data table with lap data
     */
    private void updateDataTable(List<Map<String, Object>> lapData) {
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        model.setRowCount(0); // Clear existing data
        
        for (Map<String, Object> lap : lapData) {
            Object[] row = new Object[5];
            row[0] = lap.get("lap_number");
            row[1] = lap.get("lap_time");
            row[2] = lap.get("speed");
            row[3] = lap.get("position");
            row[4] = lap.get("compound");
            model.addRow(row);
        }
    }
    
    /**
     * Sample race data for testing
     */
    private List<Map<String, Object>> getSampleRaces() {
        List<Map<String, Object>> races = new ArrayList<>();
        
        // 2023 Races
        Map<String, Object> race1 = new HashMap<>();
        race1.put("meeting_key", "1");
        race1.put("meeting_name", "2023 Bahrain Grand Prix");
        races.add(race1);
        
        Map<String, Object> race2 = new HashMap<>();
        race2.put("meeting_key", "2");
        race2.put("meeting_name", "2023 Saudi Arabian Grand Prix");
        races.add(race2);
        
        Map<String, Object> race3 = new HashMap<>();
        race3.put("meeting_key", "3");
        race3.put("meeting_name", "2023 Australian Grand Prix");
        races.add(race3);
        
        Map<String, Object> race4 = new HashMap<>();
        race4.put("meeting_key", "4");
        race4.put("meeting_name", "2023 Miami Grand Prix");
        races.add(race4);
        
        Map<String, Object> race5 = new HashMap<>();
        race5.put("meeting_key", "5");
        race5.put("meeting_name", "2023 Monaco Grand Prix");
        races.add(race5);
        
        return races;
    }
    
    /**
     * Sample driver data for testing
     */
    private List<Map<String, Object>> getSampleDrivers() {
        List<Map<String, Object>> drivers = new ArrayList<>();
        
        Map<String, Object> driver1 = new HashMap<>();
        driver1.put("driver_number", "1");
        driver1.put("full_name", "Max Verstappen");
        driver1.put("team", "Red Bull Racing");
        drivers.add(driver1);
        
        Map<String, Object> driver2 = new HashMap<>();
        driver2.put("driver_number", "11");
        driver2.put("full_name", "Sergio Perez");
        driver2.put("team", "Red Bull Racing");
        drivers.add(driver2);
        
        Map<String, Object> driver3 = new HashMap<>();
        driver3.put("driver_number", "44");
        driver3.put("full_name", "Lewis Hamilton");
        driver3.put("team", "Mercedes");
        drivers.add(driver3);
        
        Map<String, Object> driver4 = new HashMap<>();
        driver4.put("driver_number", "63");
        driver4.put("full_name", "George Russell");
        driver4.put("team", "Mercedes");
        drivers.add(driver4);
        
        Map<String, Object> driver5 = new HashMap<>();
        driver5.put("driver_number", "16");
        driver5.put("full_name", "Charles Leclerc");
        driver5.put("team", "Ferrari");
        drivers.add(driver5);
        
        return drivers;
    }
    
    /**
     * Sample lap data for testing
     */
    private List<Map<String, Object>> getSampleLapData() {
        List<Map<String, Object>> laps = new ArrayList<>();
        
        // Sample tire compounds
        String[] compounds = {"Soft", "Medium", "Hard"};
        
        // Generate random lap data
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> lap = new HashMap<>();
            lap.put("lap_number", i);
            lap.put("lap_time", "1:" + (34 + i % 3) + "." + (100 + (i * 111) % 900));
            lap.put("speed", 200 + (Math.random() * 30));
            lap.put("position", 1 + (i % 5));
            lap.put("compound", compounds[i % 3]);
            laps.add(lap);
        }
        
        return laps;
    }
    
    /**
     * Sample driver statistics for testing
     */
    private Map<String, Object> getSampleDriverStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("team", "Red Bull Racing");
        stats.put("position", 1);
        stats.put("points", 350);
        stats.put("wins", 15);
        stats.put("podiums", 20);
        stats.put("avg_speed", 218.5);
        stats.put("max_speed", 340.2);
        stats.put("fastest_lap", "1:33.456");
        
        return stats;
    }
    
    /**
     * Fetch data from the OpenF1 API
     */
    private String fetchFromAPI(String endpoint) throws IOException {
        URL url = new URL(BASE_API_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        return response.toString();
    }
    
    /**
     * Main method to launch the application
     */
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch the application on the EDT
        SwingUtilities.invokeLater(() -> {
            new F1SwingDashboard();
        });
    }
}