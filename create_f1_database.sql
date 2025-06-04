-- Create the f1dashboard database if it doesn't exist
CREATE DATABASE IF NOT EXISTS f1dashboard;

-- Use the f1dashboard database
USE f1dashboard;

-- Create seasons table
CREATE TABLE IF NOT EXISTS seasons (
    id INT AUTO_INCREMENT PRIMARY KEY,
    year INT NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL
);

-- Create teams table
CREATE TABLE IF NOT EXISTS teams (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    nationality VARCHAR(100),
    base VARCHAR(255)
);

-- Create races table
CREATE TABLE IF NOT EXISTS races (
    id INT AUTO_INCREMENT PRIMARY KEY,
    season_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    circuit_name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    country VARCHAR(100),
    city VARCHAR(100),
    round_number INT NOT NULL,
    UNIQUE KEY season_race (season_id, name),
    FOREIGN KEY (season_id) REFERENCES seasons(id)
);

-- Create drivers table
CREATE TABLE IF NOT EXISTS drivers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    driver_number INT NOT NULL,
    abbreviation VARCHAR(3),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    full_name VARCHAR(200) NOT NULL,
    team_id INT NOT NULL,
    UNIQUE KEY driver_team (driver_number, team_id),
    FOREIGN KEY (team_id) REFERENCES teams(id)
);

-- Create race_results table
CREATE TABLE IF NOT EXISTS race_results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    race_id INT NOT NULL,
    driver_id INT NOT NULL,
    position INT,
    points DECIMAL(5,2),
    grid_position INT,
    status VARCHAR(100),
    laps_completed INT,
    UNIQUE KEY race_driver (race_id, driver_id),
    FOREIGN KEY (race_id) REFERENCES races(id),
    FOREIGN KEY (driver_id) REFERENCES drivers(id)
);

-- Create lap_data table
CREATE TABLE IF NOT EXISTS lap_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    race_id INT NOT NULL,
    driver_id INT NOT NULL,
    lap_number INT NOT NULL,
    position INT,
    lap_time VARCHAR(20),
    sector1_time VARCHAR(20),
    sector2_time VARCHAR(20),
    sector3_time VARCHAR(20),
    speed DECIMAL(6,3),
    UNIQUE KEY race_driver_lap (race_id, driver_id, lap_number),
    FOREIGN KEY (race_id) REFERENCES races(id),
    FOREIGN KEY (driver_id) REFERENCES drivers(id)
);

-- Insert sample seasons data
INSERT INTO seasons (year, name) VALUES 
(2020, '2020 Formula 1 Season'),
(2021, '2021 Formula 1 Season'),
(2022, '2022 Formula 1 Season'),
(2023, '2023 Formula 1 Season');

-- Insert sample teams data
INSERT INTO teams (name, full_name, nationality, base) VALUES 
('Mercedes', 'Mercedes-AMG Petronas F1 Team', 'German', 'Brackley, United Kingdom'),
('Red Bull Racing', 'Red Bull Racing', 'Austrian', 'Milton Keynes, United Kingdom'),
('Ferrari', 'Scuderia Ferrari', 'Italian', 'Maranello, Italy'),
('McLaren', 'McLaren F1 Team', 'British', 'Woking, United Kingdom'),
('Alpine', 'Alpine F1 Team', 'French', 'Enstone, United Kingdom'),
('AlphaTauri', 'Scuderia AlphaTauri', 'Italian', 'Faenza, Italy'),
('Aston Martin', 'Aston Martin Aramco Cognizant F1 Team', 'British', 'Silverstone, United Kingdom'),
('Williams', 'Williams Racing', 'British', 'Grove, United Kingdom'),
('Alfa Romeo', 'Alfa Romeo F1 Team ORLEN', 'Swiss', 'Hinwil, Switzerland'),
('Haas F1 Team', 'Haas F1 Team', 'American', 'Kannapolis, United States');

