import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Custom chart panel for F1 telemetry data visualization
 * Supports line charts, bar charts, and track maps
 */
public class ChartPanel extends JPanel {
    // Chart types
    public static final int LINE_CHART = 0;
    public static final int BAR_CHART = 1;
    public static final int TRACK_MAP = 2;
    
    // Chart data
    private List<Map<String, Object>> data;
    private String title;
    private String xAxisLabel;
    private String yAxisLabel;
    private int chartType;
    private String dataKey;
    
    // Styling
    private Color lineColor = Color.RED;
    private Color fillColor = new Color(255, 0, 0, 50);
    private Color axisColor = Color.BLACK;
    private Color gridColor = new Color(200, 200, 200);
    private Color textColor = Color.BLACK;
    
    // Track map info
    private int currentLap = 1;
    private int driverPosition = 1;
    private String tireCompound = "Soft";
    
    /**
     * Constructor
     * 
     * @param chartType The type of chart (LINE_CHART, BAR_CHART, TRACK_MAP)
     */
    public ChartPanel(int chartType) {
        this.chartType = chartType;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    /**
     * Set data for the chart
     * 
     * @param data List of data points
     * @param dataKey The key for the value to plot
     * @param title Chart title
     * @param xAxisLabel X-axis label
     * @param yAxisLabel Y-axis label
     */
    public void setData(List<Map<String, Object>> data, String dataKey, String title, String xAxisLabel, String yAxisLabel) {
        this.data = data;
        this.dataKey = dataKey;
        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        repaint();
    }
    
    /**
     * Set track map info for driver position visualization
     */
    public void setTrackMapInfo(int currentLap, int driverPosition, String tireCompound) {
        this.currentLap = currentLap;
        this.driverPosition = driverPosition;
        this.tireCompound = tireCompound;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (data == null || data.isEmpty()) {
            drawPlaceholder(g);
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        switch (chartType) {
            case LINE_CHART:
                drawLineChart(g2d);
                break;
            case BAR_CHART:
                drawBarChart(g2d);
                break;
            case TRACK_MAP:
                drawTrackMap(g2d);
                break;
        }
    }
    
    /**
     * Draw a placeholder when no data is available
     */
    private void drawPlaceholder(Graphics g) {
        String message = "No data available";
        
        FontMetrics metrics = g.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(message)) / 2;
        int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
        
        g.setColor(textColor);
        g.drawString(message, x, y);
    }
    
    /**
     * Draw a line chart
     */
    private void drawLineChart(Graphics2D g2d) {
        int padding = 40;
        int labelPadding = 10;
        int chartWidth = getWidth() - (2 * padding);
        int chartHeight = getHeight() - (2 * padding) - 30; // Extra space for title
        
        // Find max Y value
        double maxY = 0;
        for (Map<String, Object> point : data) {
            double value = getDoubleValue(point, dataKey);
            if (value > maxY) maxY = value;
        }
        
        // Round up to neat number
        maxY = Math.ceil(maxY / 50) * 50;
        
        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(textColor);
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, (getWidth() - titleWidth) / 2, 20);
        
        // Draw axes
        g2d.setColor(axisColor);
        g2d.drawLine(padding, getHeight() - padding, padding, padding + 30);
        g2d.drawLine(padding, getHeight() - padding, getWidth() - padding, getHeight() - padding);
        
        // Draw grid
        g2d.setColor(gridColor);
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
        
        // Horizontal grid lines
        for (int i = 0; i <= 5; i++) {
            int y = getHeight() - padding - (i * chartHeight / 5);
            g2d.drawLine(padding, y, getWidth() - padding, y);
        }
        
        // Y axis labels (value)
        g2d.setColor(textColor);
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        for (int i = 0; i <= 5; i++) {
            int y = getHeight() - padding - (i * chartHeight / 5);
            int value = (int)(i * maxY / 5);
            String label = String.valueOf(value);
            int labelWidth = g2d.getFontMetrics().stringWidth(label);
            g2d.drawString(label, padding - labelWidth - 5, y + 4);
        }
        
        // X axis labels (laps)
        int dataPoints = data.size();
        int xStep = chartWidth / (dataPoints - 1);
        for (int i = 0; i < dataPoints; i++) {
            int x = padding + (i * xStep);
            String label = String.valueOf(i + 1);
            int labelWidth = g2d.getFontMetrics().stringWidth(label);
            g2d.drawString(label, x - labelWidth / 2, getHeight() - padding + 20);
        }
        
        // Draw axis labels
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // X axis label
        int xLabelWidth = g2d.getFontMetrics().stringWidth(xAxisLabel);
        g2d.drawString(xAxisLabel, (getWidth() - xLabelWidth) / 2, getHeight() - 5);
        
        // Y axis label
        Graphics2D g2dY = (Graphics2D) g2d.create();
        g2dY.rotate(-Math.PI / 2);
        g2dY.drawString(yAxisLabel, -getHeight() / 2 - g2d.getFontMetrics().stringWidth(yAxisLabel) / 2, 15);
        g2dY.dispose();
        
        // Draw line and points
        g2d.setColor(lineColor);
        g2d.setStroke(new BasicStroke(2f));
        
        int[] xPoints = new int[dataPoints];
        int[] yPoints = new int[dataPoints];
        
        for (int i = 0; i < dataPoints; i++) {
            Map<String, Object> point = data.get(i);
            double value = getDoubleValue(point, dataKey);
            int x = padding + (i * xStep);
            int y = getHeight() - padding - (int)((value / maxY) * chartHeight);
            
            xPoints[i] = x;
            yPoints[i] = y;
            
            // Draw point
            g2d.fillOval(x - 3, y - 3, 6, 6);
        }
        
        // Draw line connecting all points
        for (int i = 0; i < dataPoints - 1; i++) {
            g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
        }
        
        // Draw filled area below line
        g2d.setColor(fillColor);
        int[] xPointsFill = new int[dataPoints + 2];
        int[] yPointsFill = new int[dataPoints + 2];
        
        // Start at bottom left
        xPointsFill[0] = padding;
        yPointsFill[0] = getHeight() - padding;
        
        // Copy existing points
        for (int i = 0; i < dataPoints; i++) {
            xPointsFill[i + 1] = xPoints[i];
            yPointsFill[i + 1] = yPoints[i];
        }
        
        // End at bottom right
        xPointsFill[dataPoints + 1] = getWidth() - padding;
        yPointsFill[dataPoints + 1] = getHeight() - padding;
        
        g2d.fillPolygon(xPointsFill, yPointsFill, xPointsFill.length);
    }
    
