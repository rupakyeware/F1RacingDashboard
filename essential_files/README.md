# F1 Analytics Dashboard

A Java-based F1 Analytics Dashboard that provides comprehensive race data visualization and interactive analytics for motorsport enthusiasts.

## Features

- Real-time race data updates
- Historical data comparison between drivers and races
- Detailed circuit maps with driver positioning
- Advanced telemetry visualization

## Technologies Used

- Java Swing/AWT for desktop UI
- MySQL database backend
- Formula 1 data (2020-2024)

## How to Run Locally

### Prerequisites

1. Java Development Kit (JDK) 11 or higher
2. MySQL Workbench (or MySQL Server)

### Setup Database

1. Open MySQL Workbench and connect to your MySQL instance
2. Run the provided SQL script to create and populate the database:
   - Open `create_f1_database.sql` in MySQL Workbench
   - Execute the script to create the `f1dashboard` database and its tables

### Run the Application

1. Make sure MySQL Server is running
2. Update database credentials if needed:
   - Open `F1DatabaseManager.java`
   - Update the connection details (URL, username, password) in `initDatabaseConfig()` method
   - Default configuration:
     - URL: `jdbc:mysql://localhost:3306/f1dashboard`
     - Username: `root`
     - Password: `""` (empty password)

3. Run the application using the provided script:
   ```bash
   chmod +x run_dashboard.sh
   ./run_dashboard.sh
   ```
   
   The script will:
   - Download the required MySQL JDBC driver (if not present)
   - Compile the Java files
   - Run the F1 Dashboard application

## Using the Dashboard

1. **Historical Comparison Panel**:
   - Select seasons and races to compare
   - Choose drivers to analyze their performance
   - View lap-by-lap data and position changes

2. **Track Map Panel**:
   - See driver positions on circuit maps
   - Track race progress with animated position updates

## Database Schema

The application uses the following main tables:
- `seasons` - F1 seasons data
- `teams` - Team information
- `drivers` - Driver details with team associations
- `races` - Race information for each season
- `race_results` - Results for each driver in races
- `lap_data` - Detailed lap-by-lap telemetry data