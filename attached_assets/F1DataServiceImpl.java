package com.f1.dashboard.service;

import com.f1.dashboard.model.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class F1DataServiceImpl implements F1DataService {
    
    private final Map<Integer, Meeting> meetings = new HashMap<>();
    private final Map<Integer, List<Driver>> drivers = new HashMap<>();
    private final Map<String, List<LapData>> lapData = new HashMap<>();

    public F1DataServiceImpl() {
        // Dubai 2023 Race Data
        initializeDubai2023Data();
    }

    private void initializeDubai2023Data() {
        // Meeting
        Meeting dubai2023 = new Meeting();
        dubai2023.setMeetingKey(1);
        dubai2023.setMeetingName("2023 Dubai Grand Prix");
        dubai2023.setLocation("Dubai Autodrome");
        dubai2023.setCountry("UAE");
        meetings.put(1, dubai2023);

        // Drivers
        List<Driver> dubaiDrivers = Arrays.asList(
            new Driver(44, "Lewis Hamilton", "Mercedes"),
            new Driver(33, "Max Verstappen", "Red Bull"),
            new Driver(16, "Charles Leclerc", "Ferrari")
        );
        drivers.put(1, dubaiDrivers);

        // Lap Data
        List<LapData> laps = new ArrayList<>();
        for(int i = 1; i <= 50; i++) {
            LapData lap = new LapData();
            lap.setLapNumber(i);
            lap.setLapTime(1.45 + (Math.random() * 0.5));
            lap.setTyreCompound(i <= 15 ? "Soft" : i <= 35 ? "Medium" : "Hard");
            laps.add(lap);
        }
        lapData.put("1-44", laps); // Meeting 1, Driver 44
    }

    @Override
    public List<Meeting> getLastFiveMeetings() {
        return List.of(meetings.get(1)); // Only Dubai 2023 for demo
    }

    @Override
    public List<Driver> getDrivers(int meetingKey) {
        return drivers.getOrDefault(meetingKey, Collections.emptyList());
    }

    @Override
    public List<LapData> getLapData(int meetingKey, int driverNumber) {
        return lapData.getOrDefault(meetingKey + "-" + driverNumber, Collections.emptyList());
    }
    
    // Implement other methods
}