package com.f1.dashboard.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.f1.dashboard.model.Driver;
import com.f1.dashboard.model.Interval;
import com.f1.dashboard.model.LapData;
import com.f1.dashboard.model.Location;
import com.f1.dashboard.model.Meeting;

/**
 * Service to manage caching of F1 data
 * Reduces API calls and improves performance
 */
@Service
public class DataCacheService {
    
    private static final Logger logger = LoggerFactory.getLogger(DataCacheService.class);
    
    // Cache collections
    private List<Meeting> lastFiveMeetings = new ArrayList<>();
    private final Map<Integer, List<Meeting>> meetingsByYear = new HashMap<>();
    private final Map<String, List<LapData>> lapDataCache = new HashMap<>();
    private final Map<String, List<Driver>> driversCache = new HashMap<>();
    private final Map<String, List<Interval>> intervalsCache = new HashMap<>();
    private final Map<String, List<Location>> locationCache = new HashMap<>();
    
    // Time when cache was last updated
    private final Map<String, Long> cacheTimestamps = new HashMap<>();
    
    // Cache durations in milliseconds
    private static final long MEETINGS_CACHE_DURATION = TimeUnit.HOURS.toMillis(24);
    private static final long RACE_DATA_CACHE_DURATION = TimeUnit.MINUTES.toMillis(30);
    
    public List<Meeting> getLastFiveMeetings() {
        Long timestamp = cacheTimestamps.get("lastFiveMeetings");
        if (timestamp == null || System.currentTimeMillis() - timestamp > MEETINGS_CACHE_DURATION) {
            return Collections.emptyList();
        }
        return lastFiveMeetings;
    }
    
    public void cacheLastFiveMeetings(List<Meeting> meetings) {
        this.lastFiveMeetings = meetings;
        cacheTimestamps.put("lastFiveMeetings", System.currentTimeMillis());
    }
    
    public List<Meeting> getMeetingsByYear(int year) {
        Long timestamp = cacheTimestamps.get("meetingsByYear_" + year);
        if (timestamp == null || System.currentTimeMillis() - timestamp > MEETINGS_CACHE_DURATION) {
            return Collections.emptyList();
        }
        return meetingsByYear.getOrDefault(year, Collections.emptyList());
    }
    
    public void cacheMeetingsByYear(int year, List<Meeting> meetings) {
        this.meetingsByYear.put(year, meetings);
        cacheTimestamps.put("meetingsByYear_" + year, System.currentTimeMillis());
    }
    
    public List<LapData> getLapData(String cacheKey) {
        Long timestamp = cacheTimestamps.get("lapData_" + cacheKey);
        if (timestamp == null || System.currentTimeMillis() - timestamp > RACE_DATA_CACHE_DURATION) {
            return Collections.emptyList();
        }
        return lapDataCache.getOrDefault(cacheKey, Collections.emptyList());
    }
    
    public void cacheLapData(String cacheKey, List<LapData> laps) {
        this.lapDataCache.put(cacheKey, laps);
        cacheTimestamps.put("lapData_" + cacheKey, System.currentTimeMillis());
    }
    
    public List<Driver> getDrivers(String cacheKey) {
        Long timestamp = cacheTimestamps.get("drivers_" + cacheKey);
        if (timestamp == null || System.currentTimeMillis() - timestamp > RACE_DATA_CACHE_DURATION) {
            return Collections.emptyList();
        }
        return driversCache.getOrDefault(cacheKey, Collections.emptyList());
    }
    
    public void cacheDrivers(String cacheKey, List<Driver> drivers) {
        this.driversCache.put(cacheKey, drivers);
        cacheTimestamps.put("drivers_" + cacheKey, System.currentTimeMillis());
    }
    
    public List<Interval> getIntervals(String cacheKey) {
        Long timestamp = cacheTimestamps.get("intervals_" + cacheKey);
        if (timestamp == null || System.currentTimeMillis() - timestamp > RACE_DATA_CACHE_DURATION) {
            return Collections.emptyList();
        }
        return intervalsCache.getOrDefault(cacheKey, Collections.emptyList());
    }
    
    public void cacheIntervals(String cacheKey, List<Interval> intervals) {
        this.intervalsCache.put(cacheKey, intervals);
        cacheTimestamps.put("intervals_" + cacheKey, System.currentTimeMillis());
    }
    
    public List<Location> getLocationData(String cacheKey) {
        Long timestamp = cacheTimestamps.get("location_" + cacheKey);
        if (timestamp == null || System.currentTimeMillis() - timestamp > RACE_DATA_CACHE_DURATION) {
            return Collections.emptyList();
        }
        return locationCache.getOrDefault(cacheKey, Collections.emptyList());
    }
    
    public void cacheLocationData(String cacheKey, List<Location> locations) {
        this.locationCache.put(cacheKey, locations);
        cacheTimestamps.put("location_" + cacheKey, System.currentTimeMillis());
    }
    
    /**
     * Scheduled job to clean expired cache items
     * Runs every hour
     */
    @Scheduled(fixedRate = 3600000)
    public void cleanExpiredCache() {
        long currentTime = System.currentTimeMillis();
        
        // Clean meetings cache
        List<String> meetingsToRemove = new ArrayList<>();
        for (String key : cacheTimestamps.keySet()) {
            if (key.startsWith("meetings") && 
                currentTime - cacheTimestamps.get(key) > MEETINGS_CACHE_DURATION) {
                meetingsToRemove.add(key);
            }
        }
        
        for (String key : meetingsToRemove) {
            cacheTimestamps.remove(key);
            if (key.equals("lastFiveMeetings")) {
                lastFiveMeetings.clear();
            } else if (key.startsWith("meetingsByYear_")) {
                String yearStr = key.substring("meetingsByYear_".length());
                try {
                    int year = Integer.parseInt(yearStr);
                    meetingsByYear.remove(year);
                } catch (NumberFormatException e) {
                    logger.error("Invalid year in cache key: {}", yearStr);
                }
            }
        }
        
        // Clean race data cache
        List<String> raceDataToRemove = new ArrayList<>();
        for (String key : cacheTimestamps.keySet()) {
            if ((key.startsWith("lapData_") || key.startsWith("drivers_") ||
                 key.startsWith("intervals_") || key.startsWith("location_")) && 
                currentTime - cacheTimestamps.get(key) > RACE_DATA_CACHE_DURATION) {
                raceDataToRemove.add(key);
            }
        }
        
        for (String key : raceDataToRemove) {
            cacheTimestamps.remove(key);
            if (key.startsWith("lapData_")) {
                String cacheKey = key.substring("lapData_".length());
                lapDataCache.remove(cacheKey);
            } else if (key.startsWith("drivers_")) {
                String cacheKey = key.substring("drivers_".length());
                driversCache.remove(cacheKey);
            } else if (key.startsWith("intervals_")) {
                String cacheKey = key.substring("intervals_".length());
                intervalsCache.remove(cacheKey);
            } else if (key.startsWith("location_")) {
                String cacheKey = key.substring("location_".length());
                locationCache.remove(cacheKey);
            }
        }
        
        logger.info("Cleaned {} expired cache items", meetingsToRemove.size() + raceDataToRemove.size());
    }
}
