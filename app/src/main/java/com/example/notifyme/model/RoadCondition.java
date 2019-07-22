package com.example.notifyme.model;

public class RoadCondition {
    String cordinates;
    String message;


    public String getCordinates() {
        return cordinates;
    }

    public void setCordinates(String cordinates) {
        this.cordinates = cordinates;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RoadCondition(String message, String cordinates) {
        this.cordinates = cordinates;
        this.message = message;
    }
}
