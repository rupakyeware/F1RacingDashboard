package com.f1.dashboard.model;

import java.time.OffsetDateTime;
import java.util.List;

public class LapData {
    // Fields for API implementation
    private int driverNumber;
    private int meetingKey;
    private int sessionKey;
    private OffsetDateTime dateStart;
    private double lapDuration;
    
    // Common fields
    private String driverId;
    private int lapNumber;
    private String lapTime;
    private int position;
    private double speed;
    private String compound;
    private int sector1Time;
    private int sector2Time;
    private int sector3Time;
    private double durationSector1;
    private double durationSector2;
    private double durationSector3;
    private int i1Speed;
    private int i2Speed;
    private int stSpeed;
    private boolean isPitOutLap;
    private List<Integer> segmentsSector1;
    private List<Integer> segmentsSector2;
    private List<Integer> segmentsSector3;

    public LapData() {
    }

    public LapData(String driverId, int lapNumber, String lapTime, int position, double speed, String compound, 
                  int sector1Time, int sector2Time, int sector3Time) {
        this.driverId = driverId;
        this.lapNumber = lapNumber;
        this.lapTime = lapTime;
        this.position = position;
        this.speed = speed;
        this.compound = compound;
        this.sector1Time = sector1Time;
        this.sector2Time = sector2Time;
        this.sector3Time = sector3Time;
    }
    
    // Getters and setters for API implementation
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
    
    public OffsetDateTime getDateStart() {
        return dateStart;
    }

    public void setDateStart(OffsetDateTime dateStart) {
        this.dateStart = dateStart;
    }

    public double getLapDuration() {
        return lapDuration;
    }

    public void setLapDuration(double lapDuration) {
        this.lapDuration = lapDuration;
    }

    // Getters and setters for common fields
    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public int getLapNumber() {
        return lapNumber;
    }

    public void setLapNumber(int lapNumber) {
        this.lapNumber = lapNumber;
    }

    public String getLapTime() {
        return lapTime;
    }

    public void setLapTime(String lapTime) {
        this.lapTime = lapTime;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getCompound() {
        return compound;
    }

    public void setCompound(String compound) {
        this.compound = compound;
    }

    public int getSector1Time() {
        return sector1Time;
    }

    public void setSector1Time(int sector1Time) {
        this.sector1Time = sector1Time;
    }

    public int getSector2Time() {
        return sector2Time;
    }

    public void setSector2Time(int sector2Time) {
        this.sector2Time = sector2Time;
    }

    public int getSector3Time() {
        return sector3Time;
    }

    public void setSector3Time(int sector3Time) {
        this.sector3Time = sector3Time;
    }

    public double getDurationSector1() {
        return durationSector1;
    }

    public void setDurationSector1(double durationSector1) {
        this.durationSector1 = durationSector1;
    }

    public double getDurationSector2() {
        return durationSector2;
    }

    public void setDurationSector2(double durationSector2) {
        this.durationSector2 = durationSector2;
    }

    public double getDurationSector3() {
        return durationSector3;
    }

    public void setDurationSector3(double durationSector3) {
        this.durationSector3 = durationSector3;
    }

    public int getI1Speed() {
        return i1Speed;
    }

    public void setI1Speed(int i1Speed) {
        this.i1Speed = i1Speed;
    }

    public int getI2Speed() {
        return i2Speed;
    }

    public void setI2Speed(int i2Speed) {
        this.i2Speed = i2Speed;
    }

    public int getStSpeed() {
        return stSpeed;
    }

    public void setStSpeed(int stSpeed) {
        this.stSpeed = stSpeed;
    }

    public boolean isPitOutLap() {
        return isPitOutLap;
    }

    public void setIsPitOutLap(boolean isPitOutLap) {
        this.isPitOutLap = isPitOutLap;
    }

    public List<Integer> getSegmentsSector1() {
        return segmentsSector1;
    }

    public void setSegmentsSector1(List<Integer> segmentsSector1) {
        this.segmentsSector1 = segmentsSector1;
    }

    public List<Integer> getSegmentsSector2() {
        return segmentsSector2;
    }

    public void setSegmentsSector2(List<Integer> segmentsSector2) {
        this.segmentsSector2 = segmentsSector2;
    }

    public List<Integer> getSegmentsSector3() {
        return segmentsSector3;
    }

    public void setSegmentsSector3(List<Integer> segmentsSector3) {
        this.segmentsSector3 = segmentsSector3;
    }
}