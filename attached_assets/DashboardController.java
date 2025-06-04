package com.f1.dashboard.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.f1.dashboard.model.CarData;
import com.f1.dashboard.model.LapData;
import com.f1.dashboard.service.F1DataService;

@RestController
@RequestMapping("/api")
public class DashboardController {
    private final F1DataService dataService;

    public DashboardController(F1DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/car-data/{driverNumber}/{sessionKey}")
    public List<CarData> getCarData(
            @PathVariable int driverNumber,
            @PathVariable int sessionKey) {
        return dataService.fetchCarData(driverNumber, sessionKey);
    }

    @GetMapping("/meetings/last-five")
    public List<Map<String, Object>> getLastFiveMeetings() {
        return dataService.getLastFiveMeetings();
    }

    @GetMapping("/driver-stats")
    public Map<String, Object> getDriverStats(
            @RequestParam int meeting_key,
            @RequestParam int driver_number) {
        return dataService.getDriverStats(meeting_key, driver_number);
    }

    @GetMapping("/laps")
    public List<LapData> getLapData(
            @RequestParam int meeting_key,
            @RequestParam int driver_number) {
        return dataService.getLapData(meeting_key, driver_number);
    }

    @GetMapping("/drivers")
    public List<Map<String, Object>> getDrivers(@RequestParam int meeting_key) {
        return dataService.getDrivers(meeting_key);
    }
}