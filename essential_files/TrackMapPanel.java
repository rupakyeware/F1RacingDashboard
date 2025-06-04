import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * TrackMapPanel - A specialized panel for displaying F1 circuit layouts
 * with real-time driver positioning
 */
public class TrackMapPanel extends JPanel {
    // Track data
    private String trackName;
    private Path2D trackPath;
    private List<DriverPosition> driverPositions;
    private Map<String, Color> teamColors;
    private Map<Integer, Image> trackImages;
    private Image currentTrackImage;
    private int trackId;
    
    // Display settings
    private boolean showDriverNames = true;
    private boolean showSectors = true;
    private boolean showPitLane = true;
    private boolean useCustomColors = true;
    
    // Constants
    private static final int DRIVER_MARKER_SIZE = 20;
    private static final int PADDING = 30;
    
    /**
     * Constructor
     */
    public TrackMapPanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Initialize data structures
        driverPositions = new ArrayList<>();
        teamColors = createTeamColorMap();
        trackImages = loadTrackImages();
        
        // Set default track
        trackId = 1; // Default to first track
        trackName = "Unknown Circuit";
        trackPath = createDefaultTrackPath();
        updateTrackImage();
    }
    
    /**
     * Set the current track by ID
     * 
     * @param trackId The track ID to display
     * @param trackName The name of the track
     */
    public void setTrack(int trackId, String trackName) {
        this.trackId = trackId;
        this.trackName = trackName;
        updateTrackImage();
        repaint();
    }
    
    /**
     * Update the displayed track image based on trackId
     */
    private void updateTrackImage() {
        currentTrackImage = trackImages.getOrDefault(trackId, null);
    }
    
    /**
     * Update driver positions on the track
     * 
     * @param positions List of driver positions
     */
    public void updateDriverPositions(List<DriverPosition> positions) {
        this.driverPositions = positions;
        repaint();
    }
    
    /**
     * Toggle showing driver names
     */
    public void toggleDriverNames() {
        showDriverNames = !showDriverNames;
        repaint();
    }
    
    /**
     * Toggle showing sector boundaries
     */
    public void toggleSectors() {
        showSectors = !showSectors;
        repaint();
    }
    
    /**
     * Toggle showing pit lane
     */
    public void togglePitLane() {
        showPitLane = !showPitLane;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Draw track name
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(Color.BLACK);
        drawCenteredString(g2d, trackName, width / 2, 25);
        
        // Draw track
        if (currentTrackImage != null) {
            drawTrackImage(g2d, width, height);
        } else {
            drawTrackPath(g2d, width, height);
        }
        
        // Draw sector boundaries if enabled
        if (showSectors) {
            drawSectors(g2d, width, height);
        }
        
        // Draw pit lane if enabled
        if (showPitLane) {
            drawPitLane(g2d, width, height);
        }
        
        // Draw driver positions
        drawDriverPositions(g2d, width, height);
        
        // Draw legend
        drawLegend(g2d, width, height);
    }
    
    /**
     * Draw the track using the track image
     */
    private void drawTrackImage(Graphics2D g2d, int width, int height) {
        int imgWidth = currentTrackImage.getWidth(this);
        int imgHeight = currentTrackImage.getHeight(this);
        
        // Calculate scaling factor to fit in panel while maintaining aspect ratio
        double scale = Math.min(
            (double)(width - 2 * PADDING) / imgWidth,
            (double)(height - 2 * PADDING - 50) / imgHeight // Allow space for title and legend
        );
        
        int scaledWidth = (int)(imgWidth * scale);
        int scaledHeight = (int)(imgHeight * scale);
        
        // Draw the image centered in the panel
        int x = (width - scaledWidth) / 2;
        int y = ((height - 50) - scaledHeight) / 2 + 40; // Adjust for title
        
        g2d.drawImage(currentTrackImage, x, y, scaledWidth, scaledHeight, this);
    }
    
    /**
     * Draw the track using the track path
     */
    private void drawTrackPath(Graphics2D g2d, int width, int height) {
        // Transform the path to fit the panel
        Rectangle2D bounds = trackPath.getBounds2D();
        
        // Calculate scaling and translation
        double scale = Math.min(
            (width - 2 * PADDING) / bounds.getWidth(),
            (height - 2 * PADDING - 50) / bounds.getHeight()  // Allow space for title and legend
        );
        
        // Create a transformed copy of the path
        AffineTransform at = new AffineTransform();
        at.translate((width - bounds.getWidth() * scale) / 2 - bounds.getX() * scale,
                     ((height - 50) - bounds.getHeight() * scale) / 2 - bounds.getY() * scale + 40); // Adjust for title
        at.scale(scale, scale);
        
        Shape transformedPath = at.createTransformedShape(trackPath);
        
        // Draw track outline
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(20f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(transformedPath);
        
        // Draw track inner line
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(18f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(transformedPath);
        
        // Draw track center line (dashed)
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
        g2d.draw(transformedPath);
        
        // Draw start/finish line
        Point2D start = getPointOnPath(transformedPath, 0);
        if (start != null) {
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2d.drawLine((int)start.getX() - 10, (int)start.getY(), (int)start.getX() + 10, (int)start.getY());
        }
    }
    
    /**
     * Draw sector boundaries on the track
     */
    private void drawSectors(Graphics2D g2d, int width, int height) {
        // Sample sector boundary points at 1/3 and 2/3 around the track
        // In a real application, these would be determined from actual track data
        
        g2d.setColor(Color.YELLOW);
        g2d.setStroke(new BasicStroke(3f));
        
        // For the image-based approach, we'd have predetermined points
        if (currentTrackImage != null) {
            // These would be predefined for each track
            int scaledWidth = width - 2 * PADDING;
            int scaledHeight = height - 2 * PADDING - 50;
            int x = PADDING;
            int y = PADDING + 40;
            
            // Sector 1 to 2 transition (approximately 33% around the track)
            g2d.drawLine(x + scaledWidth / 3, y + scaledHeight / 2 - 30, 
                         x + scaledWidth / 3, y + scaledHeight / 2 + 30);
            
            // Sector 2 to 3 transition (approximately 66% around the track)
            g2d.drawLine(x + 2 * scaledWidth / 3, y + scaledHeight / 2 - 30, 
                         x + 2 * scaledWidth / 3, y + scaledHeight / 2 + 30);
        } else {
            // For path-based approach, we would calculate points along the path
            Shape transformedPath = getTransformedPath(width, height);
            
            Point2D sector1 = getPointOnPath(transformedPath, 0.33);
            Point2D sector2 = getPointOnPath(transformedPath, 0.66);
            
            if (sector1 != null) {
                g2d.drawLine((int)sector1.getX() - 10, (int)sector1.getY() - 10, 
                             (int)sector1.getX() + 10, (int)sector1.getY() + 10);
            }
            
            if (sector2 != null) {
                g2d.drawLine((int)sector2.getX() - 10, (int)sector2.getY() - 10, 
                             (int)sector2.getX() + 10, (int)sector2.getY() + 10);
            }
        }
    }
    
    /**
     * Draw pit lane on the track
     */
    private void drawPitLane(Graphics2D g2d, int width, int height) {
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(5f));
        
        // For the image-based approach, we'd have predetermined points
        if (currentTrackImage != null) {
            // These would be predefined for each track
            int scaledWidth = width - 2 * PADDING;
            int scaledHeight = height - 2 * PADDING - 50;
            int x = PADDING;
            int y = PADDING + 40;
            
            // Simple horizontal pit lane near the start/finish
            g2d.drawLine(x + scaledWidth / 2 - 100, y + scaledHeight / 4, 
                         x + scaledWidth / 2 + 100, y + scaledHeight / 4);
        } else {
            // For path-based approach, we would calculate points near start line
            Shape transformedPath = getTransformedPath(width, height);
            
            Point2D start = getPointOnPath(transformedPath, 0);
            if (start != null) {
                // Draw a simple line representing the pit lane
                g2d.drawLine((int)start.getX() - 100, (int)start.getY() - 30, 
                             (int)start.getX() + 100, (int)start.getY() - 30);
            }
        }
    }
    
    /**
     * Draw driver positions on the track
     */
    private void drawDriverPositions(Graphics2D g2d, int width, int height) {
        if (driverPositions.isEmpty()) return;
        
        // Scale for the display
        Shape transformedPath = getTransformedPath(width, height);
        
        for (DriverPosition driver : driverPositions) {
            // Get position along the track (percentage of the track completed)
            Point2D position = getPointOnPath(transformedPath, driver.getTrackPosition());
            if (position == null) continue;
            
            int x = (int) position.getX();
            int y = (int) position.getY();
            
            // Get team color
            Color teamColor = teamColors.getOrDefault(driver.getTeam(), Color.GRAY);
            
            // Draw driver marker (colored circle with driver number)
            g2d.setColor(teamColor);
            g2d.fillOval(x - DRIVER_MARKER_SIZE/2, y - DRIVER_MARKER_SIZE/2, DRIVER_MARKER_SIZE, DRIVER_MARKER_SIZE);
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            drawCenteredString(g2d, String.valueOf(driver.getNumber()), x, y + 4);
            
            // Draw driver name if enabled
            if (showDriverNames) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                g2d.drawString(driver.getName(), x + DRIVER_MARKER_SIZE/2 + 5, y + 4);
            }
        }
    }
    
    /**
     * Draw legend showing teams and colors
     */
    private void drawLegend(Graphics2D g2d, int width, int height) {
        int legendY = height - 30;
        int legendX = 20;
        int boxSize = 10;
        int entryWidth = 120;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        
        // Get unique teams from driver positions
        Set<String> teams = new HashSet<>();
        for (DriverPosition driver : driverPositions) {
            teams.add(driver.getTeam());
        }
        
        // If no drivers, show some default teams
        if (teams.isEmpty()) {
            teams.add("Red Bull Racing");
            teams.add("Mercedes");
            teams.add("Ferrari");
        }
        
        // Draw team color boxes and names
        int i = 0;
        for (String team : teams) {
            int x = legendX + (i % 3) * entryWidth;
            int y = legendY + (i / 3) * 15;
            
            Color teamColor = teamColors.getOrDefault(team, Color.GRAY);
            g2d.setColor(teamColor);
            g2d.fillRect(x, y - 9, boxSize, boxSize);
            
            g2d.setColor(Color.BLACK);
            g2d.drawString(team, x + boxSize + 5, y);
            
            i++;
        }
    }
    
    /**
     * Create a map of team colors
     */
    private Map<String, Color> createTeamColorMap() {
        Map<String, Color> colors = new HashMap<>();
        
        // 2023 F1 Teams and colors
        colors.put("Red Bull Racing", new Color(6, 0, 239));
        colors.put("Mercedes", new Color(0, 210, 190));
        colors.put("Ferrari", new Color(220, 0, 0));
        colors.put("McLaren", new Color(255, 135, 0));
        colors.put("Aston Martin", new Color(0, 111, 98));
        colors.put("Alpine", new Color(0, 144, 255));
        colors.put("Williams", new Color(0, 90, 255));
        colors.put("AlphaTauri", new Color(43, 69, 98));
        colors.put("Alfa Romeo", new Color(157, 24, 45));
        colors.put("Haas F1 Team", new Color(255, 255, 255));
        
        return colors;
    }
    
    /**
     * Load track images from resources
     */
    private Map<Integer, Image> loadTrackImages() {
        Map<Integer, Image> images = new HashMap<>();
        
        // In a real application, you would load actual track images from files
        // For this example, we'll create placeholder track paths
        
        // You can later load from files like:
        // images.put(1, ImageIO.read(new File("tracks/bahrain.png")));
        
        return images;
    }
    
    /**
     * Create a default track path (simple oval) when no image is available
     */
    private Path2D createDefaultTrackPath() {
        Path2D path = new Path2D.Double();
        
        // Create a simple oval track
        path.moveTo(100, 50);
        path.curveTo(300, 0, 500, 100, 400, 200);
        path.curveTo(350, 250, 250, 300, 200, 280);
        path.curveTo(100, 250, 50, 150, 100, 50);
        path.closePath();
        
        return path;
    }
    
    /**
     * Get a transformed path scaled to fit the panel
     */
    private Shape getTransformedPath(int width, int height) {
        if (trackPath == null) return null;
        
        Rectangle2D bounds = trackPath.getBounds2D();
        
        // Calculate scaling and translation
        double scale = Math.min(
            (width - 2 * PADDING) / bounds.getWidth(),
            (height - 2 * PADDING - 50) / bounds.getHeight()  // Allow space for title and legend
        );
        
        // Create a transformed copy of the path
        AffineTransform at = new AffineTransform();
        at.translate((width - bounds.getWidth() * scale) / 2 - bounds.getX() * scale,
                     ((height - 50) - bounds.getHeight() * scale) / 2 - bounds.getY() * scale + 40); // Adjust for title
        at.scale(scale, scale);
        
        return at.createTransformedShape(trackPath);
    }
    
    /**
     * Get a point that is a certain percentage along the path
     * 
     * @param path The path to follow
     * @param percentage How far along the path (0.0 to 1.0)
     * @return A point on the path
     */
    private Point2D getPointOnPath(Shape path, double percentage) {
        if (path == null) return null;
        
        // Flatten the path to get easier access to points
        PathIterator it = path.getPathIterator(null, 1.0); // Flatness of 1.0
        double[] coords = new double[6];
        double totalLength = 0;
        ArrayList<Point2D> points = new ArrayList<>();
        ArrayList<Double> distances = new ArrayList<>();
        
        // First pass: collect all points and calculate total length
        double lastX = 0, lastY = 0;
        boolean first = true;
        
        while (!it.isDone()) {
            int type = it.currentSegment(coords);
            double x = coords[0];
            double y = coords[1];
            
            if (first) {
                first = false;
            } else {
                double distance = Point2D.distance(lastX, lastY, x, y);
                totalLength += distance;
                points.add(new Point2D.Double(lastX, lastY));
                distances.add(distance);
            }
            
            lastX = x;
            lastY = y;
            it.next();
        }
        
        // Add the last point
        points.add(new Point2D.Double(lastX, lastY));
        
        // Second pass: find the point at the desired percentage of the total length
        double targetDistance = percentage * totalLength;
        double currentDistance = 0;
        
        for (int i = 0; i < distances.size(); i++) {
            double nextDistance = currentDistance + distances.get(i);
            
            if (nextDistance >= targetDistance) {
                // Found the segment containing the target point
                double segmentPercentage = (targetDistance - currentDistance) / distances.get(i);
                
                // Linear interpolation between the points
                Point2D p1 = points.get(i);
                Point2D p2 = points.get(i + 1);
                
                double x = p1.getX() + segmentPercentage * (p2.getX() - p1.getX());
                double y = p1.getY() + segmentPercentage * (p2.getY() - p1.getY());
                
                return new Point2D.Double(x, y);
            }
            
            currentDistance = nextDistance;
        }
        
        // If we reach here, return the last point
        return points.get(points.size() - 1);
    }
    
    /**
     * Helper method to draw a centered string
     */
    private void drawCenteredString(Graphics2D g, String text, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g.drawString(text, x - textWidth / 2, y);
    }
    
    /**
     * Create a set of sample driver positions for testing
     */
    public List<DriverPosition> createSampleDriverPositions() {
        List<DriverPosition> positions = new ArrayList<>();
        
        // Sample driver positions for testing
        positions.add(new DriverPosition(1, "Max Verstappen", "Red Bull Racing", 0.1));
        positions.add(new DriverPosition(11, "Sergio Perez", "Red Bull Racing", 0.15));
        positions.add(new DriverPosition(44, "Lewis Hamilton", "Mercedes", 0.2));
        positions.add(new DriverPosition(63, "George Russell", "Mercedes", 0.25));
        positions.add(new DriverPosition(16, "Charles Leclerc", "Ferrari", 0.3));
        positions.add(new DriverPosition(55, "Carlos Sainz", "Ferrari", 0.35));
        positions.add(new DriverPosition(4, "Lando Norris", "McLaren", 0.4));
        positions.add(new DriverPosition(3, "Daniel Ricciardo", "McLaren", 0.45));
        positions.add(new DriverPosition(14, "Fernando Alonso", "Aston Martin", 0.5));
        positions.add(new DriverPosition(18, "Lance Stroll", "Aston Martin", 0.55));
        positions.add(new DriverPosition(31, "Esteban Ocon", "Alpine", 0.6));
        positions.add(new DriverPosition(10, "Pierre Gasly", "Alpine", 0.65));
        positions.add(new DriverPosition(23, "Alexander Albon", "Williams", 0.7));
        positions.add(new DriverPosition(6, "Nicholas Latifi", "Williams", 0.75));
        positions.add(new DriverPosition(22, "Yuki Tsunoda", "AlphaTauri", 0.8));
        positions.add(new DriverPosition(21, "Nyck de Vries", "AlphaTauri", 0.85));
        positions.add(new DriverPosition(77, "Valtteri Bottas", "Alfa Romeo", 0.9));
        positions.add(new DriverPosition(24, "Zhou Guanyu", "Alfa Romeo", 0.95));
        positions.add(new DriverPosition(20, "Kevin Magnussen", "Haas F1 Team", 0.98));
        positions.add(new DriverPosition(47, "Mick Schumacher", "Haas F1 Team", 0.99));
        
        return positions;
    }
    
    /**
     * Helper class to manage track data
     */
    public class TrackData {
        private int id;
        private String name;
        private Path2D path;
        
        public TrackData(int id, String name, Path2D path) {
            this.id = id;
            this.name = name;
            this.path = path;
        }
        
        public int getId() { return id; }
        public String getName() { return name; }
        public Path2D getPath() { return path; }
    }
    
    /**
     * Helper class to represent a driver's position on the track
     */
    public static class DriverPosition {
        private int number;
        private String name;
        private String team;
        private double trackPosition; // 0.0 to 1.0, percentage around the track
        
        public DriverPosition(int number, String name, String team, double trackPosition) {
            this.number = number;
            this.name = name;
            this.team = team;
            this.trackPosition = trackPosition;
        }
        
        public int getNumber() { return number; }
        public String getName() { return name; }
        public String getTeam() { return team; }
        public double getTrackPosition() { return trackPosition; }
    }
    
    /**
     * Create sample track paths for different circuits
     */
    public Map<Integer, TrackData> createSampleTrackData() {
        Map<Integer, TrackData> tracks = new HashMap<>();
        
        // 1. Bahrain International Circuit
        Path2D bahrain = new Path2D.Double();
        bahrain.moveTo(100, 150);
        bahrain.lineTo(350, 150);
        bahrain.lineTo(400, 100);
        bahrain.lineTo(450, 150);
        bahrain.lineTo(450, 250);
        bahrain.lineTo(400, 300);
        bahrain.lineTo(350, 250);
        bahrain.lineTo(250, 350);
        bahrain.lineTo(150, 350);
        bahrain.lineTo(100, 300);
        bahrain.lineTo(50, 200);
        bahrain.closePath();
        tracks.put(1, new TrackData(1, "Bahrain International Circuit", bahrain));
        
        // 2. Jeddah Corniche Circuit
        Path2D jeddah = new Path2D.Double();
        jeddah.moveTo(100, 100);
        jeddah.lineTo(450, 100);
        jeddah.lineTo(450, 350);
        jeddah.curveTo(400, 400, 200, 400, 150, 350);
        jeddah.lineTo(150, 250);
        jeddah.lineTo(250, 200);
        jeddah.lineTo(150, 150);
        jeddah.closePath();
        tracks.put(2, new TrackData(2, "Jeddah Corniche Circuit", jeddah));
        
        // 3. Albert Park Circuit
        Path2D albert = new Path2D.Double();
        albert.moveTo(100, 150);
        albert.curveTo(150, 50, 350, 50, 400, 150);
        albert.curveTo(450, 200, 450, 300, 400, 350);
        albert.curveTo(350, 400, 150, 400, 100, 350);
        albert.curveTo(50, 300, 50, 200, 100, 150);
        tracks.put(3, new TrackData(3, "Albert Park Circuit", albert));
        
        // 4. Miami International Autodrome
        Path2D miami = new Path2D.Double();
        miami.moveTo(100, 150);
        miami.lineTo(400, 150);
        miami.curveTo(450, 150, 450, 250, 400, 250);
        miami.lineTo(300, 250);
        miami.lineTo(250, 300);
        miami.lineTo(150, 300);
        miami.lineTo(100, 250);
        miami.closePath();
        tracks.put(4, new TrackData(4, "Miami International Autodrome", miami));
        
        // 5. Circuit de Monaco
        Path2D monaco = new Path2D.Double();
        monaco.moveTo(100, 200);
        monaco.curveTo(100, 150, 150, 100, 200, 100);
        monaco.lineTo(400, 100);
        monaco.curveTo(450, 100, 450, 150, 450, 200);
        monaco.lineTo(400, 250);
        monaco.lineTo(300, 250);
        monaco.lineTo(250, 350);
        monaco.lineTo(200, 350);
        monaco.lineTo(150, 300);
        monaco.lineTo(150, 250);
        monaco.closePath();
        tracks.put(5, new TrackData(5, "Circuit de Monaco", monaco));
        
        return tracks;
    }
    
    /**
     * Set track data from the sample track data
     */
    public void setTrackData(int trackId) {
        Map<Integer, TrackData> trackData = createSampleTrackData();
        
        if (trackData.containsKey(trackId)) {
            TrackData track = trackData.get(trackId);
            this.trackId = track.getId();
            this.trackName = track.getName();
            this.trackPath = track.getPath();
        } else {
            // Default track if ID not found
            this.trackId = 1;
            this.trackName = "Unknown Circuit";
            this.trackPath = createDefaultTrackPath();
        }
        
        repaint();
    }
}