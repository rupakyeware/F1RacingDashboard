import javax.swing.*;
import java.awt.*;

/**
 * A simple test for the historical comparison panel
 */
public class HistoricalComparisonTest {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JFrame frame = new JFrame("Historical Comparison Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout());
        
        // Create header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(220, 0, 0));
        headerPanel.setPreferredSize(new Dimension(frame.getWidth(), 60));
        
        JLabel titleLabel = new JLabel("Historical Comparison Test", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        frame.add(headerPanel, BorderLayout.NORTH);
        
        // Add historical comparison panel
        HistoricalComparisonPanel historicalPanel = new HistoricalComparisonPanel();
        frame.add(historicalPanel, BorderLayout.CENTER);
        
        frame.setVisible(true);
        System.out.println("Historical Comparison Test initialized successfully!");
    }
}