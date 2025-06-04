import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Panel for comparing historical F1 data between drivers and races from 2020
 */
public class HistoricalComparisonPanel extends JPanel {
    private JComboBox<String> season1ComboBox;
    private JComboBox<String> season2ComboBox;
    private JComboBox<String> race1ComboBox;
    private JComboBox<String> race2ComboBox;
    private JComboBox<String> driver1ComboBox;
    private JComboBox<String> driver2ComboBox;
    private JPanel comparisonDisplayPanel;
    private JPanel chartPanel;
    
    // Maps to store selection data
    private Map<String, Integer> seasonMap = new HashMap<>();
    private Map<String, Integer> racesMap = new HashMap<>();
    private Map<String, Integer> driversMap = new HashMap<>();
    
    /**
     * Constructor for the historical comparison panel
     */
    public HistoricalComparisonPanel() {
        setLayout(new BorderLayout());
        
        // Initialize selection panel
        initializeSelectionPanel();
        
        // Initialize comparison display panel
        comparisonDisplayPanel = new JPanel(new BorderLayout());
        comparisonDisplayPanel.setBorder(BorderFactory.createTitledBorder("Comparison Results"));
        
        // Add a placeholder message
        JLabel placeholderLabel = new JLabel("<html><center>Select seasons, races, and drivers<br>to compare historical data</center></html>", JLabel.CENTER);
        comparisonDisplayPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        // Initialize chart panel for visual comparison
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("Performance Comparison"));
        chartPanel.add(new JLabel("<html><center>Select data to compare</center></html>", JLabel.CENTER), BorderLayout.CENTER);
        
