package com.f1.dashboard.model;

import java.time.OffsetDateTime;

public class LapData {
    private int lap_number;
    private OffsetDateTime date_start;
    private double lap_duration;

    // Getters and Setters
    public int getLapNumber() { return lap_number; }
    public void setLapNumber(int lap_number) { this.lap_number = lap_number; }
    public OffsetDateTime getDateStart() { return date_start; }
    public void setDateStart(OffsetDateTime date_start) { this.date_start = date_start; }
    public double getLapDuration() { return lap_duration; }
    public void setLapDuration(double lap_duration) { this.lap_duration = lap_duration; }
}