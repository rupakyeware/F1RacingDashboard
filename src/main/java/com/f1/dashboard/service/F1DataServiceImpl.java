package com.f1.dashboard.service;

import com.f1.dashboard.model.Driver;
import com.f1.dashboard.model.LapData;
import com.f1.dashboard.model.Meeting;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class F1DataServiceImpl implements F1DataService {

    // Mock data for development - will be replaced with API calls
    private final List<Meeting> meetings;
    private final List<Driver> drivers;
    private final List<LapData> lapData;

    public F1DataServiceImpl() {
        // Initialize mock data
        this.meetings = createSampleMeetings();
        this.drivers = createSampleDrivers();
        this.lapData = createSampleLapData();
    }

    @Override
    public List<Meeting> getLastFiveRaces() {
        return meetings;
    }

    @Override
    public List<Driver> getAllDrivers() {
        return drivers;
    }

    @Override
    public List<LapData> getAllLapData() {
        return lapData;
    }

    @Override
    public Driver getDriverById(String driverId) {
        return drivers.stream()
                .filter(driver -> driver.getDriverId().equals(driverId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<LapData> getLapDataForDriver(String driverId) {
        return lapData.stream()
                .filter(lap -> lap.getDriverId().equals(driverId))
                .collect(Collectors.toList());
    }

    // Create sample data for development
    private List<Meeting> createSampleMeetings() {
        List<Meeting> meetings = new ArrayList<>();
        
        meetings.add(new Meeting("BHR2025", "Bahrain Grand Prix", "Sakhir", "BH", 
                LocalDate.of(2025, 3, 22), 57, 5.412));
        
        meetings.add(new Meeting("SAU2025", "Saudi Arabian Grand Prix", "Jeddah", "SA", 
                LocalDate.of(2025, 3, 29), 50, 6.174));
        
        meetings.add(new Meeting("AUS2025", "Australian Grand Prix", "Melbourne", "AU", 
                LocalDate.of(2025, 4, 5), 58, 5.278));
        
        meetings.add(new Meeting("JPN2025", "Japanese Grand Prix", "Suzuka", "JP", 
                LocalDate.of(2025, 4, 12), 53, 5.807));
        
        meetings.add(new Meeting("CHN2025", "Chinese Grand Prix", "Shanghai", "CN", 
                LocalDate.of(2025, 4, 19), 56, 5.451));
        
        return meetings;
    }

    private List<Driver> createSampleDrivers() {
        List<Driver> drivers = new ArrayList<>();
        
        drivers.add(new Driver("VER", "Max Verstappen", "VER", "Red Bull Racing", 
                1, 1, 350, "1:34.567", 212.5));
        
        drivers.add(new Driver("HAM", "Lewis Hamilton", "HAM", "Mercedes", 
                44, 2, 310, "1:34.890", 211.2));
        
        drivers.add(new Driver("LEC", "Charles Leclerc", "LEC", "Ferrari", 
                16, 3, 290, "1:35.123", 210.8));
        
        drivers.add(new Driver("NOR", "Lando Norris", "NOR", "McLaren", 
                4, 4, 244, "1:35.890", 209.7));
        
        drivers.add(new Driver("SAI", "Carlos Sainz", "SAI", "Ferrari", 
                55, 5, 236, "1:36.012", 208.9));
        
        return drivers;
    }

    private List<LapData> createSampleLapData() {
        List<LapData> lapDataList = new ArrayList<>();
        String[] compounds = {"Soft", "Medium", "Hard"};
        String[] driverIds = {"VER", "HAM", "LEC", "NOR", "SAI"};
        
        // Create sample lap data for each driver
        for (String driverId : driverIds) {
            for (int lap = 1; lap <= 10; lap++) {
                int position = (int) (Math.random() * 5) + 1;
                double speed = 205 + (Math.random() * 30);
                String compound = compounds[(int) (Math.random() * compounds.length)];
                
                // Generate random sector times (in milliseconds)
                int sector1 = 25000 + (int) (Math.random() * 5000);
                int sector2 = 35000 + (int) (Math.random() * 5000);
                int sector3 = 30000 + (int) (Math.random() * 5000);
                
                // Calculate total lap time
                int totalTime = sector1 + sector2 + sector3;
                int minutes = totalTime / 60000;
                double seconds = (totalTime % 60000) / 1000.0;
                String lapTime = String.format("%d:%06.3f", minutes, seconds);
                
                lapDataList.add(new LapData(
                        driverId, lap, lapTime, position, speed, compound, 
                        sector1, sector2, sector3
                ));
            }
        }
        
        return lapDataList;
    }
}