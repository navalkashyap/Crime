package com.example.naval.crime;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by navalkashyap on 2/5/2017.
 */

public class crime_incident {
    int id;
    int incidentType;
    double Latitude;
    double Longitude;
    double incidentTime;
    double entryTime;
    String Location;
    /**
     * address_1 : 10300 Block 133RD AVE NE
     * case_number : C17700055
     * city : KIRKLAND
     * clearance_type :
     * created_at : 2017-01-08T04:34:14.000
     * day_of_week : Wednesday
     * hour_of_day : 23
     * incident_datetime : 2016-06-01T23:10:00.000
     * incident_description : IDENTITY THEFT
     * incident_id : 788282832
     * incident_type_primary : IDENTITY THEFT
     * latitude : 47.6927449
     * location : {"type":"Point","coordinates":[-122.1635109,47.6927449]}
     * longitude : -122.1635109
     * parent_incident_type : Property Crime
     * state : WA
     * updated_at : 2017-01-21T06:30:19.000
     * zip : 98033
     */

    private String address_1;
    private String case_number;
    private String city;
    private String clearance_type;
    private String created_at;
    private String day_of_week;
    private String hour_of_day;
    private String incident_datetime;
    private String incident_description;
    private String incident_id;
    private String incident_type_primary;
    @SerializedName("latitude")
    private String latitudeX;
    @SerializedName("location")
    private LocationBean locationX;
    @SerializedName("longitude")
    private String longitudeX;
    private String parent_incident_type;
    private String state;
    private String updated_at;
    private String zip;

    public crime_incident(  int id, int iT, double lat,
                        double lng, double incTime,
                        String loc, double entTime) {
    this.id = id;
    this.incidentType = iT;
    this.Latitude = lat;
    this.Longitude = lng;
    this.incidentTime = incTime;
    this.Location = loc;
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
        return Latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public int getIncidentType() {
        return incidentType;
    }

    public String getLocation() {
        return Location;
    }

    public String getAddress_1() {
        return address_1;
    }

    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    public String getCase_number() {
        return case_number;
    }

    public void setCase_number(String case_number) {
        this.case_number = case_number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getClearance_type() {
        return clearance_type;
    }

    public void setClearance_type(String clearance_type) {
        this.clearance_type = clearance_type;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDay_of_week() {
        return day_of_week;
    }

    public void setDay_of_week(String day_of_week) {
        this.day_of_week = day_of_week;
    }

    public String getHour_of_day() {
        return hour_of_day;
    }

    public void setHour_of_day(String hour_of_day) {
        this.hour_of_day = hour_of_day;
    }

    public String getIncident_datetime() {
        return incident_datetime;
    }

    public void setIncident_datetime(String incident_datetime) {
        this.incident_datetime = incident_datetime;
    }

    public String getIncident_description() {
        return incident_description;
    }

    public void setIncident_description(String incident_description) {
        this.incident_description = incident_description;
    }

    public String getIncident_id() {
        return incident_id;
    }

    public void setIncident_id(String incident_id) {
        this.incident_id = incident_id;
    }

    public String getIncident_type_primary() {
        return incident_type_primary;
    }

    public void setIncident_type_primary(String incident_type_primary) {
        this.incident_type_primary = incident_type_primary;
    }

    public String getLatitudeX() {
        return latitudeX;
    }

    public void setLatitudeX(String latitudeX) {
        this.latitudeX = latitudeX;
    }

    public LocationBean getLocationX() {
        return locationX;
    }

    public void setLocationX(LocationBean locationX) {
        this.locationX = locationX;
    }

    public String getLongitudeX() {
        return longitudeX;
    }

    public void setLongitudeX(String longitudeX) {
        this.longitudeX = longitudeX;
    }

    public String getParent_incident_type() {
        return parent_incident_type;
    }

    public void setParent_incident_type(String parent_incident_type) {
        this.parent_incident_type = parent_incident_type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public static class LocationBean {
        /**
         * type : Point
         * coordinates : [-122.1635109,47.6927449]
         */

        private String type;
        private List<Double> coordinates;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Double> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Double> coordinates) {
            this.coordinates = coordinates;
        }
    }
}