    /**
     * Draw a bar chart
     */
    private void drawBarChart(Graphics2D g2d) {
        int padding = 40;
        int labelPadding = 10;
        int chartWidth = getWidth() - (2 * padding);
        int chartHeight = getHeight() - (2 * padding) - 30; // Extra space for title
        
        // Find max Y value
        double maxY = 0;
        for (Map<String, Object> point : data) {
            double value = getDoubleValue(point, dataKey);
            if (value > maxY) maxY = value;
        }
        
        // Round up to neat number
        maxY = Math.ceil(maxY / 50) * 50;
        
        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(textColor);
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, (getWidth() - titleWidth) / 2, 20);
        
        // Draw axes
        g2d.setColor(axisColor);
        g2d.drawLine(padding, getHeight() - padding, padding, padding + 30);
        g2d.drawLine(padding, getHeight() - padding, getWidth() - padding, getHeight() - padding);
        
        // Draw grid
        g2d.setColor(gridColor);
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
        
        // Horizontal grid lines
        for (int i = 0; i <= 5; i++) {
            int y = getHeight() - padding - (i * chartHeight / 5);
            g2d.drawLine(padding, y, getWidth() - padding, y);
        }
        
        // Y axis labels (value)
        g2d.setColor(textColor);
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        for (int i = 0; i <= 5; i++) {
            int y = getHeight() - padding - (i * chartHeight / 5);
            int value = (int)(i * maxY / 5);
            String label = String.valueOf(value);
            int labelWidth = g2d.getFontMetrics().stringWidth(label);
            g2d.drawString(label, padding - labelWidth - 5, y + 4);
        }
        
        // Draw bars
        int dataPoints = data.size();
        int barWidth = chartWidth / dataPoints;
        
