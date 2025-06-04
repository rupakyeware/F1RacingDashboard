import java.sql.*;
import java.util.*;
import java.time.LocalDate;

/**
 * F1DatabaseManager - Handles database operations for F1 data
 */
public class F1DatabaseManager {
    private static final String DB_URL_ENV = "DATABASE_URL";
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    
    /**
     * Constructor for F1DatabaseManager
     */
    public F1DatabaseManager() {
        initDatabaseConfig();
    }
    
    /**
     * Initialize database connection properties from environment variables or use local MySQL
     */
    private void initDatabaseConfig() {
        try {
            // Use local MySQL configuration for local development
            this.dbUrl = "jdbc:mysql://localhost:3306/f1dashboard";
            this.dbUser = "root";  // Default MySQL username
            this.dbPassword = "1234"; // Your MySQL password
            
            System.out.println("Database configuration initialized for local MySQL.");
        } catch (Exception e) {
            System.err.println("Error initializing database configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get a database connection
     */
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found", e);
        }
    }
    
    /**
     * Test database connection
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get races for a specific season
     */
    public List<Map<String, Object>> getRacesForSeason(int year) {
        List<Map<String, Object>> races = new ArrayList<>();
        
        try {
            String sql = "SELECT r.id, r.name, r.circuit_name, r.date, r.country, r.round_number " +
                         "FROM races r " +
                         "JOIN seasons s ON r.season_id = s.id " +
                         "WHERE s.year = ? " +
                         "ORDER BY r.round_number";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, year);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    Map<String, Object> race = new HashMap<>();
                    race.put("id", rs.getInt("id"));
                    race.put("name", rs.getString("name"));
                    race.put("circuit_name", rs.getString("circuit_name"));
                    race.put("date", rs.getDate("date").toLocalDate());
                    race.put("country", rs.getString("country"));
                    race.put("round", rs.getInt("round_number"));
                    
                    races.add(race);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting races for season: " + e.getMessage());
        }
        
        return races;
    }
    
