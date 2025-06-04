package com.f1.dashboard.model;

import java.time.OffsetDateTime;

public class Meeting {
    private int meeting_key;
    private String meeting_name;
    private OffsetDateTime date_start;

    // Getters and Setters
    public int getMeetingKey() { return meeting_key; }
    public void setMeetingKey(int meeting_key) { this.meeting_key = meeting_key; }
    public String getMeetingName() { return meeting_name; }
    public void setMeetingName(String meeting_name) { this.meeting_name = meeting_name; }
    public OffsetDateTime getDateStart() { return date_start; }
    public void setDateStart(OffsetDateTime date_start) { this.date_start = date_start; }
}