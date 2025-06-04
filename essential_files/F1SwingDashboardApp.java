import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.sql.*;
import java.time.LocalDate;

/**
 * F1SwingDashboardApp - Standalone Swing application for F1 Dashboard
 * This is a simplified, non-Maven version that runs directly with Java
 */
public class F1SwingDashboardApp extends JFrame {
    // UI Components
    private JTabbedPane tabbedPane;
    private JPanel dashboardPanel;
    private JPanel controlPanel;
    private JPanel statsPanel;
    private JPanel chartPanel;
    private JPanel dataPanel;
    private JPanel statusPanel;
    
    private JComboBox<String> raceComboBox;
    private JComboBox<String> driverComboBox;
    private JButton refreshButton;
    private JTable dataTable;
    private JScrollPane dataScrollPane;
    private JLabel statusLabel;
    
    // Data storage
    private Map<String, Integer> races;
    private Map<String, Integer> drivers;
    private List<Map<String, Object>> currentLapData;
    
    // Constants
    private static final String API_BASE_URL = "https://api.openf1.org/v1";
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;
    
    public F1SwingDashboardApp() {
        super("F1 Analytics Dashboard");
        initializeData();
        initializeUI();
        loadInitialData();
    }
    
    private void initializeData() {
        races = new HashMap<>();
        drivers = new HashMap<>();
        currentLapData = new ArrayList<>();
    }
    
    private void initializeUI() {
        // Basic frame setup
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main layout
        tabbedPane = new JTabbedPane();
        
        // Dashboard panel
        dashboardPanel = new JPanel(new BorderLayout(5, 5));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Control panel
        initializeControlPanel();
        
        // Content panel (contains stats, chart and data)
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        
        // Stats panel
        initializeStatsPanel();
        
        // Chart panel
        initializeChartPanel();
        
        // Data panel
        initializeDataPanel();
        
        // Add panels to content panel
        contentPanel.add(statsPanel);
        contentPanel.add(chartPanel);
        contentPanel.add(dataPanel);
        
        // Status bar
        initializeStatusBar();
        
        // Add components to dashboard
        dashboardPanel.add(controlPanel, BorderLayout.NORTH);
        dashboardPanel.add(contentPanel, BorderLayout.CENTER);
        dashboardPanel.add(statusPanel, BorderLayout.SOUTH);
        
        // Add dashboard to tabbed pane
        tabbedPane.addTab("Dashboard", dashboardPanel);
        
        // Add historical comparison tab
        HistoricalComparisonPanel historyPanel = new HistoricalComparisonPanel();
        tabbedPane.addTab("Historical Comparison", historyPanel);
        
        // Set content pane
        setContentPane(tabbedPane);
        
        // Create menu bar
        setJMenuBar(createMenuBar());
    }
    
    private void initializeControlPanel() {
        controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        
        JLabel raceLabel = new JLabel("Select Race:");
        raceComboBox = new JComboBox<>();
        raceComboBox.setPreferredSize(new Dimension(250, 25));
        
        JLabel driverLabel = new JLabel("Select Driver:");
        driverComboBox = new JComboBox<>();
        driverComboBox.setPreferredSize(new Dimension(250, 25));
        driverComboBox.setEnabled(false);
        
        refreshButton = new JButton("Refresh Data");
        
        controlPanel.add(raceLabel);
        controlPanel.add(raceComboBox);
        controlPanel.add(driverLabel);
        controlPanel.add(driverComboBox);
        controlPanel.add(refreshButton);
        
        // Add event listeners
        raceComboBox.addActionListener(e -> handleRaceSelection());
        driverComboBox.addActionListener(e -> handleDriverSelection());
        refreshButton.addActionListener(e -> loadInitialData());
    }
    
    private void initializeStatsPanel() {
        statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(BorderFactory.createTitledBorder("Driver Statistics"));
        
        JLabel infoLabel = new JLabel("<html><center>Select a race and driver<br>to view statistics</center></html>", JLabel.CENTER);
        statsPanel.add(infoLabel, BorderLayout.CENTER);
    }
    
    private void initializeChartPanel() {
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("Telemetry & Track Map"));
        
        // Create tabbed pane for telemetry and track map
        JTabbedPane telemetryTabs = new JTabbedPane();
        
