package com.f1.dashboard.service;

import com.f1.dashboard.model.Driver;
import com.f1.dashboard.model.LapData;
import com.f1.dashboard.model.Meeting;

import java.util.List;

public interface F1DataService {
    
    List<Meeting> getLastFiveRaces();
    
    List<Driver> getAllDrivers();
    
    List<LapData> getAllLapData();
    
    Driver getDriverById(String driverId);
    
    List<LapData> getLapDataForDriver(String driverId);
}