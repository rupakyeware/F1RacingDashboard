package com.f1.dashboard.model;

import java.time.OffsetDateTime;

public class Interval {
    private OffsetDateTime date;
    private int driverNumber;
    private int meetingKey;
    private int sessionKey;
    private double gapToLeader;
    private double interval;
    
    public Interval() {
    }
    
    public OffsetDateTime getDate() {
        return date;
    }
    
    public void setDate(OffsetDateTime date) {
        this.date = date;
    }
    
    public int getDriverNumber() {
        return driverNumber;
    }
    
    public void setDriverNumber(int driverNumber) {
        this.driverNumber = driverNumber;
    }
    
    public int getMeetingKey() {
        return meetingKey;
    }
    
    public void setMeetingKey(int meetingKey) {
        this.meetingKey = meetingKey;
    }
    
    public int getSessionKey() {
        return sessionKey;
    }
    
    public void setSessionKey(int sessionKey) {
        this.sessionKey = sessionKey;
    }
    
    public double getGapToLeader() {
        return gapToLeader;
    }
    
    public void setGapToLeader(double gapToLeader) {
        this.gapToLeader = gapToLeader;
    }
    
    public double getInterval() {
        return interval;
    }
    
    public void setInterval(double interval) {
        this.interval = interval;
    }
}