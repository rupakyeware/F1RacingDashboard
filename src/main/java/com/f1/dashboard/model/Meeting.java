package com.f1.dashboard.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class Meeting {
    private String meetingKey;
    private String meetingName;
    private String meetingOfficialName;
    private String location;
    private String countryCode;
    private LocalDate raceDate;
    private OffsetDateTime dateStart;
    private int totalLaps;
    private double trackLength;
    private int circuitKey;
    private String circuitName;
    private String circuitShortName;
    
    public Meeting() {
    }

    public Meeting(String meetingKey, String meetingName, String location, String countryCode, LocalDate raceDate, int totalLaps, double trackLength) {
        this.meetingKey = meetingKey;
        this.meetingName = meetingName;
        this.location = location;
        this.countryCode = countryCode;
        this.raceDate = raceDate;
        this.totalLaps = totalLaps;
        this.trackLength = trackLength;
    }

    public String getMeetingKey() {
        return meetingKey;
    }

    public void setMeetingKey(String meetingKey) {
        this.meetingKey = meetingKey;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }
    
    public String getMeetingOfficialName() {
        return meetingOfficialName;
    }

    public void setMeetingOfficialName(String meetingOfficialName) {
        this.meetingOfficialName = meetingOfficialName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public LocalDate getRaceDate() {
        return raceDate;
    }

    public void setRaceDate(LocalDate raceDate) {
        this.raceDate = raceDate;
    }
    
    public OffsetDateTime getDateStart() {
        return dateStart;
    }

    public void setDateStart(OffsetDateTime dateStart) {
        this.dateStart = dateStart;
    }

    public int getTotalLaps() {
        return totalLaps;
    }

    public void setTotalLaps(int totalLaps) {
        this.totalLaps = totalLaps;
    }

    public double getTrackLength() {
        return trackLength;
    }

    public void setTrackLength(double trackLength) {
        this.trackLength = trackLength;
    }
    
    public int getCircuitKey() {
        return circuitKey;
    }

    public void setCircuitKey(int circuitKey) {
        this.circuitKey = circuitKey;
    }

    public String getCircuitName() {
        return circuitName;
    }

    public void setCircuitName(String circuitName) {
        this.circuitName = circuitName;
    }

    public String getCircuitShortName() {
        return circuitShortName;
    }

    public void setCircuitShortName(String circuitShortName) {
        this.circuitShortName = circuitShortName;
    }
    
    public int extractYearFromDate() {
        return raceDate != null ? raceDate.getYear() : (dateStart != null ? dateStart.getYear() : 0);
    }
}