        // Add panels to main panel
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        centerPanel.add(comparisonDisplayPanel);
        centerPanel.add(chartPanel);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Load initial data
        loadHistoricalData();
    }
    
    /**
     * Initialize the selection panel with season, race, and driver selection options
     */
    private void initializeSelectionPanel() {
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));
        selectionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Select Comparison Data"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        // Season selection
        JPanel seasonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        seasonPanel.setBorder(BorderFactory.createTitledBorder("Seasons"));
        
        season1ComboBox = new JComboBox<>();
        season2ComboBox = new JComboBox<>();
        
        seasonPanel.add(new JLabel("Season 1:"));
        seasonPanel.add(season1ComboBox);
        seasonPanel.add(new JLabel("Season 2:"));
        seasonPanel.add(season2ComboBox);
        
        // Race selection
        JPanel racePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        racePanel.setBorder(BorderFactory.createTitledBorder("Races"));
        
        race1ComboBox = new JComboBox<>();
        race2ComboBox = new JComboBox<>();
        
        racePanel.add(new JLabel("Race 1:"));
        racePanel.add(race1ComboBox);
        racePanel.add(new JLabel("Race 2:"));
        racePanel.add(race2ComboBox);
        
        // Driver selection
        JPanel driverPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        driverPanel.setBorder(BorderFactory.createTitledBorder("Drivers"));
        
        driver1ComboBox = new JComboBox<>();
        driver2ComboBox = new JComboBox<>();
        
        driverPanel.add(new JLabel("Driver 1:"));
        driverPanel.add(driver1ComboBox);
        driverPanel.add(new JLabel("Driver 2:"));
        driverPanel.add(driver2ComboBox);
        
        // Compare button
        JButton compareButton = new JButton("Compare");
        compareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performComparison();
            }
        });
        
        // Add listeners for dropdowns
        season1ComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRaceOptions(season1ComboBox, race1ComboBox);
            }
        });
        
        season2ComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRaceOptions(season2ComboBox, race2ComboBox);
            }
        });
        
        race1ComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDriverOptions(race1ComboBox, driver1ComboBox);
            }
        });
        
        race2ComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDriverOptions(race2ComboBox, driver2ComboBox);
            }
        });
        
        // Add components to selection panel
        selectionPanel.add(seasonPanel);
        selectionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        selectionPanel.add(racePanel);
        selectionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        selectionPanel.add(driverPanel);
        selectionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(compareButton);
        selectionPanel.add(buttonPanel);
        
        // Add selection panel to the west area
        add(selectionPanel, BorderLayout.WEST);
    }
    
    /**
     * Load historical data into the selection dropdowns
     */
    private void loadHistoricalData() {
        try {
            // Create database manager
            F1DatabaseManager dbManager = new F1DatabaseManager();
            
            // Get available seasons from database
            List<String> seasons = dbManager.getAvailableSeasons();
            
            if (seasons.isEmpty()) {
                // If no seasons in database, add them manually
                seasons = Arrays.asList("2020", "2021", "2022", "2023");
            }
            
            // Populate seasons dropdown
            for (String season : seasons) {
                season1ComboBox.addItem(season);
                season2ComboBox.addItem(season);
                seasonMap.put(season, Integer.parseInt(season));
            }
            
            // Set season2 default to a different season
            if (season2ComboBox.getItemCount() > 1) {
                season2ComboBox.setSelectedIndex(1);
            }
            
            // Initial update of race options
            updateRaceOptions(season1ComboBox, race1ComboBox);
            updateRaceOptions(season2ComboBox, race2ComboBox);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading historical data: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Update race options based on selected season
     */
    private void updateRaceOptions(JComboBox<String> seasonComboBox, JComboBox<String> raceComboBox) {
        String selectedSeason = (String) seasonComboBox.getSelectedItem();
        if (selectedSeason == null) return;
        
        // Clear current items
        raceComboBox.removeAllItems();
        
        // Get races for the selected season
        List<Map<String, Object>> races = getRacesForSeason(selectedSeason);
        
        // Populate race dropdown
        for (Map<String, Object> race : races) {
            String raceName = (String) race.get("name");
            Integer raceId = (Integer) race.get("id");
            racesMap.put(raceName, raceId);
            raceComboBox.addItem(raceName);
        }
        
        // Update driver options if a race is selected
        if (raceComboBox.getItemCount() > 0) {
            updateDriverOptions(raceComboBox, 
                raceComboBox == race1ComboBox ? driver1ComboBox : driver2ComboBox);
        }
    }
    
    /**
     * Update driver options based on selected race
     */
    private void updateDriverOptions(JComboBox<String> raceComboBox, JComboBox<String> driverComboBox) {
        String selectedRace = (String) raceComboBox.getSelectedItem();
        if (selectedRace == null) return;
        
        // Clear current items
        driverComboBox.removeAllItems();
        
        // Get drivers for the selected race
        List<Map<String, Object>> drivers = getDriversForRace(selectedRace);
        
        // Populate driver dropdown
        for (Map<String, Object> driver : drivers) {
            String driverName = (String) driver.get("name");
            Integer driverId = (Integer) driver.get("id");
            driversMap.put(driverName, driverId);
            driverComboBox.addItem(driverName);
        }
    }
    
    /**
     * Perform the comparison of selected data
     */
    private void performComparison() {
        String season1 = (String) season1ComboBox.getSelectedItem();
        String season2 = (String) season2ComboBox.getSelectedItem();
        String race1 = (String) race1ComboBox.getSelectedItem();
        String race2 = (String) race2ComboBox.getSelectedItem();
        String driver1 = (String) driver1ComboBox.getSelectedItem();
        String driver2 = (String) driver2ComboBox.getSelectedItem();
        
        // Check if all required selections are made
        if (season1 == null || season2 == null || race1 == null || 
            race2 == null || driver1 == null || driver2 == null) {
            JOptionPane.showMessageDialog(this, 
                "Please make all selections to perform comparison.",
                "Incomplete Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get comparison data
        Map<String, Object> driver1Data = getDriverRaceData(driver1, race1, season1);
        Map<String, Object> driver2Data = getDriverRaceData(driver2, race2, season2);
        
        // Update the comparison display
        updateComparisonDisplay(driver1, driver1Data, driver2, driver2Data);
        
        // Update the chart display
        updateComparisonChart(driver1, driver1Data, driver2, driver2Data);
    }
    
    /**
     * Update the comparison display panel with detailed metrics
     */
    private void updateComparisonDisplay(String driver1, Map<String, Object> driver1Data, 
                                        String driver2, Map<String, Object> driver2Data) {
        comparisonDisplayPanel.removeAll();
        
        // Create a table model for the comparison
        String[] columns = {"Metric", driver1, driver2, "Difference"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        // Add metrics to the table
        addComparisonRow(model, "Final Position", driver1Data, driver2Data, "position");
        addComparisonRow(model, "Points", driver1Data, driver2Data, "points");
        addComparisonRow(model, "Best Lap Time", driver1Data, driver2Data, "best_lap_time");
        addComparisonRow(model, "Average Speed (kph)", driver1Data, driver2Data, "avg_speed");
        addComparisonRow(model, "Max Speed (kph)", driver1Data, driver2Data, "max_speed");
        addComparisonRow(model, "Pit Stops", driver1Data, driver2Data, "pit_stops");
        addComparisonRow(model, "Laps Completed", driver1Data, driver2Data, "laps_completed");
        
        // Create the table
        JTable comparisonTable = new JTable(model);
        comparisonTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Add to a scroll pane
        JScrollPane scrollPane = new JScrollPane(comparisonTable);
        comparisonDisplayPanel.add(scrollPane, BorderLayout.CENTER);
        
        comparisonDisplayPanel.revalidate();
        comparisonDisplayPanel.repaint();
    }
    
    /**
     * Helper method to add a comparison row to the table model
     */
    private void addComparisonRow(DefaultTableModel model, String metricName, 
                                Map<String, Object> driver1Data, Map<String, Object> driver2Data, 
                                String metricKey) {
        Object value1 = driver1Data.getOrDefault(metricKey, "-");
        Object value2 = driver2Data.getOrDefault(metricKey, "-");
        
        // Calculate difference if both values are numbers
        String difference = "-";
        if (value1 instanceof Number && value2 instanceof Number) {
            double diff = ((Number)value1).doubleValue() - ((Number)value2).doubleValue();
            difference = String.format("%.2f", diff);
            
            // Add + sign for positive differences
            if (diff > 0) {
                difference = "+" + difference;
            }
        } else if (metricKey.equals("best_lap_time")) {
            // Special handling for lap times (format: "1:23.456")
            difference = calculateTimeDifference((String)value1, (String)value2);
        }
        
        model.addRow(new Object[]{metricName, value1, value2, difference});
    }
    
    /**
     * Calculate the difference between two lap times
     */
    private String calculateTimeDifference(String time1, String time2) {
        if (time1.equals("-") || time2.equals("-")) {
            return "-";
        }
        
        try {
            // Parse lap times (format: "1:23.456")
            double seconds1 = parseLapTime(time1);
            double seconds2 = parseLapTime(time2);
            
            double diff = seconds1 - seconds2;
            String sign = diff < 0 ? "-" : "+";
            diff = Math.abs(diff);
            
            return String.format("%s%.3f", sign, diff);
        } catch (Exception e) {
            return "Error";
        }
    }
    
    /**
     * Parse lap time string to seconds
     */
    private double parseLapTime(String timeStr) {
        String[] parts = timeStr.split(":");
        double minutes = Double.parseDouble(parts[0]);
        double seconds = Double.parseDouble(parts[1]);
        return minutes * 60 + seconds;
    }
    
    /**
     * Update the comparison chart display
     */
    private void updateComparisonChart(String driver1, Map<String, Object> driver1Data, 
                                     String driver2, Map<String, Object> driver2Data) {
        chartPanel.removeAll();
        
        JPanel chartContent = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawComparisonChart(g, driver1, driver1Data, driver2, driver2Data);
            }
        };
        
        chartPanel.add(chartContent, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }
    
    /**
     * Draw a comparison chart for the two drivers
     */
    private void drawComparisonChart(Graphics g, String driver1, Map<String, Object> driver1Data, 
                                   String driver2, Map<String, Object> driver2Data) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = g.getClipBounds().width;
        int height = g.getClipBounds().height;
        
        int padding = 50;
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;
        
        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.drawLine(padding, height - padding, width - padding, height - padding); // X-axis
        g2d.drawLine(padding, height - padding, padding, padding); // Y-axis
        
        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Lap Time Comparison", width / 2 - 90, 25);
        
        // Generate lap data arrays for both drivers
        List<Double> driver1Laps = generateLapTimeData(driver1Data);
        List<Double> driver2Laps = generateLapTimeData(driver2Data);
        
        int maxLaps = Math.max(driver1Laps.size(), driver2Laps.size());
        if (maxLaps == 0) {
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.drawString("No lap data available", width / 2 - 70, height / 2);
            return;
        }
        
        // Find min and max lap times for scaling
        double minLapTime = Double.MAX_VALUE;
        double maxLapTime = 0;
        
        for (Double lapTime : driver1Laps) {
            if (lapTime < minLapTime) minLapTime = lapTime;
            if (lapTime > maxLapTime) maxLapTime = lapTime;
        }
        
        for (Double lapTime : driver2Laps) {
            if (lapTime < minLapTime) minLapTime = lapTime;
            if (lapTime > maxLapTime) maxLapTime = lapTime;
        }
        
        // Add a small buffer
        double range = maxLapTime - minLapTime;
        minLapTime -= range * 0.05;
        maxLapTime += range * 0.05;
        
        // Draw axes labels
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Y-axis (lap times)
        int yLabelCount = 5;
        for (int i = 0; i <= yLabelCount; i++) {
            int y = height - padding - (i * chartHeight / yLabelCount);
            double lapTime = minLapTime + (i * (maxLapTime - minLapTime) / yLabelCount);
            // Format lap time as mm:ss.SSS
            int minutes = (int)(lapTime / 60);
            double seconds = lapTime % 60;
            String timeStr = String.format("%d:%05.2f", minutes, seconds);
            
            g2d.drawString(timeStr, padding - 45, y + 5);
            g2d.drawLine(padding - 5, y, padding, y); // Tick mark
        }
        
        // X-axis (lap numbers)
        int xStep = chartWidth / maxLaps;
        for (int i = 0; i < maxLaps; i++) {
            if (i % 5 == 0 || i == maxLaps - 1) { // Label every 5th lap or the last lap
                int x = padding + i * xStep + xStep / 2;
                g2d.drawString("Lap " + (i + 1), x - 15, height - padding + 15);
                g2d.drawLine(x, height - padding, x, height - padding + 5); // Tick mark
            }
        }
        
        // Draw data lines
        drawDriverLapTimes(g2d, driver1Laps, padding, height - padding, chartWidth, chartHeight, 
                         minLapTime, maxLapTime, Color.RED, driver1);
        
        drawDriverLapTimes(g2d, driver2Laps, padding, height - padding, chartWidth, chartHeight, 
                         minLapTime, maxLapTime, Color.BLUE, driver2);
        
        // Add a legend
        int legendX = width - 200;
        int legendY = 50;
        
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(Color.RED);
        g2d.fillRect(legendX, legendY, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(legendX, legendY, 15, 15);
        g2d.drawString(driver1, legendX + 20, legendY + 12);
        
        g2d.setColor(Color.BLUE);
        g2d.fillRect(legendX, legendY + 25, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(legendX, legendY + 25, 15, 15);
        g2d.drawString(driver2, legendX + 20, legendY + 37);
    }
    
    /**
     * Draw a driver's lap times on the chart
     */
    private void drawDriverLapTimes(Graphics2D g2d, List<Double> lapTimes, int x0, int y0, 
                                  int width, int height, double minTime, double maxTime, 
                                  Color color, String driverName) {
        if (lapTimes.isEmpty()) return;
        
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2f));
        
        int xStep = width / Math.max(lapTimes.size() - 1, 1);
        
        int prevX = -1;
        int prevY = -1;
        
        for (int i = 0; i < lapTimes.size(); i++) {
            double lapTime = lapTimes.get(i);
            
            // Scale the lap time to chart coordinates
            double yPercentage = (lapTime - minTime) / (maxTime - minTime);
            int x = x0 + i * xStep;
            int y = y0 - (int)(yPercentage * height);
            
            // Draw point
            g2d.fillOval(x - 4, y - 4, 8, 8);
            
            // Draw line if not first point
            if (prevX != -1) {
                g2d.drawLine(prevX, prevY, x, y);
            }
            
            prevX = x;
            prevY = y;
        }
    }
    
    /**
     * Generate lap time data for a driver
     */
    private List<Double> generateLapTimeData(Map<String, Object> driverData) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> laps = (List<Map<String, Object>>) driverData.getOrDefault("laps", new ArrayList<>());
        
        List<Double> lapTimes = new ArrayList<>();
        for (Map<String, Object> lap : laps) {
            String timeStr = (String) lap.get("time");
            if (timeStr != null) {
                try {
                    double seconds = parseLapTime(timeStr);
                    lapTimes.add(seconds);
                } catch (Exception e) {
                    // Skip invalid lap times
                }
            }
        }
        
        return lapTimes;
    }
    
    // Data retrieval methods
    
    /**
     * Get races for a specific season
     */
    private List<Map<String, Object>> getRacesForSeason(String season) {
        List<Map<String, Object>> races = new ArrayList<>();
        
        try {
            // Create database service
            F1DatabaseService dbService = new F1DatabaseService();
            
            // Get races from database
            int seasonYear = Integer.parseInt(season);
            races = dbService.getRacesForSeason(seasonYear);
            
            // If database returned no races, use hardcoded data as fallback
            if (races.isEmpty()) {
                if (season.equals("2020")) {
                    races.add(createRace(1, "2020 Austrian Grand Prix"));
                    races.add(createRace(2, "2020 Styrian Grand Prix"));
                    races.add(createRace(3, "2020 Hungarian Grand Prix"));
                    races.add(createRace(4, "2020 British Grand Prix"));
                    races.add(createRace(5, "2020 70th Anniversary Grand Prix"));
                } else if (season.equals("2021")) {
                    races.add(createRace(1, "2021 Bahrain Grand Prix"));
                    races.add(createRace(2, "2021 Emilia Romagna Grand Prix"));
                    races.add(createRace(3, "2021 Portuguese Grand Prix"));
                    races.add(createRace(4, "2021 Spanish Grand Prix"));
                    races.add(createRace(5, "2021 Monaco Grand Prix"));
                } else if (season.equals("2022")) {
                    races.add(createRace(1, "2022 Bahrain Grand Prix"));
                    races.add(createRace(2, "2022 Saudi Arabian Grand Prix"));
                    races.add(createRace(3, "2022 Australian Grand Prix"));
                    races.add(createRace(4, "2022 Emilia Romagna Grand Prix"));
                    races.add(createRace(5, "2022 Miami Grand Prix"));
                } else if (season.equals("2023")) {
                    races.add(createRace(1, "2023 Bahrain Grand Prix"));
                    races.add(createRace(2, "2023 Saudi Arabian Grand Prix"));
                    races.add(createRace(3, "2023 Australian Grand Prix"));
                    races.add(createRace(4, "2023 Azerbaijan Grand Prix"));
                    races.add(createRace(5, "2023 Miami Grand Prix"));
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting races for season: " + e.getMessage());
        }
        
        return races;
    }
    
    /**
     * Helper method to create a race map
     */
    private Map<String, Object> createRace(int id, String name) {
        Map<String, Object> race = new HashMap<>();
        race.put("id", id);
        race.put("name", name);
        return race;
    }
    
    /**
     * Get drivers for a specific race
     */
    private List<Map<String, Object>> getDriversForRace(String raceName) {
        List<Map<String, Object>> drivers = new ArrayList<>();
        
        try {
            // Extract season from race name (assuming format: "2023 Race Name")
            String seasonStr = raceName.substring(0, 4);
            int seasonYear = Integer.parseInt(seasonStr);
            
            // Create database service
            F1DatabaseService dbService = new F1DatabaseService();
            
            // Get race ID from the database
            int raceId = dbService.getRaceId(raceName, seasonYear);
            
            if (raceId > 0) {
                // Get drivers for the race from database
                drivers = dbService.getDriversForRace(raceId);
            }
            
            // If database returned no drivers, use hardcoded data as fallback
            if (drivers.isEmpty()) {
                // Core drivers that were in all seasons from 2020-2023
                drivers.add(createDriver(44, "Lewis Hamilton", "Mercedes"));
                drivers.add(createDriver(77, "Valtteri Bottas", "Mercedes"));
                drivers.add(createDriver(33, "Max Verstappen", "Red Bull Racing"));
                drivers.add(createDriver(11, "Sergio Perez", "Red Bull Racing"));
                drivers.add(createDriver(16, "Charles Leclerc", "Ferrari"));
                drivers.add(createDriver(55, "Carlos Sainz", "Ferrari"));
                drivers.add(createDriver(3, "Daniel Ricciardo", "Renault/McLaren"));
                drivers.add(createDriver(4, "Lando Norris", "McLaren"));
                
                // Add more drivers as needed based on the race
                if (raceName.startsWith("2020")) {
                    drivers.add(createDriver(23, "Alexander Albon", "Red Bull Racing"));
                    drivers.add(createDriver(10, "Pierre Gasly", "AlphaTauri"));
                } else if (raceName.startsWith("2021")) {
                    drivers.add(createDriver(14, "Fernando Alonso", "Alpine"));
                    drivers.add(createDriver(22, "Yuki Tsunoda", "AlphaTauri"));
                } else if (raceName.startsWith("2022")) {
                    drivers.add(createDriver(63, "George Russell", "Mercedes"));
                    drivers.add(createDriver(24, "Zhou Guanyu", "Alfa Romeo"));
                } else if (raceName.startsWith("2023")) {
                    drivers.add(createDriver(81, "Oscar Piastri", "McLaren"));
                    drivers.add(createDriver(27, "Nico Hulkenberg", "Haas F1 Team"));
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting drivers for race: " + e.getMessage());
        }
        
        return drivers;
    }
    
    /**
     * Helper method to create a driver map
     */
    private Map<String, Object> createDriver(int id, String name, String team) {
        Map<String, Object> driver = new HashMap<>();
        driver.put("id", id);
        driver.put("name", name);
        driver.put("team", team);
        return driver;
    }
    
    /**
     * Get race data for a specific driver
     */
    private Map<String, Object> getDriverRaceData(String driverName, String raceName, String season) {
        Map<String, Object> data = new HashMap<>();
        
        try {
            // Create database service
            F1DatabaseService dbService = new F1DatabaseService();
            
            // Extract season from race name (assuming format: "2023 Race Name")
            int seasonYear = Integer.parseInt(season);
            
            // Get race ID from the database
            int raceId = dbService.getRaceId(raceName, seasonYear);
            
            // Get driver ID from the database
            int driverId = dbService.getDriverId(driverName);
            
            // If we have valid IDs, get the data from the database
            if (raceId > 0 && driverId > 0) {
                Map<String, Object> dbData = dbService.getDriverRaceData(driverId, raceId);
                
                // If we got data from the database, use it
                if (dbData != null && !dbData.isEmpty()) {
                    return dbData;
                }
            }
            
            // If database data not available, use generated data as fallback
            // Set basic info
            data.put("driver", driverName);
            data.put("race", raceName);
            data.put("season", season);
            
            // Add team information
            if (driverName.equals("Lewis Hamilton") || driverName.equals("George Russell") || driverName.equals("Valtteri Bottas")) {
                data.put("team", "Mercedes");
            } else if (driverName.equals("Max Verstappen") || driverName.equals("Sergio Perez") || driverName.equals("Alexander Albon")) {
                data.put("team", "Red Bull Racing");
            } else if (driverName.equals("Charles Leclerc") || driverName.equals("Carlos Sainz")) {
                data.put("team", "Ferrari");
            } else if (driverName.equals("Lando Norris") || driverName.equals("Daniel Ricciardo") || driverName.equals("Oscar Piastri")) {
                data.put("team", "McLaren");
            } else {
                data.put("team", "Other");
            }
            
            // Generate semi-realistic performance data based on driver and season
            // This would be replaced with real data in a production environment
            
            // Generate position
            int position = getRandomPosition(driverName);
            data.put("position", position);
            
            // Generate points based on position
            int points = 0;
            if (position == 1) points = 25;
            else if (position == 2) points = 18;
            else if (position == 3) points = 15;
            else if (position <= 10) points = 11 - position;
            data.put("points", points);
            
            // Generate best lap time (around 1:30)
            String bestLapTime = generateLapTime(driverName, 90.0, 95.0);
            data.put("best_lap_time", bestLapTime);
            
            // Generate average speed (around 200-220 kph)
            double avgSpeed = 200 + Math.random() * 20;
            if (driverName.equals("Lewis Hamilton") || driverName.equals("Max Verstappen")) {
                avgSpeed += 5; // Top drivers slightly faster
            }
            data.put("avg_speed", Math.round(avgSpeed * 100) / 100.0);
            
            // Generate max speed (around 320-340 kph)
            double maxSpeed = 320 + Math.random() * 20;
            data.put("max_speed", Math.round(maxSpeed * 100) / 100.0);
            
            // Number of pit stops (1-3)
            int pitStops = 1 + (int)(Math.random() * 2);
            data.put("pit_stops", pitStops);
            
            // Laps completed (max 55, DNF possible)
            int maxLaps = 55;
            int lapsCompleted = position > 15 ? (int)(maxLaps * 0.8 + Math.random() * (maxLaps * 0.2)) : maxLaps;
            data.put("laps_completed", lapsCompleted);
            
            // Generate lap data
            List<Map<String, Object>> laps = generateLapData(driverName, lapsCompleted);
            data.put("laps", laps);
            
        } catch (Exception e) {
            System.err.println("Error getting driver race data: " + e.getMessage());
            
            // Fallback to basic info in case of error
            data.put("driver", driverName);
            data.put("race", raceName);
            data.put("season", season);
            data.put("position", "-");
            data.put("points", 0);
            data.put("best_lap_time", "-");
            data.put("avg_speed", 0);
            data.put("max_speed", 0);
            data.put("pit_stops", 0);
            data.put("laps_completed", 0);
            data.put("laps", new ArrayList<>());
        }
        
        return data;
    }
    
    /**
     * Generate a random position biased by driver's typical performance
     */
    private int getRandomPosition(String driverName) {
        // Top tier drivers
        if (driverName.equals("Lewis Hamilton") || 
            driverName.equals("Max Verstappen") || 
            driverName.equals("Charles Leclerc")) {
            return 1 + (int)(Math.random() * 5); // Positions 1-5
        }
        // Mid tier drivers
        else if (driverName.equals("Carlos Sainz") || 
                driverName.equals("Sergio Perez") || 
                driverName.equals("George Russell") || 
                driverName.equals("Lando Norris")) {
            return 3 + (int)(Math.random() * 7); // Positions 3-9
        }
        // Other drivers
        else {
            return 8 + (int)(Math.random() * 12); // Positions 8-19
        }
    }
    
    /**
     * Generate a lap time string (format: "1:30.123")
     */
    private String generateLapTime(String driverName, double minSeconds, double maxSeconds) {
        // Better drivers have slightly better lap times
        double driverFactor = 0.0;
        if (driverName.equals("Lewis Hamilton") || driverName.equals("Max Verstappen")) {
            driverFactor = -1.5; // Faster
        } else if (driverName.equals("Charles Leclerc") || driverName.equals("Lando Norris")) {
            driverFactor = -0.8; // Slightly faster
        }
        
        double seconds = minSeconds + Math.random() * (maxSeconds - minSeconds) + driverFactor;
        int minutes = (int)(seconds / 60);
        double remainingSeconds = seconds % 60;
        
        return String.format("%d:%05.3f", minutes, remainingSeconds);
    }
    
    /**
     * Generate lap data for a driver
     */
    private List<Map<String, Object>> generateLapData(String driverName, int lapsCompleted) {
        List<Map<String, Object>> laps = new ArrayList<>();
        
        double baseLapTime = 90.0; // Base lap time in seconds
        double variability = 2.0; // Lap time variability
        
        // Adjust base time for top drivers
        if (driverName.equals("Lewis Hamilton") || driverName.equals("Max Verstappen")) {
            baseLapTime -= 0.8;
            variability = 1.5; // More consistent
        }
        
        for (int lap = 1; lap <= lapsCompleted; lap++) {
            Map<String, Object> lapData = new HashMap<>();
            
            // Generate lap time (varies by fuel load, tire wear, etc.)
            double fuelEffect = -0.05 * lap; // Faster as fuel burns off
            double tireEffect = 0.02 * lap; // Slower as tires wear
            double randomEffect = (Math.random() - 0.5) * variability; // Random variation
            
            // Reset tire effect after pit stops (assuming pit stops around lap 15-20, 35-40)
            if (lap == 18 || lap == 38) {
                tireEffect = -0.5; // Fresh tires give an advantage
            }
            
            double adjustedLapTime = baseLapTime + fuelEffect + tireEffect + randomEffect;
            String lapTime = generateLapTime(driverName, adjustedLapTime, adjustedLapTime);
            
            lapData.put("lap", lap);
            lapData.put("time", lapTime);
            lapData.put("sector_1", generateSectorTime(27, 29));
            lapData.put("sector_2", generateSectorTime(32, 34));
            lapData.put("sector_3", generateSectorTime(29, 31));
            
            // Generate speed data
            lapData.put("avg_speed", 210 + (Math.random() * 20 - 10));
            lapData.put("max_speed", 315 + (Math.random() * 25 - 5));
            
            laps.add(lapData);
        }
        
        return laps;
    }
    
    /**
     * Generate a sector time string (format: "27.123")
     */
    private String generateSectorTime(double minSeconds, double maxSeconds) {
        double seconds = minSeconds + Math.random() * (maxSeconds - minSeconds);
        return String.format("%.3f", seconds);
    }
}