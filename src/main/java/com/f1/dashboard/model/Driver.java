package com.f1.dashboard.model;

public class Driver {
    // Common fields for both implementations
    private String fullName;
    private String teamName;
    private int driverNumber;
    
    // Fields for API implementation
    private int meetingKey;
    private int sessionKey;
    private String broadcastName;
    private String countryCode;
    private String firstName;
    private String lastName;
    private String headshotUrl;
    private String nameAcronym;
    private String teamColor;
    
    // Fields for UI/Dashboard implementation
    private String driverId;
    private String shortName;
    private String team;
    private int carNumber;
    private int position;
    private int points;
    private String fastestLap;
    private double avgSpeed;

    public Driver() {
    }

    public Driver(String driverId, String fullName, String shortName, String team, int carNumber, int position, int points, String fastestLap, double avgSpeed) {
        this.driverId = driverId;
        this.fullName = fullName;
        this.shortName = shortName;
        this.team = team;
        this.carNumber = carNumber;
        this.position = position;
        this.points = points;
        this.fastestLap = fastestLap;
        this.avgSpeed = avgSpeed;
    }

    // Getters and setters for both implementations
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getBroadcastName() {
        return broadcastName;
    }

    public void setBroadcastName(String broadcastName) {
        this.broadcastName = broadcastName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getHeadshotUrl() {
        return headshotUrl;
    }

    public void setHeadshotUrl(String headshotUrl) {
        this.headshotUrl = headshotUrl;
    }

    public String getNameAcronym() {
        return nameAcronym;
    }

    public void setNameAcronym(String nameAcronym) {
        this.nameAcronym = nameAcronym;
    }

    public String getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(String teamColor) {
        this.teamColor = teamColor;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
        this.team = teamName; // Set both for compatibility
    }
    
    // Getters and setters for UI/Dashboard implementation
    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getShortName() {
        return shortName != null ? shortName : nameAcronym;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getTeam() {
        return team != null ? team : teamName;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public int getCarNumber() {
        return carNumber > 0 ? carNumber : driverNumber;
    }

    public void setCarNumber(int carNumber) {
        this.carNumber = carNumber;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getFastestLap() {
        return fastestLap;
    }

    public void setFastestLap(String fastestLap) {
        this.fastestLap = fastestLap;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }
}