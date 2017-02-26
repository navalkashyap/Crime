package com.example.naval.crime;

/**
 * Created by navalkashyap on 2/24/2017.
 */

public class Seattle_incident {


    /**
     * at_scene_time : 2017-02-08T09:26:29.000
     * cad_event_number : 17000047025
     * initial_type_description : MOTOR VEHICLE COLLISION, REPORT ONLY - NON INJURY/NON BLOCKING
     * initial_type_group : TRAFFIC RELATED CALLS
     * latitude : 47.5861
     * longitude : -122.3342
     */

    private String at_scene_time;
    private Long   cad_event_number;
    private String initial_type_description;
    private String initial_type_group;
    private String latitude;
    private String longitude;

    public String getAt_scene_time() {
        return at_scene_time;
    }

    public void setAt_scene_time(String at_scene_time) {
        this.at_scene_time = at_scene_time;
    }

    public Long getCad_event_number() {
        return cad_event_number;
    }

    public void setCad_event_number(Long cad_event_number) {
        this.cad_event_number = cad_event_number;
    }

    public String getInitial_type_description() {
        return initial_type_description;
    }

    public void setInitial_type_description(String initial_type_description) {
        this.initial_type_description = initial_type_description;
    }

    public String getInitial_type_group() {
        return initial_type_group;
    }

    public void setInitial_type_group(String initial_type_group) {
        this.initial_type_group = initial_type_group;
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
