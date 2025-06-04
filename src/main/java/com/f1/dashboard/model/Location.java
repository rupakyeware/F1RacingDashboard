package com.f1.dashboard.model;

import java.time.OffsetDateTime;

public class Location {
    private OffsetDateTime date;
    private int driverNumber;
    private int meetingKey;
    private int sessionKey;
    private int x;
    private int y;
    private int z;
    
    public Location() {
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
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getZ() {
        return z;
    }
    
    public void setZ(int z) {
        this.z = z;
    }
}