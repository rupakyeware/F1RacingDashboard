import javax.swing.*;
import java.awt.*;

public class TestTrackMap {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Track Map Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        
        TrackMapPanel trackMap = new TrackMapPanel();
        frame.add(trackMap);
        
        frame.setVisible(true);
        System.out.println("Track Map initialized successfully!");
    }
}