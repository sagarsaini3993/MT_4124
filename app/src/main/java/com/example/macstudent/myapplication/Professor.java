package com.example.macstudent.myapplication;

import java.io.Serializable;

public class Professor implements Serializable {

    String name;
    double lat;
    double lng;

    public Professor(String name, double lat, double lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Professor{" +
                "name='" + name + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}


// Link to the instruction booklet: https://docs.google.com/document/d/1WbP23r1empSAcurIVFmf1wlFcW-3gtHd5tP6BBQwwaQ/edit#