-- Insert sample drivers data
INSERT INTO drivers (driver_number, abbreviation, first_name, last_name, full_name, team_id) VALUES 
(44, 'HAM', 'Lewis', 'Hamilton', 'Lewis Hamilton', 1),
(63, 'RUS', 'George', 'Russell', 'George Russell', 1),
(1, 'VER', 'Max', 'Verstappen', 'Max Verstappen', 2),
(11, 'PER', 'Sergio', 'Perez', 'Sergio Perez', 2),
(16, 'LEC', 'Charles', 'Leclerc', 'Charles Leclerc', 3),
(55, 'SAI', 'Carlos', 'Sainz', 'Carlos Sainz', 3),
(4, 'NOR', 'Lando', 'Norris', 'Lando Norris', 4),
(3, 'RIC', 'Daniel', 'Ricciardo', 'Daniel Ricciardo', 4),
(14, 'ALO', 'Fernando', 'Alonso', 'Fernando Alonso', 5),
(31, 'OCO', 'Esteban', 'Ocon', 'Esteban Ocon', 5),
(10, 'GAS', 'Pierre', 'Gasly', 'Pierre Gasly', 6),
(22, 'TSU', 'Yuki', 'Tsunoda', 'Yuki Tsunoda', 6),
(5, 'VET', 'Sebastian', 'Vettel', 'Sebastian Vettel', 7),
(18, 'STR', 'Lance', 'Stroll', 'Lance Stroll', 7),
(6, 'LAT', 'Nicholas', 'Latifi', 'Nicholas Latifi', 8),
(23, 'ALB', 'Alexander', 'Albon', 'Alexander Albon', 8),
(77, 'BOT', 'Valtteri', 'Bottas', 'Valtteri Bottas', 9),
(24, 'ZHO', 'Guanyu', 'Zhou', 'Guanyu Zhou', 9),
(47, 'MSC', 'Mick', 'Schumacher', 'Mick Schumacher', 10),
(20, 'MAG', 'Kevin', 'Magnussen', 'Kevin Magnussen', 10);

-- Insert sample races for 2023
INSERT INTO races (season_id, name, circuit_name, date, country, city, round_number) VALUES 
((SELECT id FROM seasons WHERE year = 2023), '2023 Bahrain Grand Prix', 'Bahrain International Circuit', '2023-03-05', 'Bahrain', 'Sakhir', 1),
((SELECT id FROM seasons WHERE year = 2023), '2023 Saudi Arabian Grand Prix', 'Jeddah Corniche Circuit', '2023-03-19', 'Saudi Arabia', 'Jeddah', 2),
((SELECT id FROM seasons WHERE year = 2023), '2023 Australian Grand Prix', 'Albert Park Circuit', '2023-04-02', 'Australia', 'Melbourne', 3),
((SELECT id FROM seasons WHERE year = 2023), '2023 Azerbaijan Grand Prix', 'Baku City Circuit', '2023-04-30', 'Azerbaijan', 'Baku', 4),
((SELECT id FROM seasons WHERE year = 2023), '2023 Miami Grand Prix', 'Miami International Autodrome', '2023-05-07', 'United States', 'Miami', 5);

-- Insert sample races for 2022
INSERT INTO races (season_id, name, circuit_name, date, country, city, round_number) VALUES 
((SELECT id FROM seasons WHERE year = 2022), '2022 Bahrain Grand Prix', 'Bahrain International Circuit', '2022-03-20', 'Bahrain', 'Sakhir', 1),
((SELECT id FROM seasons WHERE year = 2022), '2022 Saudi Arabian Grand Prix', 'Jeddah Corniche Circuit', '2022-03-27', 'Saudi Arabia', 'Jeddah', 2),
((SELECT id FROM seasons WHERE year = 2022), '2022 Australian Grand Prix', 'Albert Park Circuit', '2022-04-10', 'Australia', 'Melbourne', 3),
((SELECT id FROM seasons WHERE year = 2022), '2022 Emilia Romagna Grand Prix', 'Autodromo Enzo e Dino Ferrari', '2022-04-24', 'Italy', 'Imola', 4),
((SELECT id FROM seasons WHERE year = 2022), '2022 Miami Grand Prix', 'Miami International Autodrome', '2022-05-08', 'United States', 'Miami', 5);

-- Insert sample race results for 2023 Bahrain Grand Prix
SET @race_id = (SELECT id FROM races WHERE name = '2023 Bahrain Grand Prix');

