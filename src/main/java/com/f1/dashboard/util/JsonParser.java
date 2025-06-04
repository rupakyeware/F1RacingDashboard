package com.f1.dashboard.util;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.f1.dashboard.model.CarData;
import com.f1.dashboard.model.Driver;
import com.f1.dashboard.model.Interval;
import com.f1.dashboard.model.LapData;
import com.f1.dashboard.model.Location;
import com.f1.dashboard.model.Meeting;

/**
 * Utility class for parsing JSON responses from the OpenF1 API
 */
@Component
public class JsonParser {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonParser.class);
    
    public List<CarData> parseCarData(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return Collections.emptyList();
        }
        
        try {
            List<CarData> result = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(jsonString);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                CarData carData = new CarData();
                
                carData.setBrake(json.getInt("brake"));
                carData.setDate(OffsetDateTime.parse(json.getString("date")));
                carData.setDriverNumber(json.getInt("driver_number"));
                carData.setDrs(json.getInt("drs"));
                carData.setMeetingKey(json.getInt("meeting_key"));
                carData.setNGear(json.getInt("n_gear"));
                carData.setRpm(json.getInt("rpm"));
                carData.setSessionKey(json.getInt("session_key"));
                carData.setSpeed(json.getInt("speed"));
                carData.setThrottle(json.getInt("throttle"));
                
                result.add(carData);
            }
            
            return result;
        } catch (JSONException e) {
            logger.error("Error parsing car data JSON: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public List<Driver> parseDrivers(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return Collections.emptyList();
        }
        
        try {
            List<Driver> result = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(jsonString);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                Driver driver = new Driver();
                
                driver.setDriverNumber(json.getInt("driver_number"));
                driver.setSessionKey(json.getInt("session_key"));
                driver.setMeetingKey(json.getInt("meeting_key"));
                
                if (json.has("broadcast_name")) {
                    driver.setBroadcastName(json.getString("broadcast_name"));
                }
                
                if (json.has("country_code")) {
                    driver.setCountryCode(json.getString("country_code"));
                }
                
                if (json.has("first_name")) {
                    driver.setFirstName(json.getString("first_name"));
                }
                
                if (json.has("last_name")) {
                    driver.setLastName(json.getString("last_name"));
                }
                
                if (json.has("full_name")) {
                    driver.setFullName(json.getString("full_name"));
                }
                
                if (json.has("headshot_url")) {
                    driver.setHeadshotUrl(json.getString("headshot_url"));
                }
                
                if (json.has("name_acronym")) {
                    driver.setNameAcronym(json.getString("name_acronym"));
                }
                
                if (json.has("team_colour")) {
                    driver.setTeamColor(json.getString("team_colour"));
                }
                
                if (json.has("team_name")) {
                    driver.setTeamName(json.getString("team_name"));
                }
                
                result.add(driver);
            }
            
            return result;
        } catch (JSONException e) {
            logger.error("Error parsing drivers JSON: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public List<LapData> parseLapData(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return Collections.emptyList();
        }
        
        try {
            List<LapData> result = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(jsonString);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                LapData lapData = new LapData();
                
                lapData.setLapNumber(json.getInt("lap_number"));
                lapData.setDriverNumber(json.getInt("driver_number"));
                lapData.setSessionKey(json.getInt("session_key"));
                lapData.setMeetingKey(json.getInt("meeting_key"));
                
                if (json.has("date_start")) {
                    lapData.setDateStart(OffsetDateTime.parse(json.getString("date_start")));
                }
                
                if (json.has("lap_duration")) {
                    lapData.setLapDuration(json.getDouble("lap_duration"));
                }
                
                if (json.has("duration_sector_1")) {
                    lapData.setDurationSector1(json.getDouble("duration_sector_1"));
                }
                
                if (json.has("duration_sector_2")) {
                    lapData.setDurationSector2(json.getDouble("duration_sector_2"));
                }
                
                if (json.has("duration_sector_3")) {
                    lapData.setDurationSector3(json.getDouble("duration_sector_3"));
                }
                
                if (json.has("i1_speed")) {
                    lapData.setI1Speed(json.getInt("i1_speed"));
                }
                
                if (json.has("i2_speed")) {
                    lapData.setI2Speed(json.getInt("i2_speed"));
                }
                
                if (json.has("st_speed")) {
                    lapData.setStSpeed(json.getInt("st_speed"));
                }
                
                if (json.has("is_pit_out_lap")) {
                    lapData.setIsPitOutLap(json.getBoolean("is_pit_out_lap"));
                }
                
                if (json.has("segments_sector_1")) {
                    List<Integer> segments1 = new ArrayList<>();
                    JSONArray segArray = json.getJSONArray("segments_sector_1");
                    for (int j = 0; j < segArray.length(); j++) {
                        segments1.add(segArray.getInt(j));
                    }
                    lapData.setSegmentsSector1(segments1);
                }
                
                if (json.has("segments_sector_2")) {
                    List<Integer> segments2 = new ArrayList<>();
                    JSONArray segArray = json.getJSONArray("segments_sector_2");
                    for (int j = 0; j < segArray.length(); j++) {
                        segments2.add(segArray.getInt(j));
                    }
                    lapData.setSegmentsSector2(segments2);
                }
                
                if (json.has("segments_sector_3")) {
                    List<Integer> segments3 = new ArrayList<>();
                    JSONArray segArray = json.getJSONArray("segments_sector_3");
                    for (int j = 0; j < segArray.length(); j++) {
                        segments3.add(segArray.getInt(j));
                    }
                    lapData.setSegmentsSector3(segments3);
                }
                
                result.add(lapData);
            }
            
            return result;
        } catch (JSONException e) {
            logger.error("Error parsing lap data JSON: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public List<Meeting> parseMeetings(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return Collections.emptyList();
        }
        
        try {
            List<Meeting> result = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(jsonString);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                Meeting meeting = new Meeting();
                
                meeting.setMeetingKey(String.valueOf(json.getInt("meeting_key")));
                
                if (json.has("meeting_name")) {
                    meeting.setMeetingName(json.getString("meeting_name"));
                }
                
                if (json.has("meeting_official_name")) {
                    meeting.setMeetingOfficialName(json.getString("meeting_official_name"));
                }
                
                if (json.has("date_start")) {
                    meeting.setDateStart(OffsetDateTime.parse(json.getString("date_start")));
                }
                
                if (json.has("circuit_key")) {
                    meeting.setCircuitKey(json.getInt("circuit_key"));
                }
                
                if (json.has("circuit_name")) {
                    meeting.setCircuitName(json.getString("circuit_name"));
                }
                
                if (json.has("circuit_short_name")) {
                    meeting.setCircuitShortName(json.getString("circuit_short_name"));
                }
                
                // Extract year from date for searching
                meeting.extractYearFromDate();
                
                result.add(meeting);
            }
            
            return result;
        } catch (JSONException e) {
            logger.error("Error parsing meetings JSON: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public List<Interval> parseIntervals(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return Collections.emptyList();
        }
        
        try {
            List<Interval> result = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(jsonString);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                Interval interval = new Interval();
                
                interval.setDate(OffsetDateTime.parse(json.getString("date")));
                interval.setDriverNumber(json.getInt("driver_number"));
                interval.setMeetingKey(json.getInt("meeting_key"));
                interval.setSessionKey(json.getInt("session_key"));
                
                if (!json.isNull("gap_to_leader")) {
                    interval.setGapToLeader(json.getDouble("gap_to_leader"));
                }
                
                if (!json.isNull("interval")) {
                    interval.setInterval(json.getDouble("interval"));
                }
                
                result.add(interval);
            }
            
            return result;
        } catch (JSONException e) {
            logger.error("Error parsing intervals JSON: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public List<Location> parseLocations(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return Collections.emptyList();
        }
        
        try {
            List<Location> result = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(jsonString);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                Location location = new Location();
                
                location.setDate(OffsetDateTime.parse(json.getString("date")));
                location.setDriverNumber(json.getInt("driver_number"));
                location.setMeetingKey(json.getInt("meeting_key"));
                location.setSessionKey(json.getInt("session_key"));
                location.setX(json.getInt("x"));
                location.setY(json.getInt("y"));
                location.setZ(json.getInt("z"));
                
                result.add(location);
            }
            
            return result;
        } catch (JSONException e) {
            logger.error("Error parsing location JSON: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public List<Map<String, Object>> parseSessionsList(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return Collections.emptyList();
        }
        
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(jsonString);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                Map<String, Object> session = new java.util.HashMap<>();
                
                for (String key : json.keySet()) {
                    if (!json.isNull(key)) {
                        Object value = json.get(key);
                        if (key.contains("date") && value instanceof String) {
                            session.put(key, OffsetDateTime.parse((String) value));
                        } else {
                            session.put(key, value);
                        }
                    }
                }
                
                result.add(session);
            }
            
            return result;
        } catch (JSONException e) {
            logger.error("Error parsing sessions list JSON: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