        // Telemetry panel
        JPanel telemetryPanel = new JPanel(new BorderLayout());
        JLabel telemetryLabel = new JLabel("<html><center>Select a race and driver<br>to view telemetry</center></html>", JLabel.CENTER);
        telemetryPanel.add(telemetryLabel, BorderLayout.CENTER);
        
        // Track map panel
        TrackMapPanel trackMapPanel = new TrackMapPanel();
        
        // Add sample driver positions for testing
        trackMapPanel.updateDriverPositions(trackMapPanel.createSampleDriverPositions());
        
        // Add tool panel for track map options
        JPanel trackMapTools = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> circuitBox = new JComboBox<>(new String[]{
            "Bahrain International Circuit",
            "Jeddah Corniche Circuit",
            "Albert Park Circuit",
            "Miami International Autodrome",
            "Circuit de Monaco"
        });
        
        JCheckBox showNamesCheck = new JCheckBox("Show Names", true);
        JCheckBox showSectorsCheck = new JCheckBox("Show Sectors", true);
        JCheckBox showPitLaneCheck = new JCheckBox("Show Pit Lane", true);
        
        trackMapTools.add(new JLabel("Circuit:"));
        trackMapTools.add(circuitBox);
        trackMapTools.add(showNamesCheck);
        trackMapTools.add(showSectorsCheck);
        trackMapTools.add(showPitLaneCheck);
        
        // Add listeners for track map options
        circuitBox.addActionListener(e -> {
            int index = circuitBox.getSelectedIndex();
            trackMapPanel.setTrackData(index + 1);
        });
        
        showNamesCheck.addActionListener(e -> trackMapPanel.toggleDriverNames());
        showSectorsCheck.addActionListener(e -> trackMapPanel.toggleSectors());
        showPitLaneCheck.addActionListener(e -> trackMapPanel.togglePitLane());
        
        JPanel trackMapContainer = new JPanel(new BorderLayout());
        trackMapContainer.add(trackMapTools, BorderLayout.NORTH);
        trackMapContainer.add(trackMapPanel, BorderLayout.CENTER);
        
        // Add panels to tabbed pane
        telemetryTabs.addTab("Telemetry", telemetryPanel);
        telemetryTabs.addTab("Track Map", trackMapContainer);
        
