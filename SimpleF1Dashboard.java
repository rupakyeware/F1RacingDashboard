import javax.swing.*;
import java.awt.*;

public class SimpleF1Dashboard {
    public static void main(String[] args) {
        try {
            // Set look and feel to system
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JFrame frame = new JFrame("F1 Dashboard - Simple Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Add a simple header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(220, 0, 0));
        JLabel titleLabel = new JLabel("F1 Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create content panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add TrackMapPanel
        try {
            TrackMapPanel trackMapPanel = new TrackMapPanel();
            tabbedPane.addTab("Track Map", trackMapPanel);
            System.out.println("TrackMapPanel loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading TrackMapPanel: " + e.getMessage());
            e.printStackTrace();
            // Add placeholder if TrackMapPanel fails
            tabbedPane.addTab("Track Map", new JPanel());
        }
        
        // Add HistoricalComparisonPanel
        try {
            HistoricalComparisonPanel histPanel = new HistoricalComparisonPanel();
            tabbedPane.addTab("Historical Comparison", histPanel);
            System.out.println("HistoricalComparisonPanel loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading HistoricalComparisonPanel: " + e.getMessage());
            e.printStackTrace();
            // Add placeholder if HistoricalComparisonPanel fails
            tabbedPane.addTab("Historical Comparison", new JPanel());
        }
        
        // Add simple placeholder panel
        JPanel placeholderPanel = new JPanel();
        placeholderPanel.setLayout(new BorderLayout());
        placeholderPanel.add(new JLabel("Simple Dashboard Test", JLabel.CENTER), BorderLayout.CENTER);
        tabbedPane.addTab("Info", placeholderPanel);
        
        // Add the tabbed pane to the main panel
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add footer
        JPanel footerPanel = new JPanel();
        footerPanel.add(new JLabel("F1 Dashboard Test Application"));
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
        frame.setVisible(true);
        System.out.println("Simple F1 Dashboard initialized successfully!");
    }
}