-- Max Verstappen (1st place)
INSERT INTO race_results (race_id, driver_id, position, points, grid_position, status, laps_completed) 
VALUES (@race_id, (SELECT id FROM drivers WHERE driver_number = 1), 1, 25, 1, 'Finished', 57);

-- Sergio Perez (2nd place)
INSERT INTO race_results (race_id, driver_id, position, points, grid_position, status, laps_completed) 
VALUES (@race_id, (SELECT id FROM drivers WHERE driver_number = 11), 2, 18, 2, 'Finished', 57);

-- Fernando Alonso (3rd place)
INSERT INTO race_results (race_id, driver_id, position, points, grid_position, status, laps_completed) 
VALUES (@race_id, (SELECT id FROM drivers WHERE driver_number = 14), 3, 15, 5, 'Finished', 57);

-- Carlos Sainz (4th place)
INSERT INTO race_results (race_id, driver_id, position, points, grid_position, status, laps_completed) 
VALUES (@race_id, (SELECT id FROM drivers WHERE driver_number = 55), 4, 12, 3, 'Finished', 57);

-- Lewis Hamilton (5th place)
INSERT INTO race_results (race_id, driver_id, position, points, grid_position, status, laps_completed) 
VALUES (@race_id, (SELECT id FROM drivers WHERE driver_number = 44), 5, 10, 7, 'Finished', 57);

-- Generate sample lap data for Max Verstappen
SET @driver_id = (SELECT id FROM drivers WHERE driver_number = 1);

-- Insert 10 sample laps for demonstration
INSERT INTO lap_data (race_id, driver_id, lap_number, position, lap_time, sector1_time, sector2_time, sector3_time, speed)
VALUES 
(@race_id, @driver_id, 1, 1, '1:35.245', '29.123', '38.657', '27.465', 212.345),
(@race_id, @driver_id, 2, 1, '1:34.876', '28.987', '38.532', '27.357', 213.678),
(@race_id, @driver_id, 3, 1, '1:34.562', '28.876', '38.421', '27.265', 214.123),
(@race_id, @driver_id, 4, 1, '1:34.321', '28.765', '38.312', '27.244', 214.567),
(@race_id, @driver_id, 5, 1, '1:34.123', '28.654', '38.256', '27.213', 214.982),
(@race_id, @driver_id, 6, 1, '1:33.987', '28.543', '38.234', '27.210', 215.345),
(@race_id, @driver_id, 7, 1, '1:33.876', '28.521', '38.189', '27.166', 215.687),
(@race_id, @driver_id, 8, 1, '1:33.765', '28.498', '38.145', '27.122', 215.912),
(@race_id, @driver_id, 9, 1, '1:33.654', '28.476', '38.089', '27.089', 216.234),
(@race_id, @driver_id, 10, 1, '1:33.543', '28.454', '38.023', '27.066', 216.567);

-- Generate sample lap data for Sergio Perez
SET @driver_id = (SELECT id FROM drivers WHERE driver_number = 11);

-- Insert 10 sample laps for demonstration
INSERT INTO lap_data (race_id, driver_id, lap_number, position, lap_time, sector1_time, sector2_time, sector3_time, speed)
VALUES 
(@race_id, @driver_id, 1, 2, '1:35.567', '29.223', '38.857', '27.487', 211.345),
(@race_id, @driver_id, 2, 2, '1:35.123', '29.087', '38.632', '27.404', 212.678),
(@race_id, @driver_id, 3, 2, '1:34.876', '28.976', '38.521', '27.379', 213.123),
(@race_id, @driver_id, 4, 2, '1:34.654', '28.865', '38.432', '27.357', 213.567),
(@race_id, @driver_id, 5, 2, '1:34.432', '28.754', '38.356', '27.322', 213.982),
(@race_id, @driver_id, 6, 2, '1:34.321', '28.643', '38.334', '27.344', 214.345),
(@race_id, @driver_id, 7, 2, '1:34.123', '28.621', '38.289', '27.213', 214.687),
(@race_id, @driver_id, 8, 2, '1:34.023', '28.598', '38.245', '27.180', 214.912),
(@race_id, @driver_id, 9, 2, '1:33.934', '28.576', '38.189', '27.169', 215.234),
(@race_id, @driver_id, 10, 2, '1:33.845', '28.554', '38.123', '27.168', 215.567);