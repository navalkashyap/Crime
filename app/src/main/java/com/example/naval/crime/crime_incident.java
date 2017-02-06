package com.example.naval.crime;

/**
 * Created by navalkashyap on 2/5/2017.
 */

public class crime_incident {
    int id;
    int incidentType;
    double latitude;
    double longitude;
    double incidentTime;
    double entryTime;
    String location;
public crime_incident(  int id, int iT, double lat,
                        double lng, double incTime,
                        String loc, double entTime) {
    this.id = id;
    this.incidentType = iT;
    this.latitude = lat;
    this.longitude = lng;
    this.incidentTime = incTime;
    this.location = loc;
    this.entryTime = entTime;
    };

    public int getId() {
        return id;
    }

    public double getEntryTime() {
        return entryTime;
    }

    public double getIncidentTime() {
        return incidentTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getIncidentType() {
        return incidentType;
    }

    public String getLocation() {
        return location;
    }
}
