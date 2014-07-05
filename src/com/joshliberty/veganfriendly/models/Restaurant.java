package com.joshliberty.veganfriendly.models;

import android.os.Parcel;
import android.os.Parcelable;
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

    @Column(name="Address")
    public String address;

    @Column(name="Cuisine")
    public String cuisine;

    @Column(name="Is_vegan")
    public boolean is_vegan;

    @Column(name="Is_recommended")
    public boolean is_recommended;

    @Column(name="Image_name")
    public String image_name;

    @Column(name="Image_fetched")
    public boolean image_fetched;

    @Column(name="Opening_times")
    public OpeningTime opening_times;

    @Column(name="Opening_time_id")
    public Integer opening_time;

    // Public API
    public LatLng getLocation(){
        return new LatLng(latitude, longitude);
    }

    // Getters and setters
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
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
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
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
    public boolean isIs_recommended() {
        return is_recommended;
    }
    public void setIs_recommended(boolean is_recommended) {
        this.is_recommended = is_recommended;
    }
    public String getImage_name() {
        return image_name;
    }
    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }
    public OpeningTime getOpening_times() {
        return opening_times;
    }
    public void setOpening_times(OpeningTime opening_times) {
        this.opening_times = opening_times;
    }
    public boolean isImage_fetched() {
        return image_fetched;
    }
    public void setImage_fetched(boolean image_fetched) {
        this.image_fetched = image_fetched;
    }
    public Integer getOpening_time() {
        return opening_time;
    }
    public void setOpening_time(Integer opening_time) {
        this.opening_time = opening_time;
    }
}
