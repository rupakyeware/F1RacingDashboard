package com.f1.dashboard.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.f1.dashboard.model.CarData;
import com.f1.dashboard.model.Driver;
import com.f1.dashboard.model.LapData;
import com.f1.dashboard.model.Meeting;

@Service
public class F1DataService {
    private final RestTemplate restTemplate = new RestTemplate();

    public List<CarData> fetchCarData(int driverNumber, int sessionKey) {
        String url = "https://api.openf1.org/v1/car_data?driver_number=" + driverNumber + "&session_key=" + sessionKey;
        CarData[] response = restTemplate.getForObject(url, CarData[].class);
        return Arrays.asList(response != null ? response : new CarData[0]);
    }

    public List<Map<String, Object>> getLastFiveMeetings() {
        String url = "https://api.openf1.org/v1/meetings?year=2023&order=desc&limit=5";
        Meeting[] meetings = restTemplate.getForObject(url, Meeting[].class);
        return processMeetings(meetings);
    }

    public Map<String, Object> getDriverStats(int meetingKey, int driverNumber) {
        String url = String.format("https://api.openf1.org/v1/car_data?meeting_key=%d&driver_number=%d",
            meetingKey, driverNumber);
        CarData[] data = restTemplate.getForObject(url, CarData[].class);
        return calculateStats(data);
    }

    public List<LapData> getLapData(int meetingKey, int driverNumber) {
        String url = String.format("https://api.openf1.org/v1/laps?meeting_key=%d&driver_number=%d",
            meetingKey, driverNumber);
        LapData[] data = restTemplate.getForObject(url, LapData[].class);
        return Arrays.asList(data != null ? data : new LapData[0]);
    }

    public List<Map<String, Object>> getDrivers(int meetingKey) {
        String url = "https://api.openf1.org/v1/drivers?meeting_key=" + meetingKey;
        Driver[] drivers = restTemplate.getForObject(url, Driver[].class);
        return processDrivers(drivers);
    }

    private List<Map<String, Object>> processMeetings(Meeting[] meetings) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (meetings == null) return result;

        for (Meeting meeting : meetings) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("meeting_key", meeting.getMeetingKey());
            entry.put("meeting_name", meeting.getMeetingName());
            entry.put("date_start", meeting.getDateStart());
            result.add(entry);
        }
        return result;
    }

    private List<Map<String, Object>> processDrivers(Driver[] drivers) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (drivers == null) return result;

        for (Driver driver : drivers) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("driver_number", driver.getDriverNumber());
            entry.put("full_name", driver.getFullName());
            entry.put("team_name", driver.getTeamName());
            result.add(entry);
        }
        return result;
    }

    private Map<String, Object> calculateStats(CarData[] data) {
        Map<String, Object> stats = new HashMap<>();
        if (data == null || data.length == 0) return stats;

        double totalSpeed = 0;
        int maxSpeed = 0;
        double totalRpm = 0;
        int lapCount = 0;

        for (CarData entry : data) {
            totalSpeed += entry.getSpeed();
            totalRpm += entry.getRpm();
            if (entry.getSpeed() > maxSpeed) maxSpeed = entry.getSpeed();
            if (entry.getLapNumber() > lapCount) lapCount = entry.getLapNumber();
        }

        stats.put("avg_speed", totalSpeed / data.length);
        stats.put("max_speed", maxSpeed);
        stats.put("avg_rpm", totalRpm / data.length);
        stats.put("total_laps", lapCount);

        return stats;
    }
}