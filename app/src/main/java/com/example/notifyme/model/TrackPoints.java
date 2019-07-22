package com.example.notifyme.model;

public class TrackPoints {
    String cordinates;
    String color;

    public String getCordinates() {
        return cordinates;
    }

    public void setCordinates(String cordinates) {
        this.cordinates = cordinates;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public TrackPoints(String color, String cordinates) {
        this.cordinates = cordinates;
        this.color = color;
    }
}
