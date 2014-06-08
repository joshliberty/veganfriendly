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
    public Time sundayOpen;
    @Column(name="sunday_close")
    public Time sundayClose;
    @Column(name="monday_open")
    public Time mondayOpen;
    @Column(name="monday_close")
    public Time mondayClose;
    @Column(name="tuesday_open")
    public Time tuesdayOpen;
    @Column(name="tuesday_close")
    public Time tuesdayClose;
    @Column(name="wednesday_open")
    public Time wednesdayOpen;
    @Column(name="wednesday_close")
    public Time wednesdayClose;
    @Column(name="thursday_open")
    public Time thursdayOpen;
    @Column(name="thursday_close")
    public Time thursdayClose;
    @Column(name="friday_open")
    public Time fridayOpen;
    @Column(name="friday_close")
    public Time fridayClose;
    @Column(name="saturday_open")
    public Time saturdayOpen;
    @Column(name="saturday_close")
    public Time saturdayClose;
}
