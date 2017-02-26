package com.example.naval.crime;

/**
 * Created by navalkashyap on 2/5/2017.
 */

public class WoodRed_incident {

    /**
     * case_number : c17006675
     * incident_datetime : 2017-02-07T17:00:00.000
     * incident_description : THEFT, AUTO
     * incident_type_primary : THEFT, AUTO
     * latitude : 47.7471242
     * longitude : -122.0942483
     */

    private String case_number;
    private String incident_datetime;
    private String incident_description;
    private String incident_type_primary;
    private String latitude;
    private String longitude;

    public String getCase_number() {
        return case_number;
    }

    public void setCase_number(String case_number) {
        this.case_number = case_number;
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

    public String getIncident_type_primary() {
        return incident_type_primary;
    }

    public void setIncident_type_primary(String incident_type_primary) {
        this.incident_type_primary = incident_type_primary;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
