import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * F1DashboardRunner - Main class to launch the F1 Analytics Dashboard
 * This class initializes the necessary components and launches the dashboard application
 */
public class F1DashboardRunner {
    
    // Configuration settings
    private static final String APP_TITLE = "F1 Analytics Dashboard";
    private static final Dimension WINDOW_SIZE = new Dimension(1280, 800);
    
    // Environment variables and settings
    private static Map<String, String> appSettings = new HashMap<>();
    
    /**
     * Main method - application entry point
     */
    public static void main(String[] args) {
        // Configure the application
        configureApplication();
        
        // Launch the dashboard on the EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            // Create and show the dashboard
            createAndShowDashboard();
        });
    }
    
    /**
     * Configure application settings before launch
     */
    private static void configureApplication() {
        // Set up system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set system look and feel: " + e.getMessage());
            // Continue with default look and feel
        }
        
        // Load environment variables and settings
        appSettings.put("api_base_url", "https://api.openf1.org/v1");
        appSettings.put("cache_enabled", "true");
        appSettings.put("cache_duration", "3600"); // 1 hour in seconds
        
        // Load any custom settings from arguments or config file
        loadCustomSettings();
        
        // Initialize logging
        initializeLogging();
    }
    
    /**
     * Create and show the dashboard GUI
     */
    private static void createAndShowDashboard() {
        // Create the main application frame
        JFrame frame = new JFrame(APP_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_SIZE);
        frame.setLocationRelativeTo(null); // Center on screen
        
        // Create a tabbed interface for different dashboard views
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add the main dashboard panel
        tabbedPane.addTab("Dashboard", createIcon("dashboard"), new F1SwingDashboard(), "Main F1 Dashboard");
        
        // Add additional views (commented out for now, can be implemented later)
        // tabbedPane.addTab("Telemetry", createIcon("telemetry"), createTelemetryPanel(), "Detailed Telemetry Analysis");
        // tabbedPane.addTab("Comparison", createIcon("compare"), createComparisonPanel(), "Driver Comparison");
        // tabbedPane.addTab("Historical", createIcon("history"), createHistoricalPanel(), "Historical Data Analysis");
        
        // Add the tabbed pane to the frame
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        // Create a status bar
        JPanel statusBar = createStatusBar();
        frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
        
        // Add a menu bar
        JMenuBar menuBar = createMenuBar();
        frame.setJMenuBar(menuBar);
        
        // Display the frame
        frame.setVisible(true);
        
        // Log application start
        System.out.println("F1 Dashboard application started successfully");
    }
    
    /**
     * Create a menu bar for the application
     */
    private static JMenuBar createMenuBar() {
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
        viewMenu.add(new JCheckBoxMenuItem("Auto Update", true));
        viewMenu.addSeparator();
        JMenu themeMenu = new JMenu("Theme");
        themeMenu.add(new JRadioButtonMenuItem("Light", true));
        themeMenu.add(new JRadioButtonMenuItem("Dark"));
        viewMenu.add(themeMenu);
        
        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.add(new JMenuItem("Preferences..."));
        toolsMenu.add(new JMenuItem("Data Sources..."));
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new JMenuItem("Documentation"));
        helpMenu.add(new JMenuItem("About F1 Dashboard"));
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    /**
     * Create a status bar for the application
     */
    private static JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        JLabel apiStatusLabel = new JLabel("API: Connected");
        apiStatusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        statusBar.add(apiStatusLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    /**
     * Create an icon for tabbed pane
     */
    private static Icon createIcon(String iconType) {
        // This is a simple implementation that creates colored squares as icons
        // In a real application, you would load actual icons from resources
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                
                switch (iconType) {
                    case "dashboard":
                        g2d.setColor(new Color(65, 105, 225)); // Royal Blue
                        break;
                    case "telemetry":
                        g2d.setColor(new Color(46, 139, 87)); // Sea Green
                        break;
                    case "compare":
                        g2d.setColor(new Color(210, 105, 30)); // Chocolate
                        break;
                    case "history":
                        g2d.setColor(new Color(128, 0, 128)); // Purple
                        break;
                    default:
                        g2d.setColor(Color.GRAY);
                }
                
                g2d.fillRect(x, y, getIconWidth(), getIconHeight());
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, getIconWidth() - 1, getIconHeight() - 1);
                
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return 16;
            }
            
            @Override
            public int getIconHeight() {
                return 16;
            }
        };
    }
    
    /**
     * Load custom settings from arguments or config file
     */
    private static void loadCustomSettings() {
        // Check for config file
        File configFile = new File("f1dashboard.properties");
        if (configFile.exists()) {
            System.out.println("Loading configuration from: " + configFile.getAbsolutePath());
            // In a real application, load properties from the file
            // Properties props = new Properties();
            // props.load(new FileInputStream(configFile));
            // Add properties to appSettings
        }
        
        // Override with system properties if specified
        String apiUrl = System.getProperty("f1dashboard.api.url");
        if (apiUrl != null && !apiUrl.isEmpty()) {
            appSettings.put("api_base_url", apiUrl);
        }
    }
    
    /**
     * Initialize logging for the application
     */
    private static void initializeLogging() {
        // In a real application, you would set up a proper logging framework like Log4j
        // For this example, we'll use simple console logging
        System.out.println("F1 Dashboard logging initialized");
        System.out.println("API Base URL: " + appSettings.get("api_base_url"));
        System.out.println("Cache Enabled: " + appSettings.get("cache_enabled"));
    }
}