        // Add tabbed pane to chart panel
        chartPanel.add(telemetryTabs, BorderLayout.CENTER);
    }
    
    private void initializeDataPanel() {
        dataPanel = new JPanel(new BorderLayout());
        dataPanel.setBorder(BorderFactory.createTitledBorder("Lap Data"));
        
        String[] columns = {"Lap", "Time", "Speed", "Position"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        dataTable = new JTable(model);
        dataScrollPane = new JScrollPane(dataTable);
        
        dataPanel.add(dataScrollPane, BorderLayout.CENTER);
    }
    
    private void initializeStatusBar() {
        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        JLabel apiLabel = new JLabel("API Status: Connected");
        apiLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(apiLabel, BorderLayout.EAST);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("Export Data..."));
        fileMenu.add(new JMenuItem("Print..."));
        fileMenu.addSeparator();
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.add(new JMenuItem("Refresh Data"));
        viewMenu.add(new JCheckBoxMenuItem("Auto-refresh", false));
        
        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.add(new JMenuItem("Settings..."));
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new JMenuItem("About"));
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    private void loadInitialData() {
        statusLabel.setText("Loading race data...");
        
        // Load sample data
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Load race data (sample data for now)
                    List<Map<String, Object>> raceData = loadSampleRaces();
                    
                    SwingUtilities.invokeLater(() -> {
                        populateRaces(raceData);
                        statusLabel.setText("Race data loaded");
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Error loading race data: " + e.getMessage());
                        JOptionPane.showMessageDialog(F1SwingDashboardApp.this, 
                            "Error loading race data: " + e.getMessage(),
                            "Data Loading Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };
        worker.execute();
    }
    
    private void populateRaces(List<Map<String, Object>> raceData) {
        raceComboBox.removeAllItems();
        races.clear();
        
        for (Map<String, Object> race : raceData) {
            String raceName = (String) race.get("name");
            Integer raceId = (Integer) race.get("id");
            races.put(raceName, raceId);
            raceComboBox.addItem(raceName);
        }
    }
    
    private void handleRaceSelection() {
        String selectedRace = (String) raceComboBox.getSelectedItem();
        if (selectedRace == null) return;
        
        Integer raceId = races.get(selectedRace);
        statusLabel.setText("Loading drivers for " + selectedRace + "...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Load drivers for selected race (sample data for now)
                    List<Map<String, Object>> driverData = loadSampleDrivers();
                    
                    SwingUtilities.invokeLater(() -> {
                        populateDrivers(driverData);
                        statusLabel.setText("Driver data loaded for " + selectedRace);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Error loading driver data: " + e.getMessage());
                        JOptionPane.showMessageDialog(F1SwingDashboardApp.this, 
                            "Error loading driver data: " + e.getMessage(),
                            "Data Loading Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };
        worker.execute();
    }
    
    private void populateDrivers(List<Map<String, Object>> driverData) {
        driverComboBox.removeAllItems();
        drivers.clear();
        
        for (Map<String, Object> driver : driverData) {
            String driverName = (String) driver.get("name");
            Integer driverId = (Integer) driver.get("id");
            drivers.put(driverName, driverId);
            driverComboBox.addItem(driverName);
        }
        
        driverComboBox.setEnabled(true);
    }
    
    private void handleDriverSelection() {
        String selectedDriver = (String) driverComboBox.getSelectedItem();
        if (selectedDriver == null) return;
        
        Integer driverId = drivers.get(selectedDriver);
        statusLabel.setText("Loading data for " + selectedDriver + "...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Load lap data for selected driver (sample data for now)
                    List<Map<String, Object>> lapData = loadSampleLapData();
                    Map<String, Object> driverStats = loadSampleDriverStats();
                    
                    SwingUtilities.invokeLater(() -> {
                        updateStatsPanel(driverStats, selectedDriver);
                        updateChartPanel(lapData);
                        updateDataTable(lapData);
                        statusLabel.setText("Data loaded for " + selectedDriver);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Error loading driver data: " + e.getMessage());
                        JOptionPane.showMessageDialog(F1SwingDashboardApp.this, 
                            "Error loading driver data: " + e.getMessage(),
                            "Data Loading Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };
        worker.execute();
    }
    
    private void updateStatsPanel(Map<String, Object> stats, String driverName) {
        statsPanel.removeAll();
        
        JPanel statsContent = new JPanel(new GridLayout(0, 2, 5, 5));
        statsContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add driver name
        JLabel nameLabel = new JLabel("Driver:");
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        statsContent.add(nameLabel);
        statsContent.add(new JLabel(driverName));
        
        // Add team
        JLabel teamLabel = new JLabel("Team:");
        teamLabel.setFont(teamLabel.getFont().deriveFont(Font.BOLD));
        statsContent.add(teamLabel);
        statsContent.add(new JLabel((String) stats.getOrDefault("team", "Unknown")));
        
        // Add other stats
        DecimalFormat df = new DecimalFormat("#.##");
        
        for (Map.Entry<String, Object> entry : stats.entrySet()) {
            if (entry.getKey().equals("team")) continue; // Already displayed
            
            String key = entry.getKey().replace("_", " ");
            // Capitalize first letter
            key = key.substring(0, 1).toUpperCase() + key.substring(1);
            
            JLabel keyLabel = new JLabel(key + ":");
            keyLabel.setFont(keyLabel.getFont().deriveFont(Font.BOLD));
            statsContent.add(keyLabel);
            
            Object value = entry.getValue();
            String displayValue;
            if (value instanceof Number) {
                displayValue = df.format(value);
            } else {
                displayValue = value.toString();
            }
            statsContent.add(new JLabel(displayValue));
        }
        
        statsPanel.add(statsContent, BorderLayout.CENTER);
        statsPanel.revalidate();
        statsPanel.repaint();
    }
    
    private void updateChartPanel(List<Map<String, Object>> lapData) {
        // We don't want to remove all components since we have a tabbed pane now
        // Find the telemetry tab and update it
        
        // Find telemetry tab in the chart panel
        Component[] components = chartPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JTabbedPane) {
                JTabbedPane tabs = (JTabbedPane)component;
                
                // Get the telemetry panel (first tab)
                Component telemetryTab = tabs.getComponentAt(0);
                if (telemetryTab instanceof JPanel) {
                    JPanel telemetryPanel = (JPanel)telemetryTab;
                    telemetryPanel.removeAll();
                    
                    // Create a simple chart showing lap times
                    JPanel chartContent = new JPanel() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            super.paintComponent(g);
                            drawChart(g, lapData);
                        }
                    };
                    
                    telemetryPanel.add(chartContent, BorderLayout.CENTER);
                    telemetryPanel.revalidate();
                    telemetryPanel.repaint();
                }
                
                // Update driver positions in track map panel (second tab)
                Component trackMapTab = tabs.getComponentAt(1);
                if (trackMapTab instanceof JPanel) {
                    JPanel trackMapContainer = (JPanel)trackMapTab;
                    Component[] mapComponents = trackMapContainer.getComponents();
                    for (Component mapComp : mapComponents) {
                        if (mapComp instanceof JPanel && "CENTER".equals(((BorderLayout)trackMapContainer.getLayout()).getConstraints(mapComp))) {
                            if (mapComp instanceof TrackMapPanel) {
                                TrackMapPanel trackMap = (TrackMapPanel)mapComp;
                                
                                // Create driver positions from lap data
                                List<TrackMapPanel.DriverPosition> driverPositions = new ArrayList<>();
                                String selectedDriver = (String) driverComboBox.getSelectedItem();
                                String selectedTeam = "Red Bull Racing"; // Default, would be from real data
                                
                                // Create a single driver position from our current driver
                                if (selectedDriver != null) {
                                    double progress = 0.1; // Starting position
                                    int driverNumber = drivers.getOrDefault(selectedDriver, 1);
                                    driverPositions.add(new TrackMapPanel.DriverPosition(
                                        driverNumber, 
                                        selectedDriver, 
                                        selectedTeam, 
                                        progress));
                                }
                                
                                // Update the track map
                                trackMap.updateDriverPositions(driverPositions);
                            }
                        }
                    }
                }
                
                break;
            }
        }
    }
    
    private void drawChart(Graphics g, List<Map<String, Object>> lapData) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = g.getClipBounds().width;
        int height = g.getClipBounds().height;
        
        int padding = 40;
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;
        
        // Find maximum speed value
        double maxSpeed = 0;
        for (Map<String, Object> lap : lapData) {
            double speed = (double) lap.get("speed");
            if (speed > maxSpeed) maxSpeed = speed;
        }
        maxSpeed = Math.ceil(maxSpeed / 10) * 10; // Round up to nearest 10
        
        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.drawLine(padding, height - padding, width - padding, height - padding); // X-axis
        g2d.drawLine(padding, height - padding, padding, padding); // Y-axis
        
        // Draw labels
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        
        // Y-axis labels (speed)
        for (int i = 0; i <= 10; i++) {
            int y = height - padding - (i * chartHeight / 10);
            int speed = (int) (i * maxSpeed / 10);
            g2d.drawString(String.valueOf(speed), padding - 30, y + 5);
            g2d.drawLine(padding - 5, y, padding, y); // Tick mark
        }
        
        // X-axis labels (laps)
        int xStep = chartWidth / lapData.size();
        for (int i = 0; i < lapData.size(); i++) {
            int x = padding + i * xStep + xStep / 2;
            g2d.drawString("Lap " + (i + 1), x - 15, height - padding + 15);
        }
        
        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Speed per Lap", width / 2 - 50, 20);
        
        // Draw data points and line
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2f));
        
        int prevX = -1;
        int prevY = -1;
        
        for (int i = 0; i < lapData.size(); i++) {
            Map<String, Object> lap = lapData.get(i);
            double speed = (double) lap.get("speed");
            
            int x = padding + i * xStep + xStep / 2;
            int y = height - padding - (int) (speed / maxSpeed * chartHeight);
            
            // Draw point
            g2d.fillOval(x - 4, y - 4, 8, 8);
            
            // Connect with line if not first point
            if (prevX != -1) {
                g2d.drawLine(prevX, prevY, x, y);
            }
            
            prevX = x;
            prevY = y;
        }
    }
    
    private void updateDataTable(List<Map<String, Object>> lapData) {
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        model.setRowCount(0); // Clear existing data
        
        for (Map<String, Object> lap : lapData) {
            model.addRow(new Object[] {
                lap.get("lap"),
                lap.get("time"),
                lap.get("speed"),
                lap.get("position")
            });
        }
    }
    
    // Sample data methods for testing
    private List<Map<String, Object>> loadSampleRaces() {
        List<Map<String, Object>> races = new ArrayList<>();
        
        // 2023 Season races
        Map<String, Object> race1 = new HashMap<>();
        race1.put("id", 1);
        race1.put("name", "2023 Bahrain Grand Prix");
        races.add(race1);
        
        Map<String, Object> race2 = new HashMap<>();
        race2.put("id", 2);
        race2.put("name", "2023 Saudi Arabian Grand Prix");
        races.add(race2);
        
        Map<String, Object> race3 = new HashMap<>();
        race3.put("id", 3);
        race3.put("name", "2023 Australian Grand Prix");
        races.add(race3);
        
        Map<String, Object> race4 = new HashMap<>();
        race4.put("id", 4);
        race4.put("name", "2023 Azerbaijan Grand Prix");
        races.add(race4);
        
        Map<String, Object> race5 = new HashMap<>();
        race5.put("id", 5);
        race5.put("name", "2023 Miami Grand Prix");
        races.add(race5);
        
        return races;
    }
    
    private List<Map<String, Object>> loadSampleDrivers() {
        List<Map<String, Object>> drivers = new ArrayList<>();
        
        Map<String, Object> driver1 = new HashMap<>();
        driver1.put("id", 1);
        driver1.put("name", "Max Verstappen");
        driver1.put("team", "Red Bull Racing");
        drivers.add(driver1);
        
        Map<String, Object> driver2 = new HashMap<>();
        driver2.put("id", 11);
        driver2.put("name", "Sergio Perez");
        driver2.put("team", "Red Bull Racing");
        drivers.add(driver2);
        
        Map<String, Object> driver3 = new HashMap<>();
        driver3.put("id", 44);
        driver3.put("name", "Lewis Hamilton");
        driver3.put("team", "Mercedes");
        drivers.add(driver3);
        
        Map<String, Object> driver4 = new HashMap<>();
        driver4.put("id", 63);
        driver4.put("name", "George Russell");
        driver4.put("team", "Mercedes");
        drivers.add(driver4);
        
        Map<String, Object> driver5 = new HashMap<>();
        driver5.put("id", 16);
        driver5.put("name", "Charles Leclerc");
        driver5.put("team", "Ferrari");
        drivers.add(driver5);
        
        Map<String, Object> driver6 = new HashMap<>();
        driver6.put("id", 55);
        driver6.put("name", "Carlos Sainz");
        driver6.put("team", "Ferrari");
        drivers.add(driver6);
        
        return drivers;
    }
    
    private List<Map<String, Object>> loadSampleLapData() {
        List<Map<String, Object>> laps = new ArrayList<>();
        
        // Generate sample lap data
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> lap = new HashMap<>();
            lap.put("lap", i);
            lap.put("time", "1:" + (30 + i % 5) + "." + (100 + i * 10) % 1000);
            lap.put("speed", 290.0 + (Math.random() * 30 - 15)); // Random speed between 275-305
            lap.put("position", (i <= 3) ? 1 : (i % 3) + 1);  // Maintain position or change slightly
            laps.add(lap);
        }
        
        return laps;
    }
    
    private Map<String, Object> loadSampleDriverStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("team", "Red Bull Racing");
        stats.put("position", 1);
        stats.put("points", 416);
        stats.put("wins", 14);
        stats.put("podiums", 19);
        stats.put("fastest laps", 8);
        stats.put("avg speed", 213.6);
        stats.put("max speed", 337.5);
        
        return stats;
    }
    
    // Main method
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Initialize database
        try {
            System.out.println("Initializing database...");
            F1DatabaseManager dbManager = new F1DatabaseManager();
            if (dbManager.testConnection()) {
                System.out.println("Database connection successful!");
                dbManager.initializeDatabase();
            } else {
                System.err.println("Failed to connect to the database!");
                JOptionPane.showMessageDialog(null, 
                    "Failed to connect to the database. The application will use sample data instead.",
                    "Database Connection Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Launch application
        SwingUtilities.invokeLater(() -> {
            F1SwingDashboardApp app = new F1SwingDashboardApp();
            app.setVisible(true);
        });
    }
}