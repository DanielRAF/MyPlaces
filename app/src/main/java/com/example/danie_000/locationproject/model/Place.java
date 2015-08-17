package com.example.danie_000.locationproject.model;

/**
 * Created by danie_000 on 7/13/2015.
 */
public class Place {
    private String name, address, web, phone;
    private double lat, lon;

    public Place(String name, String address, double lat, double lon, String web, String phone) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.web = web;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