        for (int i = 0; i < dataPoints; i++) {
            Map<String, Object> point = data.get(i);
            double value = getDoubleValue(point, dataKey);
            int barHeight = (int)((value / maxY) * chartHeight);
            
            int x = padding + (i * barWidth) + barWidth/4;
            int y = getHeight() - padding - barHeight;
            
            // Draw bar
            g2d.setColor(fillColor);
            g2d.fillRect(x, y, barWidth/2, barHeight);
            
            g2d.setColor(lineColor);
            g2d.drawRect(x, y, barWidth/2, barHeight);
            
            // X axis label (lap number)
            String label = String.valueOf(i + 1);
            int labelWidth = g2d.getFontMetrics().stringWidth(label);
            g2d.setColor(textColor);
            g2d.drawString(label, x + barWidth/4 - labelWidth/2, getHeight() - padding + 20);
        }
        
        // Draw axis labels
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // X axis label
        int xLabelWidth = g2d.getFontMetrics().stringWidth(xAxisLabel);
        g2d.drawString(xAxisLabel, (getWidth() - xLabelWidth) / 2, getHeight() - 5);
        
        // Y axis label
        Graphics2D g2dY = (Graphics2D) g2d.create();
        g2dY.rotate(-Math.PI / 2);
        g2dY.drawString(yAxisLabel, -getHeight() / 2 - g2d.getFontMetrics().stringWidth(yAxisLabel) / 2, 15);
        g2dY.dispose();
    }
    
    /**
     * Draw a track map with driver position
     */
    private void drawTrackMap(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();
        
        // Draw track outline
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(20f));
        
        // Create oval track shape
        int trackPadding = 60;
        int trackWidth = width - (2 * trackPadding);
        int trackHeight = height - (2 * trackPadding);
        g2d.drawOval(trackPadding, trackPadding, trackWidth, trackHeight);
        
        // Draw track center line
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
        g2d.drawOval(trackPadding, trackPadding, trackWidth, trackHeight);
        
        // Draw track sectors
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(trackPadding + trackWidth/2 - 5, trackPadding - 5, 10, 10); // Sector 1
        g2d.fillOval(trackPadding + trackWidth - 5, trackPadding + trackHeight/2 - 5, 10, 10); // Sector 2
        g2d.fillOval(trackPadding + trackWidth/2 - 5, trackPadding + trackHeight - 5, 10, 10); // Sector 3
        
        // Draw start/finish line
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawLine(trackPadding + trackWidth/2, trackPadding - 10, trackPadding + trackWidth/2, trackPadding + 10);
        
        // Calculate driver position on track
        double lapProgress = (currentLap - 1) % data.size() / (double)data.size();
        double angle = lapProgress * 2 * Math.PI - Math.PI/2; // Start at top (-PI/2)
        
        int centerX = trackPadding + trackWidth/2;
        int centerY = trackPadding + trackHeight/2;
        int radiusX = trackWidth/2;
        int radiusY = trackHeight/2;
        
        int carX = centerX + (int)(radiusX * Math.cos(angle));
        int carY = centerY + (int)(radiusY * Math.sin(angle));
        
        // Determine car color based on tire compound
        Color carColor;
        if ("Soft".equalsIgnoreCase(tireCompound)) {
            carColor = Color.RED;
        } else if ("Medium".equalsIgnoreCase(tireCompound)) {
            carColor = Color.YELLOW;
        } else if ("Hard".equalsIgnoreCase(tireCompound)) {
            carColor = Color.WHITE;
        } else {
            carColor = Color.RED;
        }
        
        // Draw driver's car
        g2d.setColor(carColor);
        g2d.fillOval(carX - 8, carY - 8, 16, 16);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawOval(carX - 8, carY - 8, 16, 16);
        
        // Draw driver position
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        String posStr = String.valueOf(driverPosition);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(posStr, carX - fm.stringWidth(posStr)/2, carY + fm.getAscent()/2);
        
        // Draw info box
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(width - 150, 20, 130, 80, 10, 10);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Lap: " + currentLap, width - 140, 40);
        g2d.drawString("Position: " + driverPosition, width - 140, 60);
        g2d.drawString("Tires: " + tireCompound, width - 140, 80);
    }
    
    /**
     * Helper method to safely get a double value from a map
     */
    private double getDoubleValue(Map<String, Object> map, String key) {
        if (!map.containsKey(key)) return 0;
        
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * Base chart class for custom charts
     */
    abstract class BaseChart {
        protected int padding;
        protected int chartWidth;
        protected int chartHeight;
        
        public BaseChart(int padding) {
            this.padding = padding;
            this.chartWidth = getWidth() - (2 * padding);
            this.chartHeight = getHeight() - (2 * padding) - 30; // Extra space for title
        }
        
        abstract void draw(Graphics2D g2d);
    }
}