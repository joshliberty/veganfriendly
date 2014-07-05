package com.joshliberty.veganfriendly.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.sql.Time;

/**
 * Created by caligula on 09/06/14.
 * This file is part of VeganFriendly.
 */
@Table(name="opening_times")
public class OpeningTime extends Model {
    @Column(name="sunday_open")
    public String sunday_o;
    @Column(name="sunday_close")
    public String sunday_c;
    @Column(name="monday_open")
    public String monday_o;
    @Column(name="monday_close")
    public String monday_c;
    @Column(name="tuesday_open")
    public String tuesday_o;
    @Column(name="tuesday_close")
    public String tuesday_c;
    @Column(name="wednesday_open")
    public String wednesday_o;
    @Column(name="wednesday_close")
    public String wednesday_c;
    @Column(name="thursday_open")
    public String thursday_o;
    @Column(name="thursday_close")
    public String thursday_c;
    @Column(name="friday_open")
    public String friday_o;
    @Column(name="friday_close")
    public String friday_c;
    @Column(name="saturday_open")
    public String saturday_o;
    @Column(name="saturday_close")
    public String saturday_c;

    public String getSunday_o() {
        return sunday_o;
    }
    public void setSunday_o(String sunday_o) {
        this.sunday_o = sunday_o;
    }
    public String getSunday_c() {
        return sunday_c;
    }
    public void setSunday_c(String sunday_c) {
        this.sunday_c = sunday_c;
    }
    public String getMonday_o() {
        return monday_o;
    }
    public void setMonday_o(String monday_o) {
        this.monday_o = monday_o;
    }
    public String getMonday_c() {
        return monday_c;
    }
    public void setMonday_c(String monday_c) {
        this.monday_c = monday_c;
    }
    public String getTuesday_o() {
        return tuesday_o;
    }
    public void setTuesday_o(String tuesday_o) {
        this.tuesday_o = tuesday_o;
    }
    public String getTuesday_c() {
        return tuesday_c;
    }
    public void setTuesday_c(String tuesday_c) {
        this.tuesday_c = tuesday_c;
    }
    public String getWednesday_o() {
        return wednesday_o;
    }
    public void setWednesday_o(String wednesday_o) {
        this.wednesday_o = wednesday_o;
    }
    public String getWednesday_c() {
        return wednesday_c;
    }
    public void setWednesday_c(String wednesday_c) {
        this.wednesday_c = wednesday_c;
    }
    public String getThursday_o() {
        return thursday_o;
    }
    public void setThursday_o(String thursday_o) {
        this.thursday_o = thursday_o;
    }
    public String getThursday_c() {
        return thursday_c;
    }
    public void setThursday_c(String thursday_c) {
        this.thursday_c = thursday_c;
    }
    public String getFriday_o() {
        return friday_o;
    }
    public void setFriday_o(String friday_o) {
        this.friday_o = friday_o;
    }
    public String getFriday_c() {
        return friday_c;
    }
    public void setFriday_c(String friday_c) {
        this.friday_c = friday_c;
    }
    public String getSaturday_o() {
        return saturday_o;
    }
    public void setSaturday_o(String saturday_o) {
        this.saturday_o = saturday_o;
    }
    public String getSaturday_c() {
        return saturday_c;
    }
    public void setSaturday_c(String saturday_c) {
        this.saturday_c = saturday_c;
    }
}
