package com.joshliberty.veganfriendly.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by caligula on 06/06/14.
 */
@Table(name = "restaurants")
public class Restaurant extends Model {

    @Column(name="Name", index = true)
    public String name;

    @Column(name="Phone")
    public String phone;

    @Column(name="Description")
    public String description;

    @Column(name="Latitude", index = true)
    public double latitude;

    @Column(name="Longitude", index = true)
    public double longitude;

    @Column(name="City")
    public String city;

    @Column(name="Street")
    public String street;

    @Column(name="Cuisine")
    public String cuisine;

    @Column(name="Is_vegan")
    public boolean is_vegan;

    @Column(name="Opening_times")
    public OpeningTime opening_times;

    // Public API
    public LatLng getLocation(){
        return new LatLng(latitude, longitude);
    }

    // Getters and setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public String getCuisine() {
        return cuisine;
    }
    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }
    public boolean isIs_vegan() {
        return is_vegan;
    }
    public void setIs_vegan(boolean is_vegan) {
        this.is_vegan = is_vegan;
    }
}
