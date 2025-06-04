package com.f1.dashboard.controller;

import com.f1.dashboard.model.Driver;
import com.f1.dashboard.model.LapData;
import com.f1.dashboard.model.Meeting;
import com.f1.dashboard.service.F1DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final F1DataService f1DataService;

    @Autowired
    public ApiController(F1DataService f1DataService) {
        this.f1DataService = f1DataService;
    }

    @GetMapping("/races")
    public ResponseEntity<List<Meeting>> getRaces() {
        return ResponseEntity.ok(f1DataService.getLastFiveRaces());
    }

    @GetMapping("/races/{raceId}/drivers")
    public ResponseEntity<List<Driver>> getDriversForRace(@PathVariable String raceId) {
        // In a real app, we would filter drivers by race ID
        return ResponseEntity.ok(f1DataService.getAllDrivers());
    }

    @GetMapping("/drivers")
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.ok(f1DataService.getAllDrivers());
    }

    @GetMapping("/drivers/{driverId}")
    public ResponseEntity<Driver> getDriver(@PathVariable String driverId) {
        List<Driver> drivers = f1DataService.getAllDrivers();
        
        // Find the driver with the matching ID
        for (Driver driver : drivers) {
            if (driver.getDriverId().equals(driverId)) {
                return ResponseEntity.ok(driver);
            }
        }
        
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/drivers/{driverId}/laps")
    public ResponseEntity<List<LapData>> getDriverLaps(@PathVariable String driverId) {
        List<LapData> allLaps = f1DataService.getAllLapData();
        
        // Filter laps for the specific driver
        List<LapData> driverLaps = allLaps.stream()
                .filter(lap -> lap.getDriverId().equals(driverId))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(driverLaps);
    }
}