package com.joshliberty.veganfriendly.api;

import com.joshliberty.veganfriendly.models.Restaurant;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

import java.util.List;

/**
 * Created bv by caligula on 09/06/14.
 * This file is part of VeganFriendly.
 */
public interface ApiService {
    public static String API_SERVER = "http://10.0.0.9:5000";
    @GET("/restaurants/{latitude}/{longitude}")
    void getRestaurants(@Path("latitude") double latitude, @Path("longitude") double longitude, Callback<List<Restaurant>> cb);
}
