# F1 Dashboard Setup Instructions for Windows

## Prerequisites
- Java Development Kit (JDK) 8 or higher installed
- MySQL Server installed and running
- MySQL Workbench or another MySQL client

## Step 1: Setup MySQL Database

1. Open MySQL Workbench
2. Connect to your MySQL server using your credentials:
   - Username: `root`
   - Password: `1234`
3. Execute the `create_f1_database.sql` script to create the database and sample data:
   - Open the file in MySQL Workbench
   - Click the lightning bolt button to execute the script
   - Verify that the `f1dashboard` database and tables were created

## Step 2: Run the Application

1. Open Command Prompt and navigate to the F1Dashboard directory:
   ```
   cd path\to\F1Dashboard
   ```

2. Run the application using the provided batch file:
   ```
   run_dashboard_windows.bat
   ```

   This script will:
   - Download any missing libraries (MySQL JDBC driver, JSON library)
   - Compile the Java source files
   - Run the F1 Dashboard application

## Troubleshooting

### Database Connection Issues
- Ensure MySQL server is running
- Verify your MySQL credentials match those in F1DatabaseManager.java:
  - Default username: `root`
  - Default password: `1234`
- Check if the `f1dashboard` database exists

### Compilation Errors
- Make sure JDK is installed and in your PATH
- Check that all Java source files are in the same directory
- Verify that library files are in the `lib` directory

### Runtime Errors
- Ensure MySQL JDBC driver and JSON library are correctly downloaded in the `lib` folder
- Check console output for specific error messages

## Features

The F1 Dashboard application provides:
- Historical race data comparison
- Track visualization with driver positions
- Lap-by-lap telemetry data
- Performance analytics

## Data Sources

The application uses sample F1 race data from 2020-2023 seasons, including:
- Race information
- Driver details
- Team information
- Lap timing data
- Performance metrics