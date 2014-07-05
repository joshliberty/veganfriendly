package com.joshliberty.veganfriendly.api;

import com.joshliberty.veganfriendly.models.Restaurant;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

import java.io.File;
import java.util.List;

/**
 * Created bv by caligula on 09/06/14.
 * This file is part of VeganFriendly.
 */
public interface ApiService {
    public static String API_SERVER = "http://futuremeat.org:8080";
    public static String STATIC_PATH = "/static/";
    @GET("/restaurants/")
    void getRestaurants(Callback<List<Restaurant>> cb);
    @GET("/static/{filename}")
    void getImage(@Path("filename") String filename, Callback<File> cb);
}