    /**
     * Get race ID by name and season
     */
    public int getRaceId(String raceName, int season) {
        try {
            String sql = "SELECT r.id FROM races r " +
                         "JOIN seasons s ON r.season_id = s.id " +
                         "WHERE r.name = ? AND s.year = ?";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, raceName);
                stmt.setInt(2, season);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting race ID: " + e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * Get race by ID
     */
    public Map<String, Object> getRaceById(int raceId) {
        try {
            String sql = "SELECT r.id, r.name, r.circuit_name, r.date, r.country, r.round_number, s.year " +
                         "FROM races r " +
                         "JOIN seasons s ON r.season_id = s.id " +
                         "WHERE r.id = ?";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, raceId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    Map<String, Object> race = new HashMap<>();
                    race.put("id", rs.getInt("id"));
                    race.put("name", rs.getString("name"));
                    race.put("circuit_name", rs.getString("circuit_name"));
                    race.put("date", rs.getDate("date").toLocalDate());
                    race.put("country", rs.getString("country"));
                    race.put("round", rs.getInt("round_number"));
                    race.put("year", rs.getInt("year"));
                    
                    return race;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting race by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get drivers for a specific race
     */
    public List<Map<String, Object>> getDriversForRace(int raceId) {
        List<Map<String, Object>> drivers = new ArrayList<>();
        
        try {
            String sql = "SELECT d.id, d.driver_number, d.abbreviation, d.full_name, t.name as team_name " +
                         "FROM race_results rr " +
                         "JOIN drivers d ON rr.driver_id = d.id " +
                         "JOIN teams t ON d.team_id = t.id " +
                         "WHERE rr.race_id = ? " +
                         "ORDER BY rr.position";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, raceId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    Map<String, Object> driver = new HashMap<>();
                    driver.put("id", rs.getInt("id"));
                    driver.put("number", rs.getInt("driver_number"));
                    driver.put("abbreviation", rs.getString("abbreviation"));
                    driver.put("name", rs.getString("full_name"));
                    driver.put("team", rs.getString("team_name"));
                    
                    drivers.add(driver);
                }
            }
            
            // If no race results yet, fall back to just getting drivers with non-race-specific data
            if (drivers.isEmpty()) {
                String fallbackSql = "SELECT d.id, d.driver_number, d.abbreviation, d.full_name, t.name as team_name " +
                                     "FROM drivers d " +
                                     "JOIN teams t ON d.team_id = t.id " +
                                     "ORDER BY d.driver_number";
                
                try (Connection conn = getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(fallbackSql)) {
                    
                    while (rs.next()) {
                        Map<String, Object> driver = new HashMap<>();
                        driver.put("id", rs.getInt("id"));
                        driver.put("number", rs.getInt("driver_number"));
                        driver.put("abbreviation", rs.getString("abbreviation"));
                        driver.put("name", rs.getString("full_name"));
                        driver.put("team", rs.getString("team_name"));
                        
                        drivers.add(driver);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting drivers for race: " + e.getMessage());
        }
        
        return drivers;
    }
    
    /**
     * Get driver ID by name
     */
    public int getDriverId(String driverName) {
        try {
            String sql = "SELECT id FROM drivers WHERE full_name = ?";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, driverName);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting driver ID: " + e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * Get available seasons from the database
     */
    public List<String> getAvailableSeasons() {
        List<String> seasons = new ArrayList<>();
        String sql = "SELECT year FROM seasons ORDER BY year DESC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                seasons.add(String.valueOf(rs.getInt("year")));
            }
        } catch (SQLException e) {
            System.err.println("Error getting available seasons: " + e.getMessage());
        }
        
        return seasons;
    }
    
    /**
     * Get race data for a specific driver
     */
    public Map<String, Object> getDriverRaceData(int driverId, int raceId) {
        Map<String, Object> data = new HashMap<>();
        
        try {
            // Get basic race and driver information
            String baseSql = "SELECT d.full_name as driver_name, d.driver_number, t.name as team_name, " +
                            "r.name as race_name, s.year as season, " +
                            "rr.position, rr.points, rr.grid_position, rr.laps_completed " +
                            "FROM race_results rr " +
                            "JOIN drivers d ON rr.driver_id = d.id " +
                            "JOIN teams t ON d.team_id = t.id " +
                            "JOIN races r ON rr.race_id = r.id " +
                            "JOIN seasons s ON r.season_id = s.id " +
                            "WHERE rr.driver_id = ? AND rr.race_id = ?";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(baseSql)) {
                stmt.setInt(1, driverId);
                stmt.setInt(2, raceId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    data.put("driver", rs.getString("driver_name"));
                    data.put("driver_number", rs.getInt("driver_number"));
                    data.put("team", rs.getString("team_name"));
                    data.put("race", rs.getString("race_name"));
                    data.put("season", String.valueOf(rs.getInt("season")));
                    data.put("position", rs.getInt("position"));
                    data.put("points", rs.getDouble("points"));
                    data.put("grid_position", rs.getInt("grid_position"));
                    data.put("laps_completed", rs.getInt("laps_completed"));
                    
                    // Get additional performance data
                    getLapDataForDriver(conn, data, driverId, raceId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting driver race data: " + e.getMessage());
        }
        
        return data;
    }
    
    /**
     * Get lap data for a driver in a race
     */
    private void getLapDataForDriver(Connection conn, Map<String, Object> data, int driverId, int raceId) throws SQLException {
        String lapSql = "SELECT lap_number, position, lap_time, sector1_time, sector2_time, sector3_time, speed " +
                        "FROM lap_data " +
                        "WHERE driver_id = ? AND race_id = ? " +
                        "ORDER BY lap_number";
        
        try (PreparedStatement stmt = conn.prepareStatement(lapSql)) {
            stmt.setInt(1, driverId);
            stmt.setInt(2, raceId);
            ResultSet rs = stmt.executeQuery();
            
            List<Map<String, Object>> laps = new ArrayList<>();
            double bestLapTime = Double.MAX_VALUE;
            String bestLapTimeStr = "";
            double totalSpeed = 0;
            int lapCount = 0;
            double maxSpeed = 0;
            
            while (rs.next()) {
                Map<String, Object> lap = new HashMap<>();
                lap.put("lap", rs.getInt("lap_number"));
                lap.put("position", rs.getInt("position"));
                
                String lapTimeStr = rs.getString("lap_time");
                lap.put("time", lapTimeStr);
                
                lap.put("sector_1", rs.getString("sector1_time"));
                lap.put("sector_2", rs.getString("sector2_time"));
                lap.put("sector_3", rs.getString("sector3_time"));
                
                double speed = rs.getDouble("speed");
                lap.put("speed", speed);
                
                laps.add(lap);
                
                // Calculate statistics
                double lapTimeSeconds = parseLapTimeToSeconds(lapTimeStr);
                if (lapTimeSeconds > 0 && lapTimeSeconds < bestLapTime) {
                    bestLapTime = lapTimeSeconds;
                    bestLapTimeStr = lapTimeStr;
                }
                
                totalSpeed += speed;
                lapCount++;
                
                if (speed > maxSpeed) {
                    maxSpeed = speed;
                }
            }
            
            // Add lap data and statistics to the main data map
            data.put("laps", laps);
            data.put("best_lap_time", bestLapTimeStr);
            data.put("avg_speed", lapCount > 0 ? Math.round((totalSpeed / lapCount) * 100) / 100.0 : 0);
            data.put("max_speed", maxSpeed);
            
            // Additional analytics like pit stops
            data.put("pit_stops", calculatePitStops(laps));
        }
    }
    
    /**
     * Parse lap time string to seconds
     */
    private double parseLapTimeToSeconds(String lapTimeStr) {
        if (lapTimeStr == null || lapTimeStr.isEmpty()) {
            return 0;
        }
        
        try {
            // Format can be "MM:SS.mmm" or "SS.mmm"
            String[] parts = lapTimeStr.split(":");
            
            if (parts.length == 2) {
                // Format is "MM:SS.mmm"
                int minutes = Integer.parseInt(parts[0]);
                double seconds = Double.parseDouble(parts[1]);
                return minutes * 60 + seconds;
            } else {
                // Format is "SS.mmm"
                return Double.parseDouble(lapTimeStr);
            }
        } catch (Exception e) {
            System.err.println("Error parsing lap time " + lapTimeStr + ": " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Calculate number of pit stops from lap data
     */
    private int calculatePitStops(List<Map<String, Object>> laps) {
        if (laps.size() < 2) {
            return 0;
        }
        
        int pitStops = 0;
        for (int i = 1; i < laps.size(); i++) {
            Map<String, Object> prevLap = laps.get(i - 1);
            Map<String, Object> currentLap = laps.get(i);
            
            double prevLapTime = parseLapTimeToSeconds((String) prevLap.get("time"));
            double currentLapTime = parseLapTimeToSeconds((String) currentLap.get("time"));
            
            // If lap time increases significantly, it's likely a pit stop
            if (currentLapTime > prevLapTime * 1.3 && currentLapTime > prevLapTime + 5) {
                pitStops++;
            }
        }
        
        return pitStops;
    }
    
    /**
     * Initialize the database with schema and sample data
     */
    public void initializeDatabase() {
        try {
            createTables();
            insertSampleData();
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
    
    /**
     * Create the database tables
     */
    private void createTables() throws SQLException {
        String[] createTableStatements = {
            // Seasons table
            "CREATE TABLE IF NOT EXISTS seasons (" +
            "    id SERIAL PRIMARY KEY," +
            "    year INTEGER NOT NULL UNIQUE," +
            "    name VARCHAR(100) NOT NULL" +
            ")",
            
            // Races table
            "CREATE TABLE IF NOT EXISTS races (" +
            "    id SERIAL PRIMARY KEY," +
            "    season_id INTEGER REFERENCES seasons(id)," +
            "    name VARCHAR(100) NOT NULL," +
            "    circuit_name VARCHAR(100) NOT NULL," +
            "    date DATE NOT NULL," +
            "    country VARCHAR(50) NOT NULL," +
            "    city VARCHAR(50)," +
            "    round_number INTEGER NOT NULL," +
            "    UNIQUE(season_id, name)" +
            ")",
            
            // Teams table
            "CREATE TABLE IF NOT EXISTS teams (" +
            "    id SERIAL PRIMARY KEY," +
            "    name VARCHAR(100) NOT NULL UNIQUE," +
            "    full_name VARCHAR(200) NOT NULL," +
            "    nationality VARCHAR(50)," +
            "    base VARCHAR(100)" +
            ")",
            
            // Drivers table
            "CREATE TABLE IF NOT EXISTS drivers (" +
            "    id SERIAL PRIMARY KEY," +
            "    driver_number INTEGER NOT NULL," +
            "    abbreviation VARCHAR(3) NOT NULL," +
            "    first_name VARCHAR(50) NOT NULL," +
            "    last_name VARCHAR(50) NOT NULL," +
            "    full_name VARCHAR(100) NOT NULL," +
            "    team_id INTEGER REFERENCES teams(id)," +
            "    UNIQUE(driver_number, team_id)" +
            ")",
            
            // Race results table
            "CREATE TABLE IF NOT EXISTS race_results (" +
            "    id SERIAL PRIMARY KEY," +
            "    race_id INTEGER REFERENCES races(id)," +
            "    driver_id INTEGER REFERENCES drivers(id)," +
            "    position INTEGER," +
            "    points DECIMAL(5,2)," +
            "    grid_position INTEGER," +
            "    status VARCHAR(50)," +
            "    laps_completed INTEGER," +
            "    UNIQUE(race_id, driver_id)" +
            ")",
            
            // Lap data table
            "CREATE TABLE IF NOT EXISTS lap_data (" +
            "    id SERIAL PRIMARY KEY," +
            "    race_id INTEGER REFERENCES races(id)," +
            "    driver_id INTEGER REFERENCES drivers(id)," +
            "    lap_number INTEGER NOT NULL," +
            "    position INTEGER," +
            "    lap_time VARCHAR(20)," +
            "    sector1_time VARCHAR(20)," +
            "    sector2_time VARCHAR(20)," +
            "    sector3_time VARCHAR(20)," +
            "    speed DECIMAL(6,3)," +
            "    UNIQUE(race_id, driver_id, lap_number)" +
            ")"
        };
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String sql : createTableStatements) {
                stmt.execute(sql);
            }
        }
    }
    
    /**
     * Insert sample data
     */
    private void insertSampleData() throws SQLException {
        insertSeasons();
        insertTeams();
        insertRaces();
        insertDrivers();
        insertSampleResults();
    }
    
    /**
     * Insert seasons
     */
    private void insertSeasons() throws SQLException {
        String sql = "INSERT INTO seasons (year, name) VALUES (?, ?) " +
                     "ON CONFLICT (year) DO NOTHING";
        
        int[][] seasons = {
            {2020, 2020},
            {2021, 2021},
            {2022, 2022},
            {2023, 2023}
        };
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (int[] season : seasons) {
                stmt.setInt(1, season[0]);
                stmt.setString(2, season[0] + " Formula 1 Season");
                stmt.executeUpdate();
            }
        }
    }
    
    /**
     * Insert teams
     */
    private void insertTeams() throws SQLException {
        String sql = "INSERT INTO teams (name, full_name, nationality, base) VALUES (?, ?, ?, ?) " +
                     "ON CONFLICT (name) DO NOTHING";
        
        String[][] teams = {
            {"Mercedes", "Mercedes-AMG Petronas F1 Team", "German", "Brackley, United Kingdom"},
            {"Red Bull Racing", "Red Bull Racing", "Austrian", "Milton Keynes, United Kingdom"},
            {"Ferrari", "Scuderia Ferrari", "Italian", "Maranello, Italy"},
            {"McLaren", "McLaren F1 Team", "British", "Woking, United Kingdom"},
            {"Alpine", "Alpine F1 Team", "French", "Enstone, United Kingdom"},
            {"AlphaTauri", "Scuderia AlphaTauri", "Italian", "Faenza, Italy"},
            {"Aston Martin", "Aston Martin Aramco Cognizant F1 Team", "British", "Silverstone, United Kingdom"},
            {"Williams", "Williams Racing", "British", "Grove, United Kingdom"},
            {"Alfa Romeo", "Alfa Romeo F1 Team ORLEN", "Swiss", "Hinwil, Switzerland"},
            {"Haas F1 Team", "Haas F1 Team", "American", "Kannapolis, United States"}
        };
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (String[] team : teams) {
                stmt.setString(1, team[0]);
                stmt.setString(2, team[1]);
                stmt.setString(3, team[2]);
                stmt.setString(4, team[3]);
                stmt.executeUpdate();
            }
        }
    }
    
    /**
     * Insert races
     */
    private void insertRaces() throws SQLException {
        String sql = "INSERT INTO races (season_id, name, circuit_name, date, country, city, round_number) " +
                     "VALUES ((SELECT id FROM seasons WHERE year = ?), ?, ?, ?, ?, ?, ?)";
        
        Object[][] races2023 = {
            {2023, "2023 Bahrain Grand Prix", "Bahrain International Circuit", "2023-03-05", "Bahrain", "Sakhir", 1},
            {2023, "2023 Saudi Arabian Grand Prix", "Jeddah Corniche Circuit", "2023-03-19", "Saudi Arabia", "Jeddah", 2},
            {2023, "2023 Australian Grand Prix", "Albert Park Circuit", "2023-04-02", "Australia", "Melbourne", 3},
            {2023, "2023 Azerbaijan Grand Prix", "Baku City Circuit", "2023-04-30", "Azerbaijan", "Baku", 4},
            {2023, "2023 Miami Grand Prix", "Miami International Autodrome", "2023-05-07", "United States", "Miami", 5}
        };
        
        Object[][] races2022 = {
            {2022, "2022 Bahrain Grand Prix", "Bahrain International Circuit", "2022-03-20", "Bahrain", "Sakhir", 1},
            {2022, "2022 Saudi Arabian Grand Prix", "Jeddah Corniche Circuit", "2022-03-27", "Saudi Arabia", "Jeddah", 2},
            {2022, "2022 Australian Grand Prix", "Albert Park Circuit", "2022-04-10", "Australia", "Melbourne", 3},
            {2022, "2022 Emilia Romagna Grand Prix", "Autodromo Enzo e Dino Ferrari", "2022-04-24", "Italy", "Imola", 4},
            {2022, "2022 Miami Grand Prix", "Miami International Autodrome", "2022-05-08", "United States", "Miami", 5}
        };
        
        Object[][] races2021 = {
            {2021, "2021 Bahrain Grand Prix", "Bahrain International Circuit", "2021-03-28", "Bahrain", "Sakhir", 1},
            {2021, "2021 Emilia Romagna Grand Prix", "Autodromo Enzo e Dino Ferrari", "2021-04-18", "Italy", "Imola", 2},
            {2021, "2021 Portuguese Grand Prix", "Algarve International Circuit", "2021-05-02", "Portugal", "Portimão", 3},
            {2021, "2021 Spanish Grand Prix", "Circuit de Barcelona-Catalunya", "2021-05-09", "Spain", "Barcelona", 4},
            {2021, "2021 Monaco Grand Prix", "Circuit de Monaco", "2021-05-23", "Monaco", "Monte Carlo", 5}
        };
        
        Object[][] races2020 = {
            {2020, "2020 Austrian Grand Prix", "Red Bull Ring", "2020-07-05", "Austria", "Spielberg", 1},
            {2020, "2020 Styrian Grand Prix", "Red Bull Ring", "2020-07-12", "Austria", "Spielberg", 2},
            {2020, "2020 Hungarian Grand Prix", "Hungaroring", "2020-07-19", "Hungary", "Budapest", 3},
            {2020, "2020 British Grand Prix", "Silverstone Circuit", "2020-08-02", "United Kingdom", "Silverstone", 4},
            {2020, "2020 70th Anniversary Grand Prix", "Silverstone Circuit", "2020-08-09", "United Kingdom", "Silverstone", 5}
        };
        
        List<Object[][]> allRaces = Arrays.asList(races2020, races2021, races2022, races2023);
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (Object[][] yearRaces : allRaces) {
                for (Object[] race : yearRaces) {
                    stmt.setInt(1, (Integer) race[0]);
                    stmt.setString(2, (String) race[1]);
                    stmt.setString(3, (String) race[2]);
                    stmt.setDate(4, java.sql.Date.valueOf((String) race[3]));
                    stmt.setString(5, (String) race[4]);
                    stmt.setString(6, (String) race[5]);
                    stmt.setInt(7, (Integer) race[6]);
                    stmt.executeUpdate();
                }
            }
        }
    }
    
    /**
     * Insert drivers
     */
    private void insertDrivers() throws SQLException {
        String sql = "INSERT INTO drivers (driver_number, abbreviation, first_name, last_name, full_name, team_id) " +
                     "VALUES (?, ?, ?, ?, ?, (SELECT id FROM teams WHERE name = ?))";
        
        Object[][] drivers = {
            {44, "HAM", "Lewis", "Hamilton", "Lewis Hamilton", "Mercedes"},
            {63, "RUS", "George", "Russell", "George Russell", "Mercedes"},
            {1, "VER", "Max", "Verstappen", "Max Verstappen", "Red Bull Racing"},
            {11, "PER", "Sergio", "Perez", "Sergio Perez", "Red Bull Racing"},
            {16, "LEC", "Charles", "Leclerc", "Charles Leclerc", "Ferrari"},
            {55, "SAI", "Carlos", "Sainz", "Carlos Sainz", "Ferrari"},
            {4, "NOR", "Lando", "Norris", "Lando Norris", "McLaren"},
            {3, "RIC", "Daniel", "Ricciardo", "Daniel Ricciardo", "McLaren"},
            {14, "ALO", "Fernando", "Alonso", "Fernando Alonso", "Alpine"},
            {31, "OCO", "Esteban", "Ocon", "Esteban Ocon", "Alpine"},
            {10, "GAS", "Pierre", "Gasly", "Pierre Gasly", "AlphaTauri"},
            {22, "TSU", "Yuki", "Tsunoda", "Yuki Tsunoda", "AlphaTauri"},
            {5, "VET", "Sebastian", "Vettel", "Sebastian Vettel", "Aston Martin"},
            {18, "STR", "Lance", "Stroll", "Lance Stroll", "Aston Martin"},
            {6, "LAT", "Nicholas", "Latifi", "Nicholas Latifi", "Williams"},
            {23, "ALB", "Alexander", "Albon", "Alexander Albon", "Williams"},
            {77, "BOT", "Valtteri", "Bottas", "Valtteri Bottas", "Alfa Romeo"},
            {24, "ZHO", "Guanyu", "Zhou", "Guanyu Zhou", "Alfa Romeo"},
            {47, "MSC", "Mick", "Schumacher", "Mick Schumacher", "Haas F1 Team"},
            {20, "MAG", "Kevin", "Magnussen", "Kevin Magnussen", "Haas F1 Team"}
        };
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (Object[] driver : drivers) {
                stmt.setInt(1, (Integer) driver[0]);
                stmt.setString(2, (String) driver[1]);
                stmt.setString(3, (String) driver[2]);
                stmt.setString(4, (String) driver[3]);
                stmt.setString(5, (String) driver[4]);
                stmt.setString(6, (String) driver[5]);
                
                try {
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    // Skip if already exists or other constraint violation
                    System.out.println("Skipping driver: " + driver[4] + " - " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Insert sample race results
     */
    private void insertSampleResults() throws SQLException {
        // For simplicity, just generate some sample results for the first race of 2023
        try {
            int raceId = getRaceId("2023 Bahrain Grand Prix", 2023);
            if (raceId < 0) {
                System.out.println("Race not found for sample results");
                return;
            }
            
            List<Map<String, Object>> drivers = getDriversForRace(raceId);
            if (drivers.isEmpty()) {
                // Get drivers directly since race doesn't have data yet
                String sql = "SELECT d.id, d.driver_number FROM drivers d";
                
                try (Connection conn = getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    
                    while (rs.next()) {
                        Map<String, Object> driver = new HashMap<>();
                        driver.put("id", rs.getInt("id"));
                        driver.put("number", rs.getInt("driver_number"));
                        drivers.add(driver);
                    }
                }
            }
            
            // Generate sample results
            List<Integer> positions = new ArrayList<>();
            for (int i = 1; i <= drivers.size(); i++) {
                positions.add(i);
            }
            Collections.shuffle(positions);
            
            String resultSql = "INSERT INTO race_results (race_id, driver_id, position, points, grid_position, " +
                              "status, laps_completed) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(resultSql)) {
                
                for (int i = 0; i < drivers.size(); i++) {
                    int driverId = (int) drivers.get(i).get("id");
                    int position = positions.get(i);
                    double points = getPointsForPosition(position);
                    int gridPosition = (int) (Math.random() * drivers.size()) + 1;
                    String status = position <= drivers.size() - 2 ? "Finished" : "DNF";
                    int lapsCompleted = status.equals("Finished") ? 57 : (int) (Math.random() * 40) + 10;
                    
                    stmt.setInt(1, raceId);
                    stmt.setInt(2, driverId);
                    stmt.setInt(3, position);
                    stmt.setDouble(4, points);
                    stmt.setInt(5, gridPosition);
                    stmt.setString(6, status);
                    stmt.setInt(7, lapsCompleted);
                    
                    stmt.executeUpdate();
                }
            }
            
            // Generate sample lap data for the first few drivers
            for (int i = 0; i < Math.min(5, drivers.size()); i++) {
                int driverId = (int) drivers.get(i).get("id");
                generateSampleLapData(raceId, driverId, positions.get(i));
            }
            
        } catch (SQLException e) {
            System.err.println("Error inserting sample results: " + e.getMessage());
        }
    }
    
    /**
     * Get points for a position
     */
    private double getPointsForPosition(int position) {
        switch (position) {
            case 1: return 25;
            case 2: return 18;
            case 3: return 15;
            case 4: return 12;
            case 5: return 10;
            case 6: return 8;
            case 7: return 6;
            case 8: return 4;
            case 9: return 2;
            case 10: return 1;
            default: return 0;
        }
    }
    
    /**
     * Generate sample lap data for a driver
     */
    private void generateSampleLapData(int raceId, int driverId, int finalPosition) throws SQLException {
        String sql = "INSERT INTO lap_data (race_id, driver_id, lap_number, position, " +
                     "lap_time, sector1_time, sector2_time, sector3_time, speed) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            double baseSpeed = 200 + Math.random() * 20;
            double baseLapTime = 90.0 + Math.random() * 5.0; // Base lap time in seconds
            
            for (int lap = 1; lap <= 57; lap++) {
                int position = calculateLapPosition(lap, finalPosition);
                
                // Vary lap time by fuel load, tire wear, etc.
                double fuelEffect = -0.05 * lap; // Faster as fuel burns off
                double tireEffect = 0.02 * lap; // Slower as tires wear
                double randomVariation = (Math.random() - 0.5) * 2.0; // Random variation
                
                // Reset tire effect after pit stops
                if (lap == 18 || lap == 38) {
                    tireEffect = -0.5; // Fresh tires
                }
                
                double adjustedLapTime = baseLapTime + fuelEffect + tireEffect + randomVariation;
                String lapTimeStr = formatLapTime(adjustedLapTime);
                
                // Generate sector times that add up to lap time
                double sector1Percent = 0.3 + (Math.random() - 0.5) * 0.05;
                double sector2Percent = 0.4 + (Math.random() - 0.5) * 0.05;
                double sector3Percent = 1.0 - sector1Percent - sector2Percent;
                
                String sector1Time = formatLapTime(adjustedLapTime * sector1Percent);
                String sector2Time = formatLapTime(adjustedLapTime * sector2Percent);
                String sector3Time = formatLapTime(adjustedLapTime * sector3Percent);
                
                // Speed varies slightly each lap
                double speed = baseSpeed + (Math.random() - 0.5) * 10;
                
                stmt.setInt(1, raceId);
                stmt.setInt(2, driverId);
                stmt.setInt(3, lap);
                stmt.setInt(4, position);
                stmt.setString(5, lapTimeStr);
                stmt.setString(6, sector1Time);
                stmt.setString(7, sector2Time);
                stmt.setString(8, sector3Time);
                stmt.setDouble(9, speed);
                
                stmt.executeUpdate();
            }
        }
    }
    
    /**
     * Calculate position on a specific lap
     */
    private int calculateLapPosition(int lap, int finalPosition) {
        // Simulate some position changes during the race
        int range = Math.min(5, Math.abs(finalPosition - 10));
        
        if (lap < 10) {
            // Early race - more variation
            return finalPosition + (int)((Math.random() - 0.3) * range);
        } else if (lap < 40) {
            // Mid race - settling into position
            return finalPosition + (int)((Math.random() - 0.5) * (range / 2));
        } else {
            // End race - close to final position
            return finalPosition + (int)((Math.random() - 0.7) * (range / 3));
        }
    }
    
    /**
     * Format lap time as "M:SS.mmm"
     */
    private String formatLapTime(double seconds) {
        int minutes = (int) (seconds / 60);
        double remainingSeconds = seconds % 60;
        return String.format("%d:%05.3f", minutes, remainingSeconds);
    }
    
    /**
     * Main method to test the database connection and setup
     */
    public static void main(String[] args) {
        F1DatabaseManager dbManager = new F1DatabaseManager();
        
        if (dbManager.testConnection()) {
            System.out.println("Database connection successful!");
            dbManager.initializeDatabase();
            
            // Test fetching data
            List<Map<String, Object>> races = dbManager.getRacesForSeason(2023);
            System.out.println("Races for 2023: " + races.size());
            
            if (!races.isEmpty()) {
                int raceId = (int) races.get(0).get("id");
                List<Map<String, Object>> drivers = dbManager.getDriversForRace(raceId);
                System.out.println("Drivers for race ID " + raceId + ": " + drivers.size());
                
                if (!drivers.isEmpty()) {
                    int driverId = (int) drivers.get(0).get("id");
                    Map<String, Object> driverData = dbManager.getDriverRaceData(driverId, raceId);
                    System.out.println("Driver race data: " + driverData);
                }
            }
        } else {
            System.err.println("Failed to connect to the database.");
        }
    }
}