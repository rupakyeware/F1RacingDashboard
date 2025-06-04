-- Create tables if not exist

-- Car Data Table
CREATE TABLE IF NOT EXISTS car_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brake INT NOT NULL,
    date TIMESTAMP WITH TIME ZONE,
    driver_number INT NOT NULL,
    drs INT NOT NULL,
    meeting_key INT NOT NULL,
    n_gear INT NOT NULL,
    rpm INT NOT NULL,
    session_key INT NOT NULL,
    speed INT NOT NULL,
    throttle INT NOT NULL,
    lap_number INT
);

-- Create indexes for car_data
CREATE INDEX IF NOT EXISTS idx_driver_session ON car_data(driver_number, session_key);
CREATE INDEX IF NOT EXISTS idx_date ON car_data(date);

-- Drivers Table
CREATE TABLE IF NOT EXISTS drivers (
    driver_number INT NOT NULL,
    session_key INT NOT NULL,
    meeting_key INT NOT NULL,
    broadcast_name VARCHAR(255),
    country_code VARCHAR(10),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    full_name VARCHAR(255),
    headshot_url VARCHAR(512),
    name_acronym VARCHAR(10),
    team_colour VARCHAR(10),
    team_name VARCHAR(255),
    PRIMARY KEY (driver_number, session_key)
);

-- Create indexes for drivers
CREATE INDEX IF NOT EXISTS idx_driver_meeting ON drivers(driver_number, meeting_key);
CREATE INDEX IF NOT EXISTS idx_session ON drivers(session_key);

-- Lap Data Table
CREATE TABLE IF NOT EXISTS lap_data (
    driver_number INT NOT NULL,
    session_key INT NOT NULL,
    lap_number INT NOT NULL,
    meeting_key INT NOT NULL,
    date_start TIMESTAMP WITH TIME ZONE,
    lap_duration DOUBLE,
    duration_sector_1 DOUBLE,
    duration_sector_2 DOUBLE,
    duration_sector_3 DOUBLE,
    i1_speed INT,
    i2_speed INT,
    st_speed INT,
    is_pit_out_lap BOOLEAN,
    segments_sector_1 CLOB,
    segments_sector_2 CLOB,
    segments_sector_3 CLOB,
    tyre_compound VARCHAR(20),
    PRIMARY KEY (driver_number, session_key, lap_number)
);

-- Create indexes for lap_data
CREATE INDEX IF NOT EXISTS idx_driver_session_lap ON lap_data(driver_number, session_key, lap_number);

-- Meetings Table
CREATE TABLE IF NOT EXISTS meetings (
    meeting_key INT PRIMARY KEY,
    meeting_name VARCHAR(255),
    meeting_official_name VARCHAR(512),
    date_start TIMESTAMP WITH TIME ZONE,
    location VARCHAR(255),
    country VARCHAR(255),
    circuit_key INT,
    circuit_name VARCHAR(255),
    circuit_short_name VARCHAR(100),
    year INT
);

-- Create indexes for meetings
CREATE INDEX IF NOT EXISTS idx_year ON meetings(year);

-- Intervals Table
CREATE TABLE IF NOT EXISTS intervals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date TIMESTAMP WITH TIME ZONE,
    driver_number INT NOT NULL,
    meeting_key INT NOT NULL,
    session_key INT NOT NULL,
    gap_to_leader DOUBLE,
    interval DOUBLE
);

-- Create indexes for intervals
CREATE INDEX IF NOT EXISTS idx_intervals_driver_session ON intervals(driver_number, session_key);
CREATE INDEX IF NOT EXISTS idx_intervals_date ON intervals(date);

-- Location Table
CREATE TABLE IF NOT EXISTS location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date TIMESTAMP WITH TIME ZONE,
    driver_number INT NOT NULL,
    meeting_key INT NOT NULL,
    session_key INT NOT NULL,
    x INT NOT NULL,
    y INT NOT NULL,
    z INT NOT NULL
);

-- Create indexes for location
CREATE INDEX IF NOT EXISTS idx_location_driver_session ON location(driver_number, session_key);
CREATE INDEX IF NOT EXISTS idx_location_date ON location(date);
