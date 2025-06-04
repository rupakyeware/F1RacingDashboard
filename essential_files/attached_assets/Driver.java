package com.f1.dashboard.model;

public class Driver {
    private int driver_number;
    private String full_name;
    private String team_name;

    // Getters and Setters
    public int getDriverNumber() { return driver_number; }
    public void setDriverNumber(int driver_number) { this.driver_number = driver_number; }
    public String getFullName() { return full_name; }
    public void setFullName(String full_name) { this.full_name = full_name; }
    public String getTeamName() { return team_name; }
    public void setTeamName(String team_name) { this.team_name = team_name; }
}