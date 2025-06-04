import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simplified version of the F1 Dashboard focusing on the track map
 */
public class F1SimpleDashboard extends JFrame {
    private TrackMapPanel trackMapPanel;
    private JComboBox<String> trackSelector;
    private JButton animateButton;
    private Timer animationTimer;
    private boolean animating = false;
    
    /**
     * Constructor
     */
    public F1SimpleDashboard() {
        super("F1 Simple Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(220, 0, 0));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("F1 Dashboard - Track Map Test", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Create control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        
        // Track selector
        String[] tracks = {"Bahrain International Circuit", "Jeddah Corniche Circuit", 
                           "Albert Park Circuit", "Miami International Autodrome", "Circuit de Monaco"};
        trackSelector = new JComboBox<>(tracks);
        trackSelector.addActionListener(e -> updateTrack());
        controlPanel.add(new JLabel("Select Track:"));
        controlPanel.add(trackSelector);
        
        // Animate button
        animateButton = new JButton("Start Animation");
        animateButton.addActionListener(e -> toggleAnimation());
        controlPanel.add(animateButton);
        
        add(controlPanel, BorderLayout.SOUTH);
        
        // Create track map panel
        trackMapPanel = new TrackMapPanel();
        add(trackMapPanel, BorderLayout.CENTER);
        
        // Initialize animation timer
        animationTimer = new Timer(500, e -> updateDriverPositions());
        
        // Set initial track
        updateTrack();
    }
    
    /**
     * Update the track based on selection
     */
    private void updateTrack() {
        int trackIndex = trackSelector.getSelectedIndex() + 1;
        trackMapPanel.setTrack(trackIndex, trackSelector.getSelectedItem().toString());
        
        // Add some initial driver positions
        updateDriverPositions();
    }
    
    /**
     * Toggle animation on/off
     */
    private void toggleAnimation() {
        if (animating) {
            animationTimer.stop();
            animateButton.setText("Start Animation");
        } else {
            animationTimer.start();
            animateButton.setText("Stop Animation");
        }
        animating = !animating;
    }
    
    /**
     * Update driver positions for animation
     */
    private void updateDriverPositions() {
        List<TrackMapPanel.DriverPosition> positions = new ArrayList<>();
        Random rand = new Random();
        
        // Create 10 random driver positions
        String[] teams = {"Red Bull Racing", "Mercedes", "Ferrari", "McLaren", "Alpine", 
                          "Aston Martin", "AlphaTauri", "Williams", "Alfa Romeo", "Haas F1 Team"};
        
        for (int i = 0; i < 10; i++) {
            int number = (i + 1);
            String name = "Driver " + number;
            String team = teams[i];
            
            // If animating, create slightly randomized positions around the track
            double position;
            if (animating) {
                position = (i * 0.1 + rand.nextDouble() * 0.05) % 1.0;
            } else {
                position = i * 0.1;
            }
            
            positions.add(new TrackMapPanel.DriverPosition(number, name, team, position));
        }
        
        trackMapPanel.updateDriverPositions(positions);
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            F1SimpleDashboard dashboard = new F1SimpleDashboard();
            dashboard.setVisible(true);
        });
    }
}