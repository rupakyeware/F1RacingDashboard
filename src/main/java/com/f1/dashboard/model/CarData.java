package com.f1.dashboard.model;

import java.time.OffsetDateTime;

public class CarData {
    private int brake;
    private OffsetDateTime date;
    private int driverNumber;
    private int drs;
    private int meetingKey;
    private int nGear;
    private int rpm;
    private int sessionKey;
    private int speed;
    private int throttle;
    
    public CarData() {
    }
    
    public int getBrake() {
        return brake;
    }
    
    public void setBrake(int brake) {
        this.brake = brake;
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
    
    public int getDrs() {
        return drs;
    }
    
    public void setDrs(int drs) {
        this.drs = drs;
    }
    
    public int getMeetingKey() {
        return meetingKey;
    }
    
    public void setMeetingKey(int meetingKey) {
        this.meetingKey = meetingKey;
    }
    
    public int getNGear() {
        return nGear;
    }
    
    public void setNGear(int nGear) {
        this.nGear = nGear;
    }
    
    public int getRpm() {
        return rpm;
    }
    
    public void setRpm(int rpm) {
        this.rpm = rpm;
    }
    
    public int getSessionKey() {
        return sessionKey;
    }
    
    public void setSessionKey(int sessionKey) {
        this.sessionKey = sessionKey;
    }
    
    public int getSpeed() {
        return speed;
    }
    
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    public int getThrottle() {
        return throttle;
    }
    
    public void setThrottle(int throttle) {
        this.throttle = throttle;
    